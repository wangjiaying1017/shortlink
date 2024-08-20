package com.wjy.shortlink.project.dto.req;

/*
* 回收站恢复功能请求参数
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
