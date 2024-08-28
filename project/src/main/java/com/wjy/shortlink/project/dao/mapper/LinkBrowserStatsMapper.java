package com.wjy.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.wjy.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkStatsBrowserRespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 获取浏览器数据持久层
 */

@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("INSERT INTO t_link_browser_stats ( full_short_url, gid, date, cnt, browser, create_time, update_time, del_flag )" +
            "VALUES(#{linkBrowserStats.fullShortUrl}, #{linkBrowserStats.gid}, #{linkBrowserStats.date}, #{linkBrowserStats.cnt}, #{linkBrowserStats.browser}, NOW(), NOW(), 0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkBrowserStats.cnt};")
    void shortLinkStats(@Param("linkBrowserStats") LinkBrowserStatsDO requestParam);

    /**
     * 获取指定链接的浏览器
     * @param requestParam
     * @return
     */
    @Select("SELECT browser,sum(cnt) as cnt\n" +
            "FROM t_link_browser_stats\n" +
            "WHERE date BETWEEN #{param.startDate} AND #{param.endDate}\n" +
            "AND gid = #{param.gid}\n" +
            "AND full_short_url = #{param.fullShortUrl}\n" +
            "GROUP BY full_short_url,gid,browser\n")
    List<ShortLinkStatsBrowserRespDTO> shortLinkLocaleStatByBrowser(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 分组获取指定链接的浏览器
     * @param requestParam
     * @return
     */
    @Select("SELECT browser,sum(cnt) as cnt\n" +
            "FROM t_link_browser_stats\n" +
            "WHERE date BETWEEN #{param.startDate} AND #{param.endDate}\n" +
            "AND gid = #{param.gid}\n" +
            "GROUP BY gid,browser\n")
    List<ShortLinkStatsBrowserRespDTO> groupShortLinkLocaleStatByBrowser(@Param("param") ShortLinkStatsReqDTO requestParam);
}
