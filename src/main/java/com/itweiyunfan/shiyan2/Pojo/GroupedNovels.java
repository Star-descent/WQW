package com.itweiyunfan.shiyan2.Pojo;

public class GroupedNovels {
    private int groupId;
    private int novelId;

    public GroupedNovels(int groupId, int novelId) {
        this.groupId = groupId;
        this.novelId = novelId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getNovelId() {
        return novelId;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }
}
