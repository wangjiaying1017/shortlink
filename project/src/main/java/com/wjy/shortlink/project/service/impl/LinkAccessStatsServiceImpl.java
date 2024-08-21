package com.wjy.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.wjy.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import com.wjy.shortlink.project.service.LinkAccessStatsService;
import org.springframework.stereotype.Service;

/**
 * 短链接监控业务接口实现层
 */
@Service
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStatsDO> implements LinkAccessStatsService {
    
}
