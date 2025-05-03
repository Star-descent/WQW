package com.itweiyunfan.shiyan2.Servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.itweiyunfan.shiyan2.DAO.CommentDAO;
import com.itweiyunfan.shiyan2.DAO.impl.*;
import com.itweiyunfan.shiyan2.Pojo.Comment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/CommentServlet/*")
public class CommentServlet extends HttpServlet {
    private CommentDAO commentDao = new CommentDAOImpl();

    // 获取评论列表
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int novelId = Integer.parseInt(request.getParameter("novelId"));
        List<Comment> comments = commentDao.getCommentsByNovelId(novelId); // 获取小说的评论
        response.setContentType("application/json");
        new Gson().toJson(comments, response.getWriter()); // 返回评论的JSON格式
    }

    // 添加评论
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            int userId = (int) session.getAttribute("userId");

            // 从请求参数中获取小说ID和评论内容
            String novelIdStr = request.getParameter("novelId");
            String content = request.getParameter("content");

            // 检查参数是否为空
            if (novelIdStr == null || content == null || content.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int novelId = Integer.parseInt(novelIdStr); // 将 novelId 转换为整数
            Comment comment = new Comment();
            comment.setNovelId(novelId);
            comment.setUserId(userId);
            comment.setContent(content);

            // 保存评论到数据库
            boolean success = commentDao.addComment(comment);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }



    // 删除评论
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] pathInfo = request.getPathInfo().split("/");
        int commentId = Integer.parseInt(pathInfo[1]);
        boolean success = commentDao.deleteComment(commentId); // 从数据库删除评论
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
