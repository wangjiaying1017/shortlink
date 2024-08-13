package com.wjy.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.admin.dao.entity.UserDO;
import com.wjy.shortlink.admin.dto.req.UserRegisterReqDto;
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

}
