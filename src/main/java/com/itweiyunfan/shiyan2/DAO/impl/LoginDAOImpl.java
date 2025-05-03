package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.LoginDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAOImpl implements LoginDAO {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/prac?useOldAliasMetadata=true"; // 数据库名称
    private static final String USER = "root"; // 数据库用户名
    private static final String PASS = "root2"; // 数据库密码


    /**
     * 验证用户凭据并返回用户ID
     * @param name 用户名
     * @param password 密码
     * @return 用户ID，如果验证失败则返回 -1
     */
    @Override
    public int validateUserByName(String name, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int userId = -1; // 默认返回值

        try {
            // 加载驱动
            Class.forName(JDBC_DRIVER);

            // 建立连接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 创建SQL语句，使用数据库中的列名（例如 user_id）
            String sql = "SELECT user_id FROM users WHERE name = ? AND password = ?";
            stmt = conn.prepareStatement(sql);

            // 设置参数
            stmt.setString(1, name);
            stmt.setString(2, password);

            // 执行查询
            rs = stmt.executeQuery();

            // 如果找到匹配的记录，获取用户ID
            if (rs.next()) {
                userId = rs.getInt("user_id"); // 使用数据库中的列名 user_id
            }
        } catch (Exception e) {
            // 异常处理逻辑，例如打印日志等
            e.printStackTrace();
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
        return userId; // 返回用户ID或-1
    }
}
