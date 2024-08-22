package com.wjy.shortlink.project.dao.mapper;

/**
 * 短链接访问日志功能持久层
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.shortlink.project.dao.entity.LinkAccessLogsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {
}
