package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.RegisterDAO;
import com.itweiyunfan.shiyan2.DAO.impl.RegisterDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private RegisterDAO registerDAO;

    @Override
    public void init() {
        registerDAO = new RegisterDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 通常GET方法用于显示注册页面
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取注册信息
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");
        String birthDate = request.getParameter("birthDate");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        // 调用注册DAO进行用户注册
        boolean isRegistered = registerDAO.registerUser(name, gender, birthDate, password, email);

        HttpSession session = request.getSession();
        if (isRegistered) {
            // 注册成功，设置成功消息
            session.setAttribute("registerMessage", "注册成功");
            response.sendRedirect("login.jsp"); // 注册成功后重定向到登录页面
        } else {
            // 注册失败，设置失败消息并重定向回注册页面
            session.setAttribute("registerMessage", "邮箱已存在，请选择其他邮箱");
            response.sendRedirect("register.jsp");
        }
    }
}
