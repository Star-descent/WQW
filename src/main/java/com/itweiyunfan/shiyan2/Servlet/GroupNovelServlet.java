package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.GroupedNovelsDAO;
import com.itweiyunfan.shiyan2.DAO.impl.GroupedNovelsDAOImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/GroupedNovelsServlet/*")
public class GroupNovelServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GroupNovelServlet.class);
    private GroupedNovelsDAO groupedNovelsDAO;

    @Override
    public void init() {
        groupedNovelsDAO = new GroupedNovelsDAOImpl(); // 初始化 DAO 实现
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 读取请求体中的 JSON 数据
        BufferedReader reader = request.getReader();
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }

        // 解析 JSON 数据
        JsonObject jsonObject = JsonParser.parseString(jsonString.toString()).getAsJsonObject();
        int novelId = jsonObject.get("novelId").getAsInt();
        int groupId = jsonObject.get("groupId").getAsInt();

        // 将小说加入分组
        boolean success = groupedNovelsDAO.addNovelToGroup(groupId, novelId);

        if (success) {
            logger.info("小说ID {} 已成功加入分组ID {}", novelId, groupId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Novel added to group successfully.");
        } else {
            logger.error("小说ID {} 加入分组ID {} 失败", novelId, groupId);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to add novel to group.");
        }
    }

    // GroupedNovelsServlet.java
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] pathInfo = request.getPathInfo().split("/"); // 提取路径中的参数
        int novelId = Integer.parseInt(pathInfo[1]); // 提取小说ID
        int groupId = Integer.parseInt(pathInfo[2]); // 提取分组ID

        boolean success = groupedNovelsDAO.deleteByGroupAndNovel(groupId, novelId); // 直接调用 DAO 删除小说

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
