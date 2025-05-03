package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.ReplyDAO;
import com.itweiyunfan.shiyan2.Pojo.Reply;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyDAOImpl implements ReplyDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReplyDAOImpl.class); // Logger实例
    private static HikariDataSource dataSource;

    // 配置 Hikari 数据源
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/prac?useOldAliasMetadata=true");
        config.setUsername("root");
        config.setPassword("root2");
        config.setMaximumPoolSize(10); // 设置最大连接数

        dataSource = new HikariDataSource(config);
    }

    // 获取数据库连接
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 获取指定评论的所有回复
    @Override
    public List<Reply> getRepliesByCommentId(int commentId) {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM replies WHERE comment_id = ? ORDER BY timestamp ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reply reply = new Reply();
                    reply.setReplyId(rs.getInt("reply_id"));
                    reply.setCommentId(rs.getInt("comment_id"));
                    reply.setUserId(rs.getInt("user_id"));
                    reply.setContent(rs.getString("content"));
                    reply.setTimestamp(rs.getTimestamp("timestamp"));
                    replies.add(reply);
                }
            }
        } catch (SQLException e) {
            logger.error("获取评论ID {} 的回复时发生错误: {}", commentId, e.getMessage(), e);
        }
        return replies;
    }

    // 添加回复
    @Override
    public boolean addReply(Reply reply) {
        String sql = "INSERT INTO replies (comment_id, user_id, content, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reply.getCommentId());
            stmt.setInt(2, reply.getUserId());
            stmt.setString(3, reply.getContent());
            stmt.executeUpdate();
            logger.info("成功添加回复到评论ID {}", reply.getCommentId());
            return true;
        } catch (SQLException e) {
            logger.error("添加回复到评论ID {} 时发生错误: {}", reply.getCommentId(), e.getMessage(), e);
            return false;
        }
    }

    // 删除回复
    @Override
    public boolean deleteReply(int replyId) {
        String sql = "DELETE FROM replies WHERE reply_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, replyId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("成功删除回复ID {}", replyId);
                return true;
            } else {
                logger.warn("删除回复ID {} 失败: 未找到该回复", replyId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("删除回复ID {} 时发生错误: {}", replyId, e.getMessage(), e);
            return false;
        }
    }
}
