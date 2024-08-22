package com.wjy.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.shortlink.project.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * IP地址转换持久层
 */
@Mapper
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    @Insert("INSERT INTO " +
            "t_link_locale_stats (full_short_url, date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt}")
    void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);
}
