package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.GroupedNovels;

import java.util.List;

public interface GroupedNovelsDAO {
    boolean addNovelToGroup(int groupId, int novelId); // 添加小说到分组

    List<Integer> getGroupsByNovelId(int novelId);

    boolean deleteGroupsByNovelId(int novelId);

    boolean deleteByGroupAndNovel(int groupId, int novelId); // 删除分组中的所有小说

    boolean removeNovelFromGroup(int groupId, int novelId); // 从分组中移除特定小说

    List<GroupedNovels> getGroupedNovels(); // 获取分组与小说的映射
    // 新增方法：根据 groupId 获取组名
    String getGroupNameById(int groupId);
}
