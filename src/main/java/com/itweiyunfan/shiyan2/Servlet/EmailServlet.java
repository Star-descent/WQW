package com.itweiyunfan.shiyan2.Servlet;

import com.google.gson.Gson;
import com.itweiyunfan.shiyan2.DAO.EmailDAO;
import com.itweiyunfan.shiyan2.DAO.impl.EmailDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Emails;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/EmailServlet")
public class EmailServlet extends HttpServlet {

    private EmailDAO emailDAO = new EmailDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addEmail(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("setAsUsername".equals(action)) {
            setAsUsername(request, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        deleteEmail(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        List<Emails> emails = emailDAO.getEmailsByUserId(userId);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(new Gson().toJson(emails));
        out.close();
    }

    private void addEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String emailAddress = request.getParameter("email");

        // 检查邮箱是否已存在
        if (emailDAO.isEmailExists(emailAddress)) {
            // 如果邮箱已存在，返回 409 Conflict 状态码
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("该邮箱已存在，请使用其他邮箱");
            return;
        }

        // 如果邮箱不存在，继续添加邮箱
        Emails email = new Emails();
        email.setUserId(userId);
        email.setEmail(emailAddress);

        emailDAO.addEmail(email);
        response.setStatus(HttpServletResponse.SC_OK);
    }


    private void setAsUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int emailId = Integer.parseInt(request.getParameter("emailId"));
        int userId = Integer.parseInt(request.getParameter("userId"));

        emailDAO.setEmailAsUsername(emailId, userId);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void deleteEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int emailId = Integer.parseInt(request.getParameter("emailId"));
        emailDAO.deleteEmail(emailId);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
