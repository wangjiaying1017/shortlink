package com.wjy.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.shortlink.admin.dao.entity.GroupDO;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.wjy.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.wjy.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/*
* 短链接分组接口层
* */
public interface GroupService extends IService<GroupDO> {

    /*
    新增短链接分组
    groupName 短链接分组名

    * */
    void saveGroup(String groupName);

    List<ShortLinkGroupRespDTO> listGroup();

    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
