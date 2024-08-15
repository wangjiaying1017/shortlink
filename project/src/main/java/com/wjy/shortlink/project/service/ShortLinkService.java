package com.wjy.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
