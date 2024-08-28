package com.wjy.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsAccessDailyRespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 短链接监控统计持久层
 */
@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    @Insert("INSERT INTO t_link_access_stats ( full_short_url, gid, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag )" +
            "VALUES(#{linkAccessStats.fullShortUrl}, #{linkAccessStats.gid}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour},#{linkAccessStats.weekday}, NOW(), NOW(), 0 ) " +
            "ON DUPLICATE KEY UPDATE pv = pv + #{linkAccessStats.pv},uv = uv + #{linkAccessStats.uv},uip = uip + #{linkAccessStats.uip};")
    void shortLinkStats(@Param("linkAccessStats")LinkAccessStatsDO requestParam);

    /**
     * 获取指定短链接每天的基础监控数据
     */

    @Select("SELECT date,SUM(pv) as pv,SUM(uv) as uv,SUM(uip) as uip\n" +
            "FROM t_link_access_stats\n" +
            "WHERE create_time BETWEEN CONCAT(#{param.startDate},' 00:00:00') and CONCAT(#{param.endDate},' 23:59:59')\n" +
            "AND gid = #{param.gid}\n" +
            "AND full_short_url = #{param.fullShortUrl}\n" +
            "GROUP BY full_short_url,gid,date")
    List<ShortLinkStatsAccessDailyRespDTO> listDayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);


    /**
     * 获取指定短链接每个小时内的监控数据
     */
    @Select("SELECT `hour`,SUM(pv) as pv,SUM(uv) as uv,SUM(uip) as ip\n" +
            "FROM t_link_access_stats\n" +
            "WHERE create_time BETWEEN CONCAT(#{param.startDate},' 00:00:00') and CONCAT(#{param.endDate},' 23:59:59')\n" +
            "AND gid = #{param.gid}\n" +
            "AND full_short_url = #{param.fullShortUrl}\n" +
            "GROUP BY full_short_url,gid,`hour`")
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 获取指定短链接每周监控数据
     */
    @Select("SELECT weekday,SUM(pv) as pv,SUM(uv) as uv,SUM(uip) as ip\n" +
            "FROM t_link_access_stats\n" +
            "WHERE create_time BETWEEN CONCAT(#{param.startDate},' 00:00:00') and CONCAT(#{param.endDate},' 23:59:59')\n" +
            "AND gid = #{param.gid}\n" +
            "AND full_short_url = #{param.fullShortUrl}\n" +
            "GROUP BY full_short_url,gid,weekday")
    List<LinkAccessStatsDO> listWeekStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);




}
