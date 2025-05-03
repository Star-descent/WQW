package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.CommentDAO;
import com.itweiyunfan.shiyan2.Pojo.Comment;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOImpl implements CommentDAO {
    private static final Logger logger = LoggerFactory.getLogger(CommentDAOImpl.class); // Logger实例
    private static HikariDataSource dataSource;

    // 配置 Hikari 数据源
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/prac?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("root2");
        config.setMaximumPoolSize(10); // 设置最大连接数

        dataSource = new HikariDataSource(config);
    }

    // 获取数据库连接
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 获取指定小说的所有评论
    @Override
    public List<Comment> getCommentsByNovelId(int novelId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE novel_id = ? ORDER BY timestamp ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setNovelId(rs.getInt("novel_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setTimestamp(rs.getTimestamp("timestamp"));
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            logger.error("获取小说ID {} 的评论时发生错误: {}", novelId, e.getMessage(), e);
        }
        return comments;
    }

    // 添加评论
    @Override
    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (novel_id, user_id, content, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, comment.getNovelId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getContent());
            stmt.executeUpdate();
            logger.info("成功添加评论到小说ID {}", comment.getNovelId());
            return true;
        } catch (SQLException e) {
            logger.error("添加评论到小说ID {} 时发生错误: {}", comment.getNovelId(), e.getMessage(), e);
            return false;
        }
    }

    // 删除评论
    @Override
    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("成功删除评论ID {}", commentId);
                return true;
            } else {
                logger.warn("删除评论ID {} 失败: 未找到该评论", commentId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("删除评论ID {} 时发生错误: {}", commentId, e.getMessage(), e);
            return false;
        }
    }
}
