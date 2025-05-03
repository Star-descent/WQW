package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.GroupDAO;
import com.itweiyunfan.shiyan2.DAO.NovelDAO;
import com.itweiyunfan.shiyan2.DAO.UserDAO;
import com.itweiyunfan.shiyan2.DAO.GroupedNovelsDAO;
import com.itweiyunfan.shiyan2.DAO.impl.GroupDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.NovelDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.UserDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.GroupedNovelsDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Novel;
import com.itweiyunfan.shiyan2.Pojo.Users;
import com.itweiyunfan.shiyan2.Pojo.Group;
import com.itweiyunfan.shiyan2.Pojo.GroupedNovels;
import com.itweiyunfan.shiyan2.Pojo.UserNovelWordCount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

@WebServlet("/NovelServlet/*")
public class NovelServlet extends HttpServlet {
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
        String pathInfo = request.getPathInfo();

        if ("/grouped".equals(pathInfo)) {
            // 获取所有已分组的小说列表
            List<Novel> novelsWithGroups = novelDAO.getNovelsWithGroups();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // 将小说列表转换为 JSON 格式并发送给前端
            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(novelsWithGroups));
            return;
        }

        if (pathInfo != null && pathInfo.equals("/all")) {
            // 获取所有小说
            List<Novel> novels = novelDAO.getAllNovels();
            List<Users> users = userDAO.getAllUsers();
            List<Group> groups = groupDAO.getAllGroups();
            List<GroupedNovels> groupedNovelsList = groupedNovelsDAO.getGroupedNovels();

            // 获取每个用户的小说总字数
            List<UserNovelWordCount> userWordCounts = novelDAO.getTotalContentLengthByUser();
            request.setAttribute("userWordCounts", userWordCounts); // 将用户小说字数统计传递到 JSP

            // 筛选已分组的小说
            List<Novel> novelsWithGroups = novelDAO.getNovelsWithGroups();
            request.setAttribute("novelsWithGroups", novelsWithGroups); // 将已分组的小说列表传递到 JSP

            // 创建小说ID到组名的映射
            Map<Integer, String> novelToGroupMap = new HashMap<>();
            for (GroupedNovels groupedNovel : groupedNovelsList) {
                String groupName = groupedNovelsDAO.getGroupNameById(groupedNovel.getGroupId());
                novelToGroupMap.put(groupedNovel.getNovelId(), groupName);
            }

            // 设置属性并转发到 JSP
            request.setAttribute("novels", novels);
            request.setAttribute("users", users);
            request.setAttribute("gps", groups);
            request.setAttribute("groupedNovels", groupedNovelsList);
            request.setAttribute("novelToGroupMap", novelToGroupMap);
            request.getRequestDispatcher("/allNovels.jsp").forward(request, response);
            return;
        }

        // 展示当前登录用户的小说逻辑
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 获取该用户的小说列表
        List<Novel> novels = novelDAO.getNovelsByUserId(userId);
        List<Users> users = userDAO.getAllUsers();
        List<Group> groups = groupDAO.getAllGroups();
        List<GroupedNovels> groupedNovelsList = groupedNovelsDAO.getGroupedNovels();
        // 获取每个用户的小说总字数
        List<UserNovelWordCount> userWordCounts = novelDAO.getTotalContentLengthByUser();
        request.setAttribute("userWordCounts", userWordCounts); // 将用户小说字数统计传递到 JSP

        // 设置属性并转发到 JSP
        request.setAttribute("novels", novels);
        request.setAttribute("users", users);
        request.setAttribute("gps", groups);
        request.setAttribute("groupedNovels", groupedNovelsList);
        request.getRequestDispatcher("novel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            Novel novel = new Novel();
            novel.setUserId(userId);
            novel.setTitle(title);
            novel.setContent(content);

            novelDAO.addNovel(novel);

            response.sendRedirect(request.getContextPath() + "/NovelServlet");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户未登录");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的小说 ID");
            return;
        }

        try {
            int novelId = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String requestBody = sb.toString();
            Gson gson = new Gson();
            Novel novelToUpdate = gson.fromJson(requestBody, Novel.class);

            novelToUpdate.setNovelId(novelId);

            boolean updated = novelDAO.updateNovel(novelToUpdate);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "未找到小说");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的小说 ID");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的小说 ID");
            return;
        }

        try {
            int novelId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = novelDAO.deleteNovel(novelId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "未找到小说");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的小说 ID");
        }
    }
}
