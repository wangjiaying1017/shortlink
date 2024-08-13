package com.wjy.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
* 用户登录接口返回参数
* */
@Data
@AllArgsConstructor
public class UserLoginRespDTO {

    private String token;
}
