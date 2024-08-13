package com.wjy.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.admin.dao.entity.GroupDO;
import com.wjy.shortlink.admin.dao.mapper.GroupMapper;
import com.wjy.shortlink.admin.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
*
* 短链接分组接口实现层
* */
@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

}
