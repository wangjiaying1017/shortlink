package com.wjy.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/*
* 回收站服务接口
* */
public interface RecycleBinService {

    /*
    * 回收站管理分页短链接
    * */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam);
}
