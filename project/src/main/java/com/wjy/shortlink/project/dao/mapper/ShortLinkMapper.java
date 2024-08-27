package com.wjy.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    @Update("UPDATE t_link " +
            "set total_pv = total_pv + #{totalPv},total_uv = total_uv + #{totalUv},total_uip = total_uip + #{totalUip} " +
            "WHERE gid = #{gid} AND full_short_url = #{fullShortUrl}")
    void increateStats(
            @Param("totalPv") Integer totalPv,
            @Param("totalUv") Integer totalUv,
            @Param("totalUip") Integer totalUip,
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl
    );

    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO shortLinkPageReqDTO);
}
