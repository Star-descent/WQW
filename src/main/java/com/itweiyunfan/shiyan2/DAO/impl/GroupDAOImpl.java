package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.GroupDAO;
import com.itweiyunfan.shiyan2.Pojo.Group;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAOImpl implements GroupDAO {
    private static final Logger logger = LoggerFactory.getLogger(GroupDAOImpl.class); // 创建Logger实例
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

    // 获取所有分组，不再依赖 userId
    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM gps"; // 替换为你的表名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Group group = new Group();
                group.setGroupId(rs.getInt("group_id"));
                group.setGroupName(rs.getString("group_name"));
                groups.add(group);
            }
        } catch (SQLException e) {
            logger.error("获取所有分组时发生错误: {}", e.getMessage(), e);
        }
        return groups;
    }

    // 添加分组，不再依赖 userId
    @Override
    public void addGroup(String groupName) {
        String sql = "INSERT INTO gps (group_name) VALUES (?)"; // 替换为你的表名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, groupName);
            stmt.executeUpdate();
            logger.info("成功添加分组 '{}'", groupName);
        } catch (SQLException e) {
            logger.error("添加分组 '{}' 时发生错误: {}", groupName, e.getMessage(), e);
        }
    }

    // 删除分组
    @Override
    public void deleteGroupById(Integer groupId) {
        String sql = "DELETE FROM gps WHERE group_id = ?"; // 替换为你的表名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
            logger.info("成功删除分组ID {}", groupId);
        } catch (SQLException e) {
            logger.error("删除分组ID {} 时发生错误: {}", groupId, e.getMessage(), e);
        }
    }

    // 更新分组
    @Override
    public void updateGroup(Group group) {
        String sql = "UPDATE gps SET group_name = ? WHERE group_id = ?"; // 替换为你的表名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, group.getGroupName());
            stmt.setInt(2, group.getGroupId());
            stmt.executeUpdate();
            logger.info("成功更新分组ID {} 的名称为 '{}'", group.getGroupId(), group.getGroupName());
        } catch (SQLException e) {
            logger.error("更新分组ID {} 时发生错误: {}", group.getGroupId(), e.getMessage(), e);
        }
    }
}
