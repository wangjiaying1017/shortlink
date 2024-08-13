package com.wjy.shortlink.admin.controller;

import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.wjy.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
