package com.wjy.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.common.convention.result.Result;
import com.wjy.shortlink.project.common.convention.result.Results;
import com.wjy.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.wjy.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
* 回收站控制层
*
* */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkPageReqDTO requestParam){
        return Results.success(recycleBinService.pageRecycleBin(requestParam));
    }

}
