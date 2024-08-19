package com.wjy.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.project.common.convention.exception.ServiceException;
import com.wjy.shortlink.project.common.enums.ValiDateTypeEnum;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.wjy.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.wjy.shortlink.project.dao.mapper.ShortLinkMapper;
import com.wjy.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.wjy.shortlink.project.service.ShortLinkService;
import com.wjy.shortlink.project.toolkit.HashUtil;
import com.wjy.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wjy.shortlink.project.common.constants.RedisKeyConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter shortLinkCreateCachePenetrationBloomFilter;

    private final ShortLinkGotoMapper shortLinkGotoMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;
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
            response.sendRedirect(originalLink);
            return;
        }
        boolean contains = shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if(!contains){

            return;
        }
        String gotoISNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(gotoISNullShortLink)){
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();;
        try{
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if(StrUtil.isNotBlank(originalLink)){
                response.sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> shortLinkGotoWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class);
            shortLinkGotoWrapper.eq(ShortLinkGotoDO::getFullShortUrl,fullShortUrl);
            ShortLinkGotoDO hasShortLinkGotoDO = shortLinkGotoMapper.selectOne(shortLinkGotoWrapper);
            if(hasShortLinkGotoDO == null){
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),"-",30, TimeUnit.MINUTES);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, hasShortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);

            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if(shortLinkDO!=null){
                if(shortLinkDO.getValidDate()!=null && shortLinkDO.getValidDate().before(new Date())){
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY,fullShortUrl),"-",30, TimeUnit.MINUTES);
                    return;
                }
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY,fullShortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS);
                response.sendRedirect(shortLinkDO.getOriginUrl());
            }

        }finally {
            lock.unlock();
        }
    }


        //通过布隆过滤器判断当前短链接是否存在
        /*if(shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl)){

        }*/


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
}
