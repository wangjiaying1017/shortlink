package com.wjy.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.admin.dao.entity.UserDO;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;

/*用户接口层*/
public interface UserService extends IService<UserDO> {

    UserRespDTO getUserByUsername(String username);
}
