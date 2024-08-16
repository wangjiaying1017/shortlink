package com.wjy.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.admin.common.biz.user.UserContext;
import com.wjy.shortlink.admin.dao.entity.GroupDO;
import com.wjy.shortlink.admin.dao.mapper.GroupMapper;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.wjy.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.wjy.shortlink.admin.service.GroupService;
import com.wjy.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
*
* 短链接分组接口实现层
* */
@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid = null;
        while(true){
            gid = RandomGenerator.generateRandom();
            if(hasGid(gid)){
                break;
            }
        }
        GroupDO groupDO = GroupDO.builder()
                .name(groupName)
                .username(UserContext.getUsername())
                .gid(gid)
                .sortOrder(0)
                .build();



        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(eq);
        return BeanUtil.copyToList(groupDOS,ShortLinkGroupRespDTO.class);
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class);
        updateWrapper.eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getGid,requestParam.getGid());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class);
        updateWrapper.eq(GroupDO::getDelFlag,0)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getGid,gid);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each ->{
            GroupDO groupDO = GroupDO
                    .builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class);
            updateWrapper.eq(GroupDO::getGid,each.getGid())
                    .eq(GroupDO::getDelFlag,0)
                    .eq(GroupDO::getUsername,UserContext.getUsername());
            baseMapper.update(groupDO,updateWrapper);
        });
    }

    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getSortOrder,0)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO hasGroupFlag = baseMapper.selectOne(eq);
        return hasGroupFlag ==null;
    }
}
