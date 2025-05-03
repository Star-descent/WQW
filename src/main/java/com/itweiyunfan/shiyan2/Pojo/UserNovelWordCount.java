package com.itweiyunfan.shiyan2.Pojo;

public class UserNovelWordCount {
    private int userId;
    private String userName;
    private int totalWords;

    // 构造函数
    public UserNovelWordCount() {
    }

    public UserNovelWordCount(int userId, String userName, int totalWords) {
        this.userId = userId;
        this.userName = userName;
        this.totalWords = totalWords;
    }

    // Getters 和 Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    // 重写 toString() 方法以便于调试输出
    @Override
    public String toString() {
        return "UserNovelWordCount{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", totalWords=" + totalWords +
                '}';
    }
}
