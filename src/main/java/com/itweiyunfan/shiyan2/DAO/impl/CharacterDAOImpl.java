package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.CharacterDAO;
import com.itweiyunfan.shiyan2.Pojo.Character;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAOImpl implements CharacterDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/prac";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root2";

    @Override
    public List<Character> getCharactersByNovelId(int novelId) {
        List<Character> characters = new ArrayList<>();
        String sql = "SELECT * FROM characters WHERE novel_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, novelId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Character character = new Character();
                character.setCharacterId(rs.getInt("character_id"));
                character.setNovelId(rs.getInt("novel_id"));
                character.setName(rs.getString("name"));
                character.setDescription(rs.getString("description"));
                characters.add(character);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return characters;
    }

    @Override
    public void addCharacter(Character character) {
        String sql = "INSERT INTO characters (novel_id, name, description) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, character.getNovelId());
            stmt.setString(2, character.getName());
            stmt.setString(3, character.getDescription());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCharacter(Character character) {
        String sql = "UPDATE characters SET name = ?, description = ? WHERE character_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, character.getName());
            stmt.setString(2, character.getDescription());
            stmt.setInt(3, character.getCharacterId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCharacter(int characterId) {
        String sql = "DELETE FROM characters WHERE character_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, characterId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
