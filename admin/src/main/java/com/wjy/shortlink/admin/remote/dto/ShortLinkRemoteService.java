package com.wjy.shortlink.admin.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.remote.dto.req.*;
import com.wjy.shortlink.admin.remote.dto.resp.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 短链接中台远程调用服务
* */
public interface ShortLinkRemoteService {

    /*
    根据url获取短链接标题
     */
    default Result<String> getTitleByUrl(String url){
        String result = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url="+url);
        return JSON.parseObject(result, new TypeReference<>() {
        });
    }


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


    default Result<Void> updateShortLink(ShortLinkUpdateReqDTO requestParam){
        String result = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(requestParam));
        return JSON.parseObject(result, new TypeReference<>() {
        });
    }


    default Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam){
        String result = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
        return JSON.parseObject(result, new TypeReference<>() {
        });
    }

    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();

        requestMap.put("gidList",requestParam.getGidList());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);

        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }


    default Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        String resultPageStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }



    default Result<Void> removeRecycleBin(RecycleBinRemoveReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        String resultPageStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }


    default Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        requestMap.put("startDate",requestParam.getStartDate());
        requestMap.put("endDate",requestParam.getEndDate());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }


    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        requestMap.put("startDate",requestParam.getStartDate());
        requestMap.put("endDate",requestParam.getEndDate());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());

        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
}
