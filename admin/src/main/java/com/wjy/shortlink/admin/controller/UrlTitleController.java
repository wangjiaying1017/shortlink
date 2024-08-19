package com.wjy.shortlink.admin.controller;

import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.remote.dto.ShortLinkRemoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
URL标题控制层
 */
@RestController

public class UrlTitleController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){

    };
    /*
    *根据URL获取对应网站的标题
    *
    * */

    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url")String url){
        return shortLinkRemoteService.getTitleByUrl(url);
    }
}
