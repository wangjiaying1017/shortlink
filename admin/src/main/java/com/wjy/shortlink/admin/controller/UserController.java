package com.wjy.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.dto.resp.UserActualRespDTO;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;
import com.wjy.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/*
用户管理控制层
*/
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
    * 根据用户名返回脱敏用户信息
    * */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO userRespDTO = userService.getUserByUsername(username);

        return Results.success(userRespDTO);

    }
    /*
    * 根据用户名返回无脱敏用户信息
    * */
    @GetMapping("/api/short-link/v1/user/actual/{username}")
    public Result<UserActualRespDTO> getActualUserByUsernameAndPassword(@PathVariable("username") String username){
        UserRespDTO userResp = userService.getUserByUsername(username);
        return Results.success(BeanUtil.toBean(userResp, UserActualRespDTO.class));
    }
}
