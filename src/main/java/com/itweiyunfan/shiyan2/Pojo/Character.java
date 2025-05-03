package com.itweiyunfan.shiyan2.Pojo;

public class Character {
    private int characterId;  // 对应 character_id
    private int novelId;      // 对应 novel_id
    private String name;      // 角色名称
    private String description; // 角色描述

    // Getter 和 Setter 方法
    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public int getNovelId() {
        return novelId;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
