package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Character;
import java.util.List;

public interface CharacterDAO {
    List<Character> getCharactersByNovelId(int novelId);   // 根据小说ID获取角色
    void addCharacter(Character character);                // 增加角色
    void updateCharacter(Character character);             // 更新角色
    void deleteCharacter(int characterId);                 // 删除角色
}
