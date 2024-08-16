package com.wjy.shortlink.admin.dto.resp;

import lombok.Data;
/*
*
* 短链接查询返回参数
*
* */
@Data
public class ShortLinkGroupRespDTO {


    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;


    /**
     * 分组排序
     */
    private Integer sortOrder;

    /*
    分组下有多少个短链接
     */

    private Integer shortLinkCount;

}
