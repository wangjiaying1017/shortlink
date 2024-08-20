package com.wjy.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dao.mapper.ShortLinkMapper;
import com.wjy.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.wjy.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.wjy.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.wjy.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.wjy.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.wjy.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.wjy.shortlink.project.common.constants.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;
import static com.wjy.shortlink.project.common.constants.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

/*
* 回收站管理接口实现层
*
* */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;
    /*
    * 新增回收站
    * */
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> shortLinkDOLambdaUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class);
        shortLinkDOLambdaUpdateWrapper.eq(ShortLinkDO::getFullShortUrl,requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid,requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus,0)
                .eq(ShortLinkDO::getDelFlag,0);
        ShortLinkDO shortLinkDO = ShortLinkDO.builder().enableStatus(1).build();
        baseMapper.update(shortLinkDO,shortLinkDOLambdaUpdateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

    /*
    * 分页短链接数据
    *
    * */
    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class);
        queryWrapper.in(ShortLinkDO::getGid,requestParam.getGidList())
                .eq(ShortLinkDO::getDelFlag,0)
                .eq(ShortLinkDO::getEnableStatus,1)
                .orderByDesc(ShortLinkDO::getCreateTime);

        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);

        IPage<ShortLinkPageRespDTO> shortLinkPageRespDTOs = resultPage.convert(
                each -> {
                    ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
                    result.setDomain("http://"+result.getDomain());
                    return result;
                }

        );

        return shortLinkPageRespDTOs;
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 1);
        ShortLinkDO shortLinkDO = ShortLinkDO.builder().enableStatus(0).build();
        baseMapper.update(shortLinkDO,updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 1);
        baseMapper.delete(updateWrapper);
    }
}
