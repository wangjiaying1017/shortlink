package com.wjy.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wjy.shortlink.admin.common.database.BaseDO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/*
* 用户持久层实体
*
* */
@TableName("t_user")
@Data
public class UserDO extends BaseDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;



    public UserDO() {}
}
