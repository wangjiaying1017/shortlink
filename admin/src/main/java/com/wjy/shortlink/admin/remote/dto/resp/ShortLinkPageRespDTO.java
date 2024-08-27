package com.wjy.shortlink.admin.remote.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/*
*
* 短链接分页返回参数
* */
@Data
public class ShortLinkPageRespDTO {
    /*
    * 短链接id
    * */
    private Long id;
    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;


    /**
     * 分组标识
     */
    private String gid;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date validDate;


    /*
     * 创建时间
     * */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;


    /**
     * 描述
     */

    @TableField("`describe`")
    private String describe;

    /**
     * 历史Pv
     */
    private Integer totalPv;

    /**
     * 今日Pv
     */
    private Integer todayPv;

    /**
     * 历史Uv
     */
    private Integer totalUv;

    /**
     * 今日Uv
     */
    private Integer todayUv;

    /**
     * 历史ip
     */
    private Integer totalUip;

    /**
     * 今日ip
     */
    private Integer todayUip;
}
