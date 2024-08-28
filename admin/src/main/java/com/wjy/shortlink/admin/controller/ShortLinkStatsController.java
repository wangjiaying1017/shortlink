package com.wjy.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkStatsController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){

    };
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam){
        return shortLinkRemoteService.shortLinkStats(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkStatsReqDTO requestParam){
        return shortLinkRemoteService.groupShortLinkStats(requestParam);
    }


    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsReqDTO requestParam){
        return shortLinkRemoteService.shortLinkStatsAccessRecord(requestParam);
    }
}
