package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.RegisterDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDAOImpl implements RegisterDAO {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/prac"; // 连接到新的数据库
    private static final String USER = "root";
    private static final String PASS = "root2";

    @Override
    public boolean registerUser(String name, String gender, String birthDate, String password, String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 加载驱动
            Class.forName(JDBC_DRIVER);

            // 建立连接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 检查邮箱是否已存在于 users 表中
            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            stmt = conn.prepareStatement(checkEmailSql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // 如果邮箱已存在，返回 false
                return false;
            }

            // 插入新用户到 users 表
            String insertUserSql = "INSERT INTO users (name, gender, birth_date, password, email) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, gender);
            stmt.setString(3, birthDate);
            stmt.setString(4, password); // 在实际应用中，请务必对密码进行加密处理
            stmt.setString(5, email);
            stmt.executeUpdate();

            // 获取新插入用户的 user_id
            rs = stmt.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // 检查是否成功获取 user_id
            if (userId == -1) {
                throw new SQLException("Failed to retrieve user ID after inserting user.");
            }

            // 插入新邮箱到 emails 表，并将 is_username 置为 1
            String insertEmailSql = "INSERT INTO emails (user_id, email, is_username) VALUES (?, ?, 1)";
            try (PreparedStatement emailStmt = conn.prepareStatement(insertEmailSql)) {
                emailStmt.setInt(1, userId);
                emailStmt.setString(2, email);
                emailStmt.executeUpdate();
            }

            return true; // 注册成功
        } catch (Exception e) {
            // 异常处理逻辑，例如打印日志等
            e.printStackTrace();
            return false; // 注册失败
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
