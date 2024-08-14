package com.wjy.shortlink.admin.config;

import com.wjy.shortlink.admin.common.biz.user.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class UserConfiguration {
    /*
    * 用户传递信息过滤器
    * */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalTransmitUserFilter(StringRedisTemplate stringRedisTemplate){
        FilterRegistrationBean<UserTransmitFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new UserTransmitFilter(stringRedisTemplate));
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(0);
        return filterFilterRegistrationBean;
    }

}
