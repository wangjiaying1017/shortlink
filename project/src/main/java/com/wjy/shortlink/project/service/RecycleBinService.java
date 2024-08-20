package com.wjy.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dto.req.RecycleBinSaveReqDTO;

/*
* 回收站管理业务接口层
* */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /*
    * 新增回收站
    * */
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

}
