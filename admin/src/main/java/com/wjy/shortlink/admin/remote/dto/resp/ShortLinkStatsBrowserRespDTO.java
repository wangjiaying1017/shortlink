package com.wjy.shortlink.admin.remote.dto.resp;

import lombok.Data;

@Data
public class ShortLinkStatsBrowserRespDTO {
    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 占比
     */
    private Double ratio;
}
