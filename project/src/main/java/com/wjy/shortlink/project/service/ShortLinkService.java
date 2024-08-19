package com.wjy.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /*
    * 创建短链接
    *
    * */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /*
    *
    * 分组查询短链接
    * */

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /*
    *
    * 查询短链接分组数量
    * */

    List<ShortLinkCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /*
    * 修改短链接功能
    *
    * */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /*
    * 短链接跳转
    *
    * */
    void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response);
}
