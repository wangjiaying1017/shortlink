package com.wjy.shortlink.admin.common.enums;

import com.wjy.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {


    USER_NOT_EXISTS("A00002","不存在该用户"),
    USER_EXIST("A00004","用户已存在");

    private final String code;

    private final String message;

    UserErrorCode(String code,String message){
        this.code = code;
        this.message = message;
    }



    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
