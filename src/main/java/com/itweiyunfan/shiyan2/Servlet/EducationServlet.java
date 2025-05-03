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
import java.util.logging.Logger;
import java.io.PrintWriter;

@WebServlet("/EducationServlet")
public class EducationServlet extends HttpServlet {
    private EducationDAO educationDAO = new EducationDAOImpl();
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Education education = educationDAO.getEducationByUserId(userId);
        Users user = userDAO.getUserById(userId);

        //        if (education != null) {
//            System.out.println("Education record found: " + education.toString());
//            request.setAttribute("education", education);
//        } else {
//            System.out.println("No education record found for userId: " + userId);
//        }

        request.setAttribute("user", user);
        request.setAttribute("education", education);

        request.getRequestDispatcher("/user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        Education existingEducation = educationDAO.getEducationByUserId(userId);
        Education education = new Gson().fromJson(request.getReader(), Education.class);
        education.setUserId(userId);

        if (existingEducation != null) {
            education.setEducationId(existingEducation.getEducationId());
            educationDAO.updateEducation(education); // 更新已有的教育经历
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            educationDAO.addEducation(education); // 添加新的教育经历
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取用户ID，这通常存储在会话中
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        Integer educationId = (Integer) request.getSession().getAttribute("educationId");
        if (userId == null) {
            System.out.println("User not logged in, cannot update education.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }
        try {
            // 从请求体中解析Education对象
            Education education = new Gson().fromJson(request.getReader(), Education.class);
            education.setUserId(userId); // 确保教育记录与正确的用户ID关联

            // 尝试更新教育记录
            boolean updated = educationDAO.updateEducation(education);
            if (!updated) {
                System.out.println("Failed to update education for userId: " + userId);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update education");
            } else {
                System.out.println("Education updated successfully for userId: " + userId);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            // 捕获并记录解析或更新中的异常
            System.out.println("Error updating education for educationId: " + educationId + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error processing the education update");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        boolean deleted = educationDAO.deleteEducationByUserId(userId);
        response.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
