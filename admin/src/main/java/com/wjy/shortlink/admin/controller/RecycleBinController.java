package com.wjy.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.wjy.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
* 回收站管理控制层
* */
@RestController
public class RecycleBinController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){

    };
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkRemoteService.pageRecycleBin(requestParam).getData());
    }
}
