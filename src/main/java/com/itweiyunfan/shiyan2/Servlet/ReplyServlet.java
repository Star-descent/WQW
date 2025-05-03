package com.itweiyunfan.shiyan2.Servlet;

import com.google.gson.Gson;
import com.itweiyunfan.shiyan2.DAO.ReplyDAO;
import com.itweiyunfan.shiyan2.Pojo.Reply;
import com.itweiyunfan.shiyan2.DAO.impl.ReplyDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/ReplyServlet/*")
public class ReplyServlet extends HttpServlet {
    private ReplyDAO replyDAO = new ReplyDAOImpl(); // 使用实现类

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取评论ID
        int commentId = Integer.parseInt(request.getParameter("commentId"));

        // 获取对应评论的所有回复
        List<Reply> replies = replyDAO.getRepliesByCommentId(commentId);

        // 使用 Gson 将回复列表转换为 JSON 格式并发送给前端
        String json = new Gson().toJson(replies);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取评论ID、回复内容和用户ID
        int commentId = Integer.parseInt(request.getParameter("commentId"));
        String content = request.getParameter("content");
        // 假设用户ID从会话中获取，表示当前登录用户
        int userId = (int) request.getSession().getAttribute("userId");

        // 创建 Reply 对象并设置属性
        Reply reply = new Reply();
        reply.setCommentId(commentId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // 添加回复到数据库
        if (replyDAO.addReply(reply)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 从路径中获取回复ID
        int replyId = Integer.parseInt(request.getPathInfo().substring(1)); // 假设URL格式为 /ReplyServlet/{replyId}

        // 删除指定的回复
        if (replyDAO.deleteReply(replyId)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
