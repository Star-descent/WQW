package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.LoginDAO;
import com.itweiyunfan.shiyan2.DAO.impl.LoginDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/LoginServlet") // 使用@WebServlet注解定义Servlet映射
public class LoginServlet extends HttpServlet {
    private LoginDAO loginDAO;

    @Override
    public void init() {
        loginDAO = new LoginDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 通常GET方法用于显示登录页面
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取姓名和密码的代码
        String name = request.getParameter("name"); // 改为获取姓名
        String password = request.getParameter("password");

        // 使用新的验证方法，假设 validateUserByName 返回 userId
        int userId = loginDAO.validateUserByName(name, password); // 如果登录成功返回用户ID，否则返回 -1

        if (userId != -1) { // 登录成功
            // 设置用户ID到session中
            HttpSession session = request.getSession();
            session.setAttribute("userId", userId); // 存储用户ID到session
            session.setAttribute("loginMessage", "登录成功");

            // 检查是否需要记住姓名和密码
            if ("on".equals(request.getParameter("rememberMe"))) {
                Cookie nameCookie = new Cookie("name", name); // 存储姓名的 cookie
                Cookie passwordCookie = new Cookie("password", password);
                // 设置 cookie 过期时间为 7 天
                nameCookie.setMaxAge(7 * 24 * 60 * 60);
                passwordCookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(nameCookie);
                response.addCookie(passwordCookie);
            } else {
                // 清除 cookie
                clearCookie(response, "name");
                clearCookie(response, "password");
            }

            // 登录成功后，重定向到主界面
            response.sendRedirect("MainServlet/" + userId); // 使用userId重定向
        } else {
            // 登录失败，设置失败消息并重定向回登录页面
            HttpSession session = request.getSession();
            session.setAttribute("loginMessage", "姓名或密码错误");
            response.sendRedirect("login.jsp");
        }
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
