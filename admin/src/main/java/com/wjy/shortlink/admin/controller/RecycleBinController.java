package com.wjy.shortlink.admin.controller;

import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.wjy.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
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
}
