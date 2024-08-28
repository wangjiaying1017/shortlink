package com.wjy.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.common.convention.result.Result;
import com.wjy.shortlink.project.common.convention.result.Results;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsAccessReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import com.wjy.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 *
 */
@RequiredArgsConstructor
@RestController
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    /*
    * 单个短链接统计数据
    * */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam){
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }
    /*
    * 单个短链接访问日志统计
    * */
    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessReqDTO requestParam){
        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }

    /**
     * 分组短链接数据监控
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkStatsReqDTO requestParam){
        return Results.success(shortLinkStatsService.groupShortLinkStats(requestParam));
    }
}
