package com.wjy.shortlink.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjy.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.wjy.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.wjy.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.wjy.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.wjy.shortlink.project.dao.mapper.*;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.*;
import com.wjy.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 短链接监控接口实现层
 */
@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkNetworkMapper linkNetworkMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;

    /**
     * 获取单个短链接监控数据
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        //基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> linkAccessStatsDOS = linkAccessStatsMapper.listDayStatsByShortLink(requestParam);
        //地区访问详情
        List<ShortLinkStatsLocaleCNRespDTO> shortLinkStatsLocaleCNRespDTOS = linkLocaleStatsMapper.shortLinkLocaleStatByProvince(requestParam);
        int localCntSum = shortLinkStatsLocaleCNRespDTOS.stream()
                .mapToInt(ShortLinkStatsLocaleCNRespDTO::getCnt)
                .sum();
        shortLinkStatsLocaleCNRespDTOS.forEach(each->{
            double ratio = (double)each.getCnt() / localCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            each.setRatio(actualRatio);


        });
        //小时访问详情
        List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        List<Integer> hourStats = new ArrayList<>();
        for(int i=0;i<24;i++){
            AtomicInteger hour = new AtomicInteger(i);
            Integer hourCnt = listHourStatsByShortLink.stream()
                    .filter(each -> Objects.equals(hour.get(), each.getHour()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }
        //高频访问Cnt
        List<ShortLinkStatsTopIpRespDTO> shortLinkStatsTopIpRespDTOList = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpShortLink = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
        listTopIpShortLink.forEach(each->{
            ShortLinkStatsTopIpRespDTO shortLinkStatsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            shortLinkStatsTopIpRespDTOList.add(shortLinkStatsTopIpRespDTO);
        });
        //每周统计
        List<LinkAccessStatsDO> weekDayStats = linkAccessStatsMapper.listWeekStatsByShortLink(requestParam);
        List<Integer> weekStats = new ArrayList<>();
        for(int i=1; i<8; i++){
            AtomicInteger weekday = new AtomicInteger(i);
            Integer weekDayCnt = weekDayStats.stream()
                    .filter(each -> Objects.equals(weekday.get(), each.getWeekday()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            weekStats.add(weekDayCnt);
        }
        //浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> shortLinkStatsBrowserRespDTOS = linkBrowserStatsMapper.shortLinkLocaleStatByBrowser(requestParam);
        int browserSum = shortLinkStatsBrowserRespDTOS.stream()
                .mapToInt(ShortLinkStatsBrowserRespDTO::getCnt)
                .sum();
        shortLinkStatsBrowserRespDTOS.forEach(each->{
            double ratio = (double)each.getCnt() / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            each.setRatio(actualRatio);
        });

        //操作系统访问
        List<HashMap<String, Object>> osStats = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        List<ShortLinkStatsOsRespDTO> osStatsResult = new ArrayList<>();
        int osSum = osStats.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        osStats.forEach(each->{
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO shortLinkStatsOsRespDTO = ShortLinkStatsOsRespDTO.builder()

                    .os(each.get("os").toString())
                    .cnt(Integer.parseInt( each.get("count").toString()))
                    .ratio(actualRatio)
                    .build();
            osStatsResult.add(shortLinkStatsOsRespDTO);
        });
        //访客类型
        HashMap<String, Object> uvTypeCntByShortLink = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
        List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
        Integer oldUserCnt = Integer.parseInt(uvTypeCntByShortLink.get("oldUserCnt").toString());
        Integer newUserCnt = Integer.parseInt(uvTypeCntByShortLink.get("newUserCnt").toString());
        Integer uvSum = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / uvSum;
        double ActualOldRatio  = Math.round(oldRatio * 100.0) / 100.0;
        double  newRatio = (double) newUserCnt / uvSum;
        double ActualNewRatio  = Math.round(newRatio * 100.0) / 100.0;
        ShortLinkStatsUvRespDTO newUser = ShortLinkStatsUvRespDTO.builder()
                .cnt(newUserCnt)
                .uvType("newUser")
                .ratio(ActualNewRatio)
                .build();
        ShortLinkStatsUvRespDTO oldUser = ShortLinkStatsUvRespDTO.builder()
                .cnt(oldUserCnt)
                .uvType("oldUser")
                .ratio(ActualOldRatio)
                .build();
        uvTypeStats.add(newUser);
        uvTypeStats.add(oldUser);

        //设备类型统计

        List<LinkDeviceStatsDO> linkDeviceStatsDOS = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
        List<ShortLinkStatsDeviceRespDTO> shortLinkStatsDeviceRespDTOS = new ArrayList<>();
        int deviceCntSum = linkDeviceStatsDOS.stream()
                .mapToInt(LinkDeviceStatsDO::getCnt)
                .sum();
        linkDeviceStatsDOS.forEach(each->{
            double ratio = (double)each.getCnt() / deviceCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO shortLinkStatsDeviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .device(each.getDevice())
                    .cnt(each.getCnt())
                    .ratio(actualRatio).build();
            shortLinkStatsDeviceRespDTOS.add(shortLinkStatsDeviceRespDTO);
        });

        //网络类型统计
        List<LinkNetworkStatsDO> linkNetworkStatsDOS = linkNetworkMapper.listNetworkStatsByShortLink(requestParam);
        List<ShortLinkStatsNetworkRespDTO> shortLinkStatsNetworkRespDTOList = new ArrayList<>();
        int networkSum = linkNetworkStatsDOS.stream()
                .mapToInt(LinkNetworkStatsDO::getCnt)
                .sum();
        linkNetworkStatsDOS.forEach(each->{
            double ratio = (double)each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO shortLinkStatsNetworkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .network(each.getNetwork())
                    .cnt(each.getCnt())
                    .ratio(actualRatio).build();
            shortLinkStatsNetworkRespDTOList.add(shortLinkStatsNetworkRespDTO);
        });

        return ShortLinkStatsRespDTO.builder()
                .browserStats(shortLinkStatsBrowserRespDTOS)
                .localeCnStats(shortLinkStatsLocaleCNRespDTOS)
                .topIpStats(shortLinkStatsTopIpRespDTOList)
                .networkStats(shortLinkStatsNetworkRespDTOList)
                .deviceStats(shortLinkStatsDeviceRespDTOS)
                .weekdayStats(weekStats)
                .daily(linkAccessStatsDOS)
                .uvTypeStats(uvTypeStats)
                .osStats(osStatsResult)
                .hourStats(hourStats)
                .browserStats(shortLinkStatsBrowserRespDTOS)
                .build();


    }

    /**
     * 分页获取短链接访问日志
     *
     * @param requestParam 分组获取短链接访问日志请求参数
     * @return
     */
    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsReqDTO requestParam) {
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getGid,requestParam.getGid())
                .eq(LinkAccessLogsDO::getFullShortUrl,requestParam.getFullShortUrl())
                .between(LinkAccessLogsDO::getCreateTime,requestParam.getStartDate(),requestParam.getEndDate())
                .eq(LinkAccessLogsDO::getDelFlag,0)
                .orderByDesc(LinkAccessLogsDO::getCreateTime);
        Page<LinkAccessLogsDO> page = new Page((int) requestParam.getCurrent(), (int) requestParam.getSize());
        Page<LinkAccessLogsDO> linkAccessLogsDOPage = linkAccessLogsMapper.selectPage(page, queryWrapper);
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsDOPage.convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .toList();
        List<Map<String, Object>> uvTypeList;
        if(userAccessLogsList != null){
            uvTypeList = linkAccessLogsMapper.selectGroupUvTypeByUsers(
                    requestParam.getGid(),
                    requestParam.getFullShortUrl(),
                    requestParam.getStartDate(),
                    requestParam.getEndDate(),
                    userAccessLogsList);
        } else {
            uvTypeList = null;
        }

        actualResult.getRecords().forEach(each->{
            String uvType = uvTypeList.stream()
                    .filter(item->Objects.equals(each.getUser(),item.get("user")))
                    .findFirst()
                    .map(item->item.get("uvType"))
                    .map(Object::toString)
                    .orElse("旧访客");
            each.setUvType(uvType);
        });
        return actualResult;

    }
}
