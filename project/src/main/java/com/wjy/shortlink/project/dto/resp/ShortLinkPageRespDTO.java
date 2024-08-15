package com.wjy.shortlink.project.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/*
*
* 短链接分页返回参数
* */
@Data
public class ShortLinkPageRespDTO {
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
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;
}
