package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Group;

import java.util.List;

public interface GroupDAO {
    // 新增全局分组
    void addGroup(String groupName);

    // 获取所有分组
    List<Group> getAllGroups();

    // 更新分组
    void updateGroup(Group group);

    // 删除分组
    void deleteGroupById(Integer groupId);

}