package com.wjy.shortlink.admin.remote.dto.resp;

import lombok.Data;

@Data
public class ShortLinkStatsLocaleCNRespDTO {
    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 地区
     */
    private String locale;

    /**
     * 占比
     */
    private Double ratio;
}
