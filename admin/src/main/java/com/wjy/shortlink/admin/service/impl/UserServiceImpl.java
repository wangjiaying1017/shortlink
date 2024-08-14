package com.wjy.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.admin.common.convention.exception.ClientException;
import com.wjy.shortlink.admin.common.enums.UserErrorCode;
import com.wjy.shortlink.admin.dao.entity.UserDO;
import com.wjy.shortlink.admin.dao.mapper.UserMapper;
import com.wjy.shortlink.admin.dto.req.UserLoginReqDTO;
import com.wjy.shortlink.admin.dto.req.UserRegisterReqDto;
import com.wjy.shortlink.admin.dto.req.UserUpdateReqDto;
import com.wjy.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.wjy.shortlink.admin.dto.resp.UserRespDTO;
import com.wjy.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wjy.shortlink.admin.common.constants.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.wjy.shortlink.admin.common.enums.UserErrorCode.USER_NAME_EXIST;
import static com.wjy.shortlink.admin.common.enums.UserErrorCode.USER_SAVE_ERROR;

/*
用户接口层实现类
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> lambdaQueryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(lambdaQueryWrapper);

        if(userDO==null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);

        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
        /*
        UserDO userDO = baseMapper.selectOne(Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username));
        return userDO != null;
        */
    }

    @Override
    public void register(UserRegisterReqDto requestParam) {
        System.out.println(requestParam);
        if(hasUsername(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST);
        }
        UserDO bean = BeanUtil.toBean(requestParam, UserDO.class);
        System.out.println(bean);
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY+requestParam.getUsername());
       try{
           if(lock.tryLock()){
               int insert = baseMapper.insert(bean);
               if(insert < 1){
                   throw new ClientException(USER_SAVE_ERROR);
               }
               userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
               return;
           }
           throw new ClientException(USER_NAME_EXIST);
       }finally {
           lock.unlock();
       }

    }

    @Override
    public void update(UserUpdateReqDto requestParam) {
        //TODO 验证当前用户名是否为登录用户
        LambdaQueryWrapper<UserDO> updateWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam,UserDO.class),updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDO::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(eq);
        if(userDO==null){
            throw new ClientException("用户不存在");
        }
        Boolean flag = stringRedisTemplate.hasKey("login_" + userLoginReqDTO.getUsername());
        if(flag!=null && flag){
            throw new ClientException("用户已登录");
        }
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+userLoginReqDTO.getUsername(),uuid,JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_"+userLoginReqDTO.getUsername(),30L,TimeUnit.DAYS);

        return new UserLoginRespDTO(uuid);
    }

    /*
    * 检查用户是否登录
    * */
    @Override
    public Boolean checkLogin(String username,String token) {
        Object remoteToken = stringRedisTemplate.opsForHash().get("login_" + username, token);
        return remoteToken!=null ;
    }

    @Override
    public void logout(String username, String token) {
        if(checkLogin(username,token)){
            stringRedisTemplate.delete("login_" + username);
            return;
        }
        throw new ClientException("用户token不存在或用户未登录");
    }
}
