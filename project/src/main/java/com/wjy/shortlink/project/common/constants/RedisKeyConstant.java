package com.wjy.shortlink.project.common.constants;

/*
* Redis key常量类
* */
public class RedisKeyConstant {

    /*
    * 短链接跳转前缀 key
    * */
    public static final String GOTO_SHORT_LINK_KEY = "short-link_goto_%s";

    /*
    * 短链接空值跳转 key
    * */
    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link_is-null_goto_%s";
    /*
    *
    * 短链接跳转锁前缀
    * */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link_lock_goto_%s";
}
