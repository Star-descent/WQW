package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.NovelDAO;
import com.itweiyunfan.shiyan2.Pojo.Novel;
import com.itweiyunfan.shiyan2.Pojo.UserNovelWordCount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NovelDAOImpl implements NovelDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/prac"; // 替换为你的数据库URL
    private static final String USER = "root"; // 替换为你的数据库用户名
    private static final String PASSWORD = "root2"; // 替换为你的数据库密码

    // 获取所有小说及其作者
    @Override
    public List<Novel> getAllNovels() {
        List<Novel> novels = new ArrayList<>();
        String sql = "SELECT n.novel_id, n.user_id, n.title, n.content, n.publish_date, n.last_update, u.name AS author_name " +
                "FROM novels n " +
                "JOIN users u ON n.user_id = u.user_id";  // 联合查询小说和用户

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 创建 Novel 对象并设置值
                Novel novel = new Novel();
                novel.setNovelId(rs.getInt("novel_id"));
                novel.setUserId(rs.getInt("user_id"));
                novel.setTitle(rs.getString("title"));
                novel.setContent(rs.getString("content"));
                novel.setPublishDate(rs.getTimestamp("publish_date"));
                novel.setLastUpdate(rs.getTimestamp("last_update"));

                // 可选：将作者名称存储在 novel 中，或者在 JSP 中根据用户ID显示作者
                novels.add(novel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return novels;
    }

    @Override
    public void addNovel(Novel novel) {
        String sql = "INSERT INTO novels (user_id, title, content, publish_date, last_update) VALUES (?, ?, ?, NOW(), NOW())";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, novel.getUserId());
            statement.setString(2, novel.getTitle());
            statement.setString(3, novel.getContent());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Novel> getNovelsByUserId(int userId) {
        List<Novel> novels = new ArrayList<>();
        String sql = "SELECT * FROM novels WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Novel novel = new Novel();
                novel.setNovelId(resultSet.getInt("novel_id"));
                novel.setUserId(resultSet.getInt("user_id"));
                novel.setTitle(resultSet.getString("title"));
                novel.setContent(resultSet.getString("content"));
                novel.setPublishDate(resultSet.getTimestamp("publish_date"));
                novel.setLastUpdate(resultSet.getTimestamp("last_update"));
                novels.add(novel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return novels;
    }

    @Override
    public boolean updateNovel(Novel novel) {
        String sql = "UPDATE novels SET title = ?, content = ?, last_update = NOW() WHERE novel_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, novel.getTitle());
            statement.setString(2, novel.getContent());
            statement.setInt(3, novel.getNovelId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteNovel(int novelId) {
        String deleteGroupedNovelsSql = "DELETE FROM groupednovels WHERE novel_id = ?";
        String deleteCharactersSql = "DELETE FROM characters WHERE novel_id = ?";
        String deleteCommentsSql = "DELETE FROM comments WHERE novel_id = ?";
        String deleteNovelSql = "DELETE FROM novels WHERE novel_id = ?";

        // 先声明并初始化 connection 为 null
        Connection connection = null;

        try {
            // 初始化 connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false); // 开启事务

            // 删除 groupednovels 表中的关联记录
            try (PreparedStatement ps = connection.prepareStatement(deleteGroupedNovelsSql)) {
                ps.setInt(1, novelId);
                ps.executeUpdate();
            }

            // 删除 characters 表中的关联记录
            try (PreparedStatement ps = connection.prepareStatement(deleteCharactersSql)) {
                ps.setInt(1, novelId);
                ps.executeUpdate();
            }

            // 删除 comments 表中的关联记录
            try (PreparedStatement ps = connection.prepareStatement(deleteCommentsSql)) {
                ps.setInt(1, novelId);
                ps.executeUpdate();
            }

            // 删除 novels 表中的记录
            try (PreparedStatement ps = connection.prepareStatement(deleteNovelSql)) {
                ps.setInt(1, novelId);
                int rowsAffected = ps.executeUpdate();
                connection.commit(); // 提交事务
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback(); // 出错时回滚事务
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // 在 finally 块中关闭 connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public List<Novel> getNovelsWithGroups() {
        List<Novel> novels = new ArrayList<>();
        String sql = "SELECT n.novel_id, n.user_id, n.title, n.content, n.publish_date, n.last_update, u.name AS author_name " +
                "FROM novels n " +
                "JOIN users u ON n.user_id = u.user_id " +
                "WHERE n.novel_id IN (SELECT gn.novel_id FROM groupednovels gn)";  // 嵌套查询确保仅返回有分组的小说

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 创建 Novel 对象并设置值
                Novel novel = new Novel();
                novel.setNovelId(rs.getInt("novel_id"));
                novel.setUserId(rs.getInt("user_id"));
                novel.setTitle(rs.getString("title"));
                novel.setContent(rs.getString("content"));
                novel.setPublishDate(rs.getTimestamp("publish_date"));
                novel.setLastUpdate(rs.getTimestamp("last_update"));

                // 可选：将作者名称存储在 novel 中，或者在 JSP 中根据用户ID显示作者
                novels.add(novel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return novels;
    }

    @Override
    public List<UserNovelWordCount> getTotalContentLengthByUser() {
        List<UserNovelWordCount> userWordCounts = new ArrayList<>();
        String sql = "SELECT u.user_id, u.name, SUM(LENGTH(n.content)) AS total_words " +
                "FROM novels n " +
                "JOIN users u ON n.user_id = u.user_id " +
                "GROUP BY u.user_id, u.name";  // 按用户ID和姓名分组

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 创建 UserNovelWordCount 对象并设置值
                UserNovelWordCount userWordCount = new UserNovelWordCount();
                userWordCount.setUserId(rs.getInt("user_id"));
                userWordCount.setUserName(rs.getString("name"));
                userWordCount.setTotalWords(rs.getInt("total_words"));

                userWordCounts.add(userWordCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userWordCounts;
    }
}
