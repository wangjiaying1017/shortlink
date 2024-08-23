package com.wjy.shortlink.project.dao.mapper;

import com.wjy.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 根据短链接获取指定日期内访问设备监控数据
     */
    @Select("SELECT " +
            "    device, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_device_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, device;")
    List<LinkDeviceStatsDO> listDeviceStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
