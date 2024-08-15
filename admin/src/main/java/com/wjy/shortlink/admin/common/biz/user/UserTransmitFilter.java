package com.wjy.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.wjy.shortlink.admin.common.convention.exception.ClientException;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static com.wjy.shortlink.admin.common.enums.UserErrorCode.USER_TOKEN_FAIL;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private static final List<String> ignore_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"

    );
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if(!ignore_URI.contains(requestURI)){
            String method = httpServletRequest.getMethod();
            if(!(Objects.equals(requestURI,"/api/short-link/admin/v1/user")&&Objects.equals(method,"POST"))){
                    String username = httpServletRequest.getHeader("username");
                    String token = httpServletRequest.getHeader("token");
                    if(!StrUtil.isAllNotBlank(username,token)){
                        getOutException((HttpServletResponse) servletResponse, Results.failure(new ClientException(USER_TOKEN_FAIL)));
                        return;
                    }
                    Object userInfo = null;
                    try {
                        userInfo = stringRedisTemplate.opsForHash().get("login_" + username, token);
                        if(userInfo==null){
                            getOutException((HttpServletResponse) servletResponse, Results.failure(new ClientException(USER_TOKEN_FAIL)));
                            return;
                        }
                    }catch (Exception ex){
                        getOutException((HttpServletResponse) servletResponse, Results.failure(new ClientException(USER_TOKEN_FAIL)));
                        return;
                    }
                    if(userInfo!=null){
                        UserInfoDTO userInfoDTO = JSON.parseObject(userInfo.toString(), UserInfoDTO.class);
                        UserContext.setUser(userInfoDTO);
                    }

            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            UserContext.removeUser();
        }
        }

    private void getOutException(HttpServletResponse response, Result r) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String jsonObject = JSON.toJSONString(r);
        out.println(jsonObject);
        out.flush();
        out.close();
    }

}
