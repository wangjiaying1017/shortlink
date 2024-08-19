package com.wjy.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.wjy.shortlink.project.common.constants.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/*
短链接工具类
 */
public class LinkUtil {
    /*
    获取短链接缓存有效期时间
     */

    public static long getLinkCacheValidDate(Date validDate){
        return Optional.ofNullable(validDate).map(each-> DateUtil.between(new Date(),each, DateUnit.MS)).orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
