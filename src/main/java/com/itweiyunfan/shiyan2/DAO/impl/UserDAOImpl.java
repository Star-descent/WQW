package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.UserDAO;
import com.itweiyunfan.shiyan2.Pojo.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/prac?useOldAliasMetadata=true"; // 替换为你的数据库URL
    private static final String USER = "root"; // 替换为你的数据库用户名
    private static final String PASSWORD = "root2"; // 替换为你的数据库密码

    @Override
    public void addUser(Users user) {
        String sql = "INSERT INTO users (name, gender, birth_date, password, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getGender());
            statement.setDate(3, new java.sql.Date(user.getBirthDate().getTime()));
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Users getUserById(int userId) {
        Users user = null;
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new Users(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("gender"),
                        resultSet.getDate("birth_date"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<Users> getAllUsers() {
        List<Users> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Users user = new Users(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("gender"),
                        resultSet.getDate("birth_date"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean updateUser(Users user) {
        String getEmailSql = "SELECT email FROM users WHERE user_id = ?";
        String updateUserSql = "UPDATE users SET name = ?, gender = ?, birth_date = ?, password = ?, email = ? WHERE user_id = ?";
        String resetUsernameFlagSql = "UPDATE emails SET is_username = 0 WHERE user_id = ?";
        String updateEmailSql = "UPDATE emails SET email = ?, is_username = 1 WHERE user_id = ? AND email = ?";
        String insertEmailSql = "INSERT INTO emails (user_id, email, is_username) VALUES (?, ?, 1)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 获取旧的 email 值以检查是否有变化
            String oldEmail = null;
            try (PreparedStatement getEmailStmt = connection.prepareStatement(getEmailSql)) {
                getEmailStmt.setInt(1, user.getUserId());
                ResultSet rs = getEmailStmt.executeQuery();
                if (rs.next()) {
                    oldEmail = rs.getString("email");
                }
            }

            // 更新用户信息
            try (PreparedStatement updateUserStmt = connection.prepareStatement(updateUserSql)) {
                updateUserStmt.setString(1, user.getName());
                updateUserStmt.setString(2, user.getGender());
                updateUserStmt.setDate(3, new java.sql.Date(user.getBirthDate().getTime()));
                updateUserStmt.setString(4, user.getPassword());
                updateUserStmt.setString(5, user.getEmail());
                updateUserStmt.setInt(6, user.getUserId());
                updateUserStmt.executeUpdate();
            }

            // 如果 email 有变化，更新 emails 表
            if (oldEmail == null || !oldEmail.equals(user.getEmail())) {
                // 重置该用户所有的 is_username 标志为 0
                try (PreparedStatement resetStmt = connection.prepareStatement(resetUsernameFlagSql)) {
                    resetStmt.setInt(1, user.getUserId());
                    resetStmt.executeUpdate();
                }

                // 检查旧 email 是否存在于 emails 表中
                try (PreparedStatement updateEmailStmt = connection.prepareStatement(updateEmailSql)) {
                    updateEmailStmt.setString(1, user.getEmail());
                    updateEmailStmt.setInt(2, user.getUserId());
                    updateEmailStmt.setString(3, oldEmail);
                    int rowsAffected = updateEmailStmt.executeUpdate();

                    // 如果旧 email 不存在，则插入新记录
                    if (rowsAffected == 0) {
                        try (PreparedStatement insertEmailStmt = connection.prepareStatement(insertEmailSql)) {
                            insertEmailStmt.setInt(1, user.getUserId());
                            insertEmailStmt.setString(2, user.getEmail());
                            insertEmailStmt.executeUpdate();
                        }
                    }
                }
            }

            return true; // 更新成功
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
