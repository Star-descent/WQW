package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Novel;
import com.itweiyunfan.shiyan2.Pojo.UserNovelWordCount;

import java.util.List;

public interface NovelDAO {
    List<Novel> getAllNovels();
    void addNovel(Novel novel);
    List<Novel> getNovelsByUserId(int userId);
    boolean updateNovel(Novel novel);
    boolean deleteNovel(int novelId);
    // 获取带有分组信息的小说列表
    public List<Novel> getNovelsWithGroups();

    public List<UserNovelWordCount> getTotalContentLengthByUser();
}
