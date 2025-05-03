package com.itweiyunfan.shiyan2.Servlet;

import com.google.gson.Gson;
import com.itweiyunfan.shiyan2.DAO.EducationDAO;
import com.itweiyunfan.shiyan2.DAO.UserDAO;
import com.itweiyunfan.shiyan2.DAO.impl.EducationDAOImpl;
import com.itweiyunfan.shiyan2.DAO.impl.UserDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Education;
import com.itweiyunfan.shiyan2.Pojo.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAOImpl();
    private EducationDAO educationDAO = new EducationDAOImpl();

    // 获取用户信息（GET请求）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 从会话中获取用户ID，确保只有登录的用户才能访问
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            // 如果未登录，重定向到登录页面
            response.sendRedirect("login.jsp");
            return;
        }

        // 从数据库获取用户信息
        Users user = userDAO.getUserById(userId);
        Education education = educationDAO.getEducationByUserId(userId);

        if (user != null) {
            // 如果用户存在，转发到用户信息页面
            request.setAttribute("user", user);
            request.setAttribute("education", education);
            request.getRequestDispatcher("/user.jsp").forward(request, response);
        } else {
            // 如果未找到用户，重定向到错误页面
            response.sendRedirect("error.jsp");
        }
    }

    // 更新用户信息（PUT请求）
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            // 如果未登录，返回 401 Unauthorized 状态
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return;
        }

        // 将请求中的JSON转换为用户对象
        Users user = new Gson().fromJson(request.getReader(), Users.class);

        // 设置用户ID，确保只能修改自己的信息
        user.setUserId(userId);

        // 更新用户信息
        boolean updated = userDAO.updateUser(user);

        if (updated) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
