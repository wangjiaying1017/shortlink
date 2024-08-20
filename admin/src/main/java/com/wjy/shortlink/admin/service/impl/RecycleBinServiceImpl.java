package com.wjy.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wjy.shortlink.admin.common.biz.user.UserContext;
import com.wjy.shortlink.admin.common.convention.exception.ServiceException;
import com.wjy.shortlink.admin.common.convention.result.Result;
import com.wjy.shortlink.admin.dao.entity.GroupDO;
import com.wjy.shortlink.admin.dao.mapper.GroupMapper;
import com.wjy.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.wjy.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.wjy.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.wjy.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){

    };
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOS = groupMapper.selectList(queryWrapper);
        if(CollUtil.isEmpty(groupDOS)){
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOS.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBin(requestParam);
    }
}
