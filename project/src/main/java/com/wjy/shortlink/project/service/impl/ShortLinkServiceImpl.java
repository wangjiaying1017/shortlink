package com.wjy.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.project.common.convention.exception.ServiceException;
import com.wjy.shortlink.project.common.enums.ValiDateTypeEnum;
import com.wjy.shortlink.project.dao.entity.*;
import com.wjy.shortlink.project.dao.mapper.*;
import com.wjy.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.wjy.shortlink.project.service.ShortLinkService;
import com.wjy.shortlink.project.toolkit.HashUtil;
import com.wjy.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.wjy.shortlink.project.common.constants.RedisKeyConstant.*;
import static com.wjy.shortlink.project.common.constants.ShortLinkConstant.AMAP_REQUEST_IP;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter shortLinkCreateCachePenetrationBloomFilter;

    private final ShortLinkGotoMapper shortLinkGotoMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String shortlinkStatsLocaleAmapKey;

    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    private final LinkOsStatsMapper linkOsStatsMapper;

    private final LinkBrowserStatsMapper linkBrowserStatsMapper;

    private final LinkAccessLogsMapper linkAccessLogsMapper;

    private final LinkDeviceStatsMapper linkDeviceStatsMapper;

    private final LinkNetworkMapper linkNetworkMapper;

    /*
* 创建短链接
*
* */
    @Override
    @Transactional
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain()).append("/").append(suffix).toString();
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(suffix);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        }catch(DuplicateKeyException e){
            //TODO 已经误判的短链接如何处理
            //1.短链接确实真实存在缓存
            //2.短链接不一定存在缓存中
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class).eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if(hasShortLinkDO!=null){
                log.warn("短链接：{}重复入库",fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }

        }
        stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY,fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS);
        //一个短链接后缀在多个域名内可用
        shortLinkCreateCachePenetrationBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://"+shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }


    /*
    * 分组查询短链接
    * */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class);
        queryWrapper.eq(ShortLinkDO::getGid,requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag,0)
                .eq(ShortLinkDO::getEnableStatus,0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        IPage<ShortLinkPageRespDTO> shortLinkPageRespDTOs = resultPage.convert(
                each -> {
                    ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
                    result.setDomain("http://"+result.getDomain());
                    return result;
                }

        );

        return shortLinkPageRespDTOs;
    }

    /*
    查询短链接分组数量
     */
    @Override
    public List<ShortLinkCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO());
        queryWrapper.select("gid as gid, count(*) as shortLinkCount")
                .in("gid",requestParam)
                .eq("enable_status",0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkCountQueryRespDTO.class);

    }

    /*
    * 修改短链接
    * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
        if(hasShortLink == null){
            throw new ServiceException("短链接记录不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .clickNum(hasShortLink.getClickNum())
                .shortUri(hasShortLink.getShortUri())
                .gid(hasShortLink.getGid())
                .favicon(hasShortLink.getFavicon())
                .createdType(hasShortLink.getCreatedType())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if(Objects.equals(hasShortLink.getGid(),requestParam.getGid())){
            //执行修改操作
            LambdaUpdateWrapper<ShortLinkDO> shortLinkDOLambdaUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class);
            shortLinkDOLambdaUpdateWrapper.eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValiDateTypeEnum.PERMANET.getType()), ShortLinkDO::getValidDateType, null);
            baseMapper.update(shortLinkDO,shortLinkDOLambdaUpdateWrapper);
        }else{
            //先删后改
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class);
            updateWrapper.eq(ShortLinkDO::getFullShortUrl,requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid,hasShortLink.getGid())
                    .eq(ShortLinkDO::getDelFlag,0)
                    .eq(ShortLinkDO::getEnableStatus,0);
            baseMapper.delete(updateWrapper);
            shortLinkDO.setGid(requestParam.getGid());
            baseMapper.insert(shortLinkDO);

        }
    }

    /*
    * 短链接跳转
    * */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String serverName = request.getServerName();
        //serverName没有携带协议
        String fullShortUrl = serverName+"/"+shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(originalLink)){
            shortLinkStats(fullShortUrl,null,request,response);
            response.sendRedirect(originalLink);
            return;
        }
        boolean contains = shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if(!contains){
            response.sendRedirect("/page/notfound");
            return;
        }
        String gotoISNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(gotoISNullShortLink)){
            response.sendRedirect("/page/notfound");
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();;
        try{
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if(StrUtil.isNotBlank(originalLink)){
                shortLinkStats(fullShortUrl,null,request,response);
                response.sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> shortLinkGotoWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class);
            shortLinkGotoWrapper.eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl);
            ShortLinkGotoDO hasShortLinkGotoDO = shortLinkGotoMapper.selectOne(shortLinkGotoWrapper);
            if(hasShortLinkGotoDO == null){
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),"-",30, TimeUnit.MINUTES);
                response.sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, hasShortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);

            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if(shortLinkDO == null || shortLinkDO.getValidDate().before(new Date())){
                //过了有效期
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),"-",30, TimeUnit.MINUTES);
                response.sendRedirect("/page/notfound");
                return;
                }
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY,fullShortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS
                );

            shortLinkStats(fullShortUrl,shortLinkDO.getGid(),request,response);
            response.sendRedirect(shortLinkDO.getOriginUrl());
        }finally {
            lock.unlock();
        }
    }


    private void shortLinkStats(String fullShortUrl,String gid,HttpServletRequest request,HttpServletResponse response){
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = request.getCookies();
        AtomicReference<Object> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = ()->{
            uv.set(UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", (String) uv.get());
            uvCookie.setMaxAge(60*60*24*30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl,fullShortUrl.indexOf("/"),fullShortUrl.length()));
            response.addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, (String) uv.get());
        };
        try{
            if(ArrayUtil.isNotEmpty(cookies)){
                Arrays.stream(cookies)
                        .filter(each->Objects.equals(each.getName(),"uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each->{
                            uv.set(each);
                            //如果uvAdded为1，表示该元素成功添加到集合中，即集合中原本没有这个元素，它是一个新元素
                            //如果uvAdded为0，表示该元素已存在于集合中，未被重新添加，因此uvAdded返回0
                            Long uvAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, each);

                            uvFirstFlag.set(uvAdded!=null && uvAdded > 0L);
                        },addResponseCookieTask);
            }else{
                addResponseCookieTask.run();
            }
            String remoteAddr = LinkUtil.getIp(request);
            Long ipAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, remoteAddr);
            boolean uipFirstFlag = ipAdded!=null && ipAdded >0L;

            if(StrUtil.isBlank(gid)){
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);

                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            int hour = DateUtil.hour(new Date(), true);
            Week week = DateUtil.dayOfWeekEnum(new Date());
            int weekValue = week.getValue();
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(uvFirstFlag.get()?1:0)
                    .uip(uipFirstFlag?1:0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(new Date())
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("key",shortlinkStatsLocaleAmapKey);
            parameterMap.put("ip",remoteAddr);
            String localResult = HttpUtil.get(AMAP_REQUEST_IP, parameterMap);
            JSONObject jsonObject = JSON.parseObject(localResult);
            String infoCode = jsonObject.getString("infocode");
            LinkLocaleStatsDO linkLocaleStatsDO;
            String actualProvice = null;
            String actualCity = null;
            if(StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode,"10000")){
                String province = jsonObject.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                 linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .province(actualProvice = unknownFlag ? "未知" : province)
                        .adcode(unknownFlag ? "未知" : jsonObject.getString("adcode"))
                        .city(actualCity = unknownFlag ? "未知" : jsonObject.getString("city"))
                        .cnt(1)
                        .country("中国")
                        .fullShortUrl(fullShortUrl)
                        .date(new Date())
                         .gid(gid)
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }
            String os = LinkUtil.getOs(request);
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .cnt(1)
                    .date(new Date())
                    .os(os)
                    .build();
            linkOsStatsMapper.shortLinkStats(linkOsStatsDO);
            String browser = LinkUtil.getBrowser(request);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .browser(browser)
                    .cnt(1)
                    .date(new Date())
                    .build();
            linkBrowserStatsMapper.shortLinkStats(linkBrowserStatsDO);

            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .ip(remoteAddr)
                    .fullShortUrl(fullShortUrl)
                    .os(os)
                    .device(LinkUtil.getDevice(request))
                    .network(LinkUtil.getNetwork(request))
                    .locale("中国"+actualProvice+actualCity)
                    .browser(browser)
                    .gid(gid)
                    .user((String) uv.get())
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);

            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(LinkUtil.getDevice(request))
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .gid(gid)
                    .cnt(1)
                    .build();

            linkDeviceStatsMapper.shortLinkStats(linkDeviceStatsDO);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(LinkUtil.getNetwork(request))
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .gid(gid)
                    .cnt(1)
                    .build();
            linkNetworkMapper.shortLinkNetworkState(linkNetworkStatsDO);

        }catch (Throwable ex){
            log.error("短链接访问量统计异常:{}",ex);
        }

    }


    private String generateSuffix(ShortLinkCreateReqDTO requestParam){

       int customGenerateCount = 0;
       String shortUri;
        String domain = requestParam.getDomain();
        while(true){
           if(customGenerateCount > 10){
               throw  new ServiceException("短链接频繁生成，请稍后再试");
           }
           String originUrl = requestParam.getOriginUrl();
           originUrl+=System.currentTimeMillis();
           shortUri = HashUtil.hashToBase62(originUrl);
           if(!shortLinkCreateCachePenetrationBloomFilter.contains(domain+"/"+shortUri)){
               break;
           }
           customGenerateCount++;

       }

        return shortUri;
    }

    @SneakyThrows
    private String getFavicon(String url){
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;

    }
}
