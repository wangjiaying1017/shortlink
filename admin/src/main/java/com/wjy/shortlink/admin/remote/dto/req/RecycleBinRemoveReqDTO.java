package com.wjy.shortlink.admin.remote.dto.req;

/*
* 回收站移除功能请求参数
*
* */

import lombok.Data;

@Data
public class RecycleBinRemoveReqDTO {
    /*
    *
    * 分组标识
    * */
    private String gid;

    /*
    * 全部短链接
    * */
    private String fullShortUrl;

}
