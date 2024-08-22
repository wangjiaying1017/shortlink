package com.wjy.shortlink.project.dao.mapper;

import com.wjy.shortlink.project.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 设备统计持久层
 */
@Mapper
public interface LinkDeviceStatsMapper {
    @Insert("INSERT INTO t_link_device_stats ( full_short_url, gid, date, cnt, device, create_time, update_time, del_flag )" +
            "VALUES(#{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.gid}, #{linkDeviceStats.date}, #{linkDeviceStats.cnt}, #{linkDeviceStats.device}, NOW(), NOW(), 0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkDeviceStats.cnt};")
    void shortLinkStats(@Param("linkDeviceStats") LinkDeviceStatsDO requestParam);
}
