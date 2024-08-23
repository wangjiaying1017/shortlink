package com.wjy.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsLocaleCNRespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * IP地址转换持久层
 */
@Mapper
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    @Insert("INSERT INTO " +
            "t_link_locale_stats (full_short_url, gid,date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.gid},#{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt}")
    void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);


    /**
     * 获取指定链接的地区分组
     * @param requestParam
     * @return
     */
    @Select("SELECT province as locale,sum(cnt) as cnt\n" +
            "FROM t_link_locale_stats\n" +
            "WHERE date BETWEEN #{param.startDate} AND #{param.endDate}\n" +
            "AND gid = #{param.gid}\n" +
            "AND full_short_url = #{param.fullShortUrl}\n" +
            "GROUP BY full_short_url,gid,province\n")
    List<ShortLinkStatsLocaleCNRespDTO> shortLinkLocaleStatByProvince(@Param("param")ShortLinkStatsReqDTO requestParam);
}
