package com.wjy.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.shortlink.project.dao.entity.ShortLinkDO;
import com.wjy.shortlink.project.dao.mapper.ShortLinkMapper;
import com.wjy.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.wjy.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
}
