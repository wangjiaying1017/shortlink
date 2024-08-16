package com.wjy.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkCreateRespDTO {



    /**
     * 分组标识
     */
    private String gid;
    /*
    * 原始链接
    * */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;



}
