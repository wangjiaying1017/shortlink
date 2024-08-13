package com.wjy.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.admin.dao.entity.GroupDO;
import com.wjy.shortlink.admin.dao.mapper.GroupMapper;
import com.wjy.shortlink.admin.service.GroupService;
import com.wjy.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .gid(gid)
                .build();



        baseMapper.insert(groupDO);
    }

    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, null);
        GroupDO hasGroupFlag = baseMapper.selectOne(eq);
        return hasGroupFlag ==null;
    }
}
