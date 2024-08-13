package com.wjy.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import com.wjy.shortlink.admin.dto.req.UserLoginReqDTO;
import com.wjy.shortlink.admin.dto.req.UserRegisterReqDto;
import com.wjy.shortlink.admin.dto.req.UserUpdateReqDto;
import com.wjy.shortlink.admin.dto.resp.UserActualRespDTO;
import com.wjy.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;
import com.wjy.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /*
    判断用户名是否已存在
    */
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasusername(@RequestParam("username")String username){

        return Results.success(!userService.hasUsername(username));
    }

    /*
    注册用户
    */
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDto requestParam){
        //System.out.println(requestParam);
        userService.register(requestParam);
        return Results.success();
    }

    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDto userUpdateReqDto){
        userService.update(userUpdateReqDto);
        return Results.success();
    }

    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO){
        UserLoginRespDTO login = userService.login(userLoginReqDTO);
        return Results.success(login);
    }


    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username")String username,@RequestParam("token")String token){

        return Results.success(userService.checkLogin(username,token));
    }
}
