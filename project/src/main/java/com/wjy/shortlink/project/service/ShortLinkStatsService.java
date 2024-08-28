package com.wjy.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsAccessReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsGroupReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 分组获取短链接访问日志
     * @param requestParam
     * @return
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessReqDTO requestParam);

    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkStatsGroupReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
