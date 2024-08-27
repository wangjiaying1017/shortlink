package com.wjy.shortlink.project.dao.mapper;

import com.wjy.shortlink.project.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 每日监控数据访问
 */
@Mapper
public interface LinkStatsTodayMapper {

    @Insert("INSERT INTO t_link_stats_today ( full_short_url, gid, date, today_pv, today_uv, today_ip_count,create_time, update_time, del_flag )" +
            "VALUES(#{linkStatsToday.fullShortUrl}, #{linkStatsToday.gid}, #{linkStatsToday.date}, #{linkStatsToday.todayPv}, #{linkStatsToday.todayUv},#{linkStatsToday.todayIpCount}, NOW(), NOW(), 0 ) " +
            "ON DUPLICATE KEY UPDATE today_pv = today_pv + #{linkStatsToday.todayPv},today_uv = today_uv + #{linkStatsToday.todayUv},today_ip_count = today_ip_count + #{linkStatsToday.todayIpCount};"

    )
    void statsToday(@Param("linkStatsToday")LinkStatsTodayDO linkStatsTodayDO);
}
