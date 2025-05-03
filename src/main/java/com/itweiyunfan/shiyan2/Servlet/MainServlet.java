package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.GroupDAO;
import com.itweiyunfan.shiyan2.DAO.GroupedNovelsDAO;
import com.itweiyunfan.shiyan2.DAO.NovelDAO;
import com.itweiyunfan.shiyan2.DAO.UserDAO;
import com.itweiyunfan.shiyan2.DAO.impl.GroupDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.GroupedNovelsDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.NovelDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.UserDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Group;
import com.itweiyunfan.shiyan2.Pojo.GroupedNovels;
import com.itweiyunfan.shiyan2.Pojo.Novel;
import com.itweiyunfan.shiyan2.Pojo.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/MainServlet/*")
public class MainServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MainServlet.class);
    private NovelDAO novelDAO;
    private UserDAO userDAO;
    private GroupedNovelsDAO groupedNovelsDAO;

    private GroupDAO groupDAO;

    @Override
    public void init() {
        novelDAO = new NovelDAOImpl();
        userDAO = new UserDAOImpl();
        groupDAO = new GroupDAOImpl();
        groupedNovelsDAO = new GroupedNovelsDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取所有分组，不再依赖 userId
        List<Novel> novels = novelDAO.getAllNovels();
        List<Users> users = userDAO.getAllUsers(); // 获取所有用户信息
        List<Group> groups = groupDAO.getAllGroups();
        List<GroupedNovels> groupedNovelsList = groupedNovelsDAO.getGroupedNovels(); // 获取小说与分组的映射
        if (groups == null || groups.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No groups found.");
            return;
        }

        request.setAttribute("novels", novels);
        request.setAttribute("users", users);
        request.setAttribute("gps", groups);
        request.setAttribute("groupedNovels", groupedNovelsList);
//        request.setAttribute("groupToNovelMap", groupToNovelMap); // 传递小说-组名映射到 JSP
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 保留登录检查
        int userId = getUserIdFromSession(request);
        if (userId == -1) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 获取所有分组，检查分组数量是否超过7个
        List<Group> groups = groupDAO.getAllGroups();
        if (groups != null && groups.size() >= 7) {
            logger.warn("Group limit reached. Cannot add more than 7 groups.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 返回403状态码
            response.getWriter().write("分组数量不能超过7个");
            return;
        }

        // 添加新分组，不再依赖 userId
        String groupName = request.getParameter("groupName");
        if (groupName != null && !groupName.trim().isEmpty()) {
            groupDAO.addGroup(groupName); // 不再传递 userId，直接添加分组
            response.sendRedirect(request.getContextPath() + "/MainServlet");
        } else {
            logger.warn("Attempted to add a group with an empty name.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Group name is required.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer groupId = getGroupIdFromPath(request);
        if (groupId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String body = sb.toString();
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseString(body).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Failed to parse request body as JSON", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format.");
            return;
        }

        String groupName = jsonObject.get("groupName").getAsString();
        if (groupName == null || groupName.trim().isEmpty()) {
            logger.warn("Attempted to update a group with invalid data.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group name.");
            return;
        }

        Group group = new Group();
        group.setGroupId(groupId);
        group.setGroupName(groupName);
        groupDAO.updateGroup(group);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Group updated successfully.");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer groupId = getGroupIdFromPath(request);
        if (groupId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID.");
            return;
        }

        groupDAO.deleteGroupById(groupId);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Group deleted successfully.");
    }

    private int getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        return (userId != null) ? userId : -1;
    }

    private Integer getGroupIdFromPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        logger.info("Received path info: " + pathInfo);

        if (pathInfo == null || pathInfo.length() <= 1) {
            logger.warn("No group ID found in path.");
            return null;
        }

        // 去除前面的 "/"，并将字符串转换为整数
        try {
            return Integer.parseInt(pathInfo.substring(1)); // 转换为整数
        } catch (NumberFormatException e) {
            logger.error("Invalid group ID format: " + pathInfo.substring(1), e);
            return null; // 如果不是有效的整数格式，返回 null
        }
    }


    private boolean isUserLoggedIn(HttpServletRequest request, int userId) {
        HttpSession session = request.getSession();
        return session.getAttribute("userId") != null && (int) session.getAttribute("userId") == userId;
    }
}
