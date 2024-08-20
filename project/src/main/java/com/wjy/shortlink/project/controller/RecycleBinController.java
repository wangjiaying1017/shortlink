package com.wjy.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.common.convention.result.Result;
import com.wjy.shortlink.project.common.convention.result.Results;
import com.wjy.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.wjy.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
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

    /**
     * 新增回收站
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站数据
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam){
        return Results.success(recycleBinService.pageRecycleBin(requestParam));
    }


    /**
     * 恢复回收站数据
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

}
