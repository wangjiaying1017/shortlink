package com.wjy.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.common.convention.result.Result;
import com.wjy.shortlink.project.common.convention.result.Results;
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

    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam){
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

    @GetMapping("/api/short-link/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsReqDTO requestParam){
        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }
}
