package com.itweiyunfan.shiyan2.Pojo;

import java.sql.Timestamp;

public class Novel {
    private int novelId;      // 小说ID
    private int userId;       // 用户ID
    private String title;     // 小说标题
    private String content;   // 小说内容
    private Timestamp publishDate; // 发布时间
    private Timestamp lastUpdate;   // 最后更新时间

    // 默认构造函数
    public Novel() {}

    // 构造函数
    public Novel(int novelId, int userId, String title, String content, Timestamp publishDate, Timestamp lastUpdate) {
        this.novelId = novelId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.publishDate = publishDate;
        this.lastUpdate = lastUpdate;
    }

    // Getter 和 Setter 方法
    public int getNovelId() {
        return novelId;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
