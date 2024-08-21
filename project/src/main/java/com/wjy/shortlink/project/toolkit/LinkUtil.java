package com.wjy.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import jakarta.servlet.http.HttpServletRequest;

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



    /**
     * 获取请求的 IP 地址
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }
}
