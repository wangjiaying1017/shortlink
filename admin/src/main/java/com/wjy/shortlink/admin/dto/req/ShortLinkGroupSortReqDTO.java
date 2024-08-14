package com.wjy.shortlink.admin.dto.req;

import lombok.Data;

/*
* 分组请求排序参数
* */
@Data
public class ShortLinkGroupSortReqDTO {

    /*
    * 分组标识
    * */
    private String gid;
    /*
    *排序字段
    * */
    private Integer sortOrder;
}
