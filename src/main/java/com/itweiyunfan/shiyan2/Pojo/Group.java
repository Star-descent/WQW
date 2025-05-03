package com.itweiyunfan.shiyan2.Pojo;

public class Group {
    private Integer groupId;
    private String groupName;
    private int userId; // 假设每个分组都有一个对应的用户ID

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
