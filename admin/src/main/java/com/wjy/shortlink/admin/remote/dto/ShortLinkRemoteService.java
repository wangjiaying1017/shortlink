package com.wjy.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 短链接中台远程调用服务
* */
public interface ShortLinkRemoteService {
    /*
    * 创建短链接
    * */
    default  Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));

        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }


/*
* 分页查询短链接
* */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);

        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }


    /*
     * 查询分组短链接总量
     * */
    default Result<List<ShortLinkCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("requestParam",requestParam);

        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", requestMap);

        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

}
