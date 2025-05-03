package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.EmailDAO;
import com.itweiyunfan.shiyan2.Pojo.Emails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDAOImpl implements EmailDAO {

    // 静态变量用于数据库连接
    private static final String URL = "jdbc:mysql://localhost:3306/prac";
    private static final String USER = "root";
    private static final String PASSWORD = "root2";

    // 获取数据库连接的方法
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public List<Emails> getEmailsByUserId(int userId) {
        List<Emails> emails = new ArrayList<>();
        String query = "SELECT * FROM emails WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Emails email = new Emails();
                    email.setEmailId(rs.getInt("email_id"));
                    email.setUserId(rs.getInt("user_id"));
                    email.setEmail(rs.getString("email"));
                    email.setIsUsername(rs.getBoolean("is_username"));
                    emails.add(email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    @Override
    public void addEmail(Emails email) {
        if (email.getEmail() == null || email.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        // 检查邮箱是否已存在
        String checkQuery = "SELECT COUNT(*) FROM emails WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setString(1, email.getEmail());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("邮箱已存在，不能重复添加");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 插入新邮箱
        String insertQuery = "INSERT INTO emails (user_id, email, is_username) VALUES (?, ?, 0)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setInt(1, email.getUserId());
            ps.setString(2, email.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setEmailAsUsername(int emailId, int userId) {
        String resetQuery = "UPDATE emails SET is_username = 0 WHERE user_id = ?";
        String setUsernameQuery = "UPDATE emails SET is_username = 1 WHERE email_id = ? AND user_id = ?";
        String updateUserEmailQuery = "UPDATE users SET email = (SELECT email FROM emails WHERE email_id = ?) WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = getConnection(); // 获取数据库连接
            conn.setAutoCommit(false); // 开启事务

            // 1. 重置该用户的所有邮箱的 is_username 字段为 0
            try (PreparedStatement resetPs = conn.prepareStatement(resetQuery)) {
                resetPs.setInt(1, userId);
                resetPs.executeUpdate();
            }

            // 2. 将指定的 email_id 设置为 is_username = 1
            try (PreparedStatement setUsernamePs = conn.prepareStatement(setUsernameQuery)) {
                setUsernamePs.setInt(1, emailId);
                setUsernamePs.setInt(2, userId);
                setUsernamePs.executeUpdate();
            }

            // 3. 更新 users 表中该用户的 email 字段
            try (PreparedStatement updateUserEmailPs = conn.prepareStatement(updateUserEmailQuery)) {
                updateUserEmailPs.setInt(1, emailId);
                updateUserEmailPs.setInt(2, userId);
                updateUserEmailPs.executeUpdate();
            }

            // 提交事务
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 如果出现异常，回滚事务
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // 关闭连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteEmail(int emailId) {
        String query = "DELETE FROM emails WHERE email_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, emailId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM emails WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
