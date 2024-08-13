package com.wjy.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.admin.dao.entity.UserDO;
import com.wjy.shortlink.admin.dto.req.UserLoginReqDTO;
import com.wjy.shortlink.admin.dto.req.UserRegisterReqDto;
import com.wjy.shortlink.admin.dto.req.UserUpdateReqDto;
import com.wjy.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;

/*用户接口层*/
public interface UserService extends IService<UserDO> {
/*
* 根据用户名查询用户信息
* */
    UserRespDTO getUserByUsername(String username);
/*
查询用户名是否存在
* */
    Boolean hasUsername(String username);

    /*
    * 用户注册
    * */
    void register(UserRegisterReqDto requestParam);


    /*
    * 根据用户名修改用户
    * */
    void update(UserUpdateReqDto requestParam);


    /*
    * 用户登录
    * */
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

    /*
    * 检查用户是否登录
    * */
    Boolean checkLogin(String username,String token);

    void logout(String username, String token);
}
