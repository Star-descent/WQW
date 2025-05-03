package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.GroupedNovelsDAO;
import com.itweiyunfan.shiyan2.Pojo.GroupedNovels;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupedNovelsDAOImpl implements GroupedNovelsDAO {
    private static final Logger logger = LoggerFactory.getLogger(GroupedNovelsDAOImpl.class);
    private static HikariDataSource dataSource;

    // 配置 Hikari 数据源
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/prac?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"); // 修改为你的数据库URL
        config.setUsername("root"); // 修改为你的数据库用户名
        config.setPassword("root2"); // 修改为你的数据库密码
        config.setMaximumPoolSize(10); // 设置最大连接数

        dataSource = new HikariDataSource(config);
    }

    // 获取数据库连接
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 添加小说到分组（允许同一小说加入多个分组）
    @Override
    public boolean addNovelToGroup(int groupId, int novelId) {
        String sql = "INSERT INTO groupednovels (group_id, novel_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, novelId);

            int rowsAffected = stmt.executeUpdate();
            logger.info("成功将小说ID {} 添加到分组ID {}", novelId, groupId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("将小说ID {} 添加到分组ID {} 时发生错误: {}", novelId, groupId, e.getMessage(), e);
        }

        return false;
    }

    // 根据小说ID获取其所有分组
    @Override
    public List<Integer> getGroupsByNovelId(int novelId) {
        String sql = "SELECT group_id FROM groupednovels WHERE novel_id = ?";
        List<Integer> groupIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novelId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groupIds.add(rs.getInt("group_id"));
                }
            }
            logger.info("成功获取小说ID {} 所属的分组ID列表", novelId);

        } catch (SQLException e) {
            logger.error("获取小说ID {} 的分组时发生错误: {}", novelId, e.getMessage(), e);
        }

        return groupIds;
    }

    // 删除特定分组中的所有小说
// GroupedNovelsDao.java
    @Override
    public boolean deleteByGroupAndNovel(int groupId, int novelId) {
        String sql = "DELETE FROM groupednovels WHERE group_id = ? AND novel_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, novelId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 删除特定分组中的特定小说
    @Override
    public boolean removeNovelFromGroup(int groupId, int novelId) {
        String sql = "DELETE FROM groupednovels WHERE group_id = ? AND novel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, novelId);

            int rowsAffected = stmt.executeUpdate();
            logger.info("成功从分组ID {} 中删除小说ID {}", groupId, novelId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("从分组ID {} 中删除小说ID {} 时发生错误: {}", groupId, novelId, e.getMessage(), e);
        }

        return false;
    }

    // 根据小说ID删除其所有分组关联
    @Override
    public boolean deleteGroupsByNovelId(int novelId) {
        String sql = "DELETE FROM groupednovels WHERE novel_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novelId);

            int rowsAffected = stmt.executeUpdate();
            logger.info("成功删除小说ID {} 所属的所有分组", novelId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("删除小说ID {} 的分组关联时发生错误: {}", novelId, e.getMessage(), e);
        }

        return false;
    }

    // 获取所有不为null的分组与小说映射
    @Override
    public List<GroupedNovels> getGroupedNovels() {
        String sql = "SELECT gn.group_id, gn.novel_id " +
                "FROM groupednovels gn " +
                "JOIN gps g ON gn.group_id = g.group_id " +
                "JOIN novels n ON gn.novel_id = n.novel_id";

        List<GroupedNovels> groupedNovelsList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int groupId = rs.getInt("group_id");
                int novelId = rs.getInt("novel_id");

                // 只添加不为 null 的组和小说
                if (groupId != 0 && novelId != 0) {
                    GroupedNovels groupedNovels = new GroupedNovels(groupId, novelId);
                    groupedNovelsList.add(groupedNovels);
                }
            }
        } catch (SQLException e) {
            logger.error("获取分组与小说映射时发生错误: {}", e.getMessage(), e);
        }

        return groupedNovelsList;
    }

    @Override
    public String getGroupNameById(int groupId) {
        String groupName = "未分组"; // 默认返回"未分组"
        String sql = "SELECT group_name FROM gps WHERE group_id = ?"; // 根据 groupId 查询组名

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    groupName = rs.getString("group_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupName;
    }
}
