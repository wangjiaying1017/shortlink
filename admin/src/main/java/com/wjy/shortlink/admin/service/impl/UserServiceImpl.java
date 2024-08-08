package com.wjy.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.admin.common.convention.exception.ClientException;
import com.wjy.shortlink.admin.common.enums.UserErrorCode;
import com.wjy.shortlink.admin.dao.entity.UserDO;
import com.wjy.shortlink.admin.dao.mapper.UserMapper;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;
import com.wjy.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/*
用户接口层实现类
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> lambdaQueryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(lambdaQueryWrapper);

        if(userDO==null){
            throw new ClientException(UserErrorCode.USER_NOT_EXISTS);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);

        return userRespDTO;
    }
}
