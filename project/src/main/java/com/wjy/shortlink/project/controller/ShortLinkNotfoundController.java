package com.wjy.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    短链接不存在跳转控制器
 */
@Controller
public class ShortLinkNotfoundController {

    @RequestMapping("/page/notfound")
    public String notfound(){
        return "notfound";
    }
}
