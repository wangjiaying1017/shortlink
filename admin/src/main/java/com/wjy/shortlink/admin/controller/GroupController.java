package com.wjy.shortlink.admin.controller;

import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.wjy.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.wjy.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 短链接分组控制层
*
* */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/api/short-link/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO reqestParam){
        groupService.saveGroup(reqestParam.getName());
        return Results.success();
    }
    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        //System.out.println(UserContext.getUsername());
        return Results.success(groupService.listGroup());
    }

    @PutMapping("/api/short-link/v1/group")
    public Result<Void> update(@RequestBody ShortLinkGroupUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/v1/group")
    public Result<Void> delete(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }


}
