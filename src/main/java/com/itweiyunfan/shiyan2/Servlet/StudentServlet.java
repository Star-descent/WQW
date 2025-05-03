package com.itweiyunfan.shiyan2.Servlet;

import com.itweiyunfan.shiyan2.DAO.StudentDAO;
import com.itweiyunfan.shiyan2.DAO.impl.StudentDAOImpl;
import com.itweiyunfan.shiyan2.Pojo.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.lang.System.out;

@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add":
                addStudent(request);
                break;
            case "delete":
                // deleteStudent方法内已处理重定向
                deleteStudent(request);
                break; // 这里return是关键，它避免了执行方法底部的重定向
            case "update":
                updateStudent(request);
                break;
            default:
                // 可能的错误处理或日志
                break;
        }
        response.sendRedirect("StudentServlet?action=list");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 此处可以处理更多的action，但对于学生列表，我们执行以下逻辑：
        String action = request.getParameter("action");
//        if (action == null || action.equals("list")) {
//            List<Student> students = studentDAO.getAllStudents();
//            request.setAttribute("students", students);
//            request.getRequestDispatcher("/student.jsp").forward(request, response);
//        }
        if ("delete".equals(action)) {
            deleteStudent(request);
            response.sendRedirect("StudentServlet?action=list"); // 重定向到学生列表
        } else if (action == null || "list".equals(action)) {
            // 现有的列出学生的逻辑
            List<Student> students = studentDAO.getAllStudents();
            request.setAttribute("students", students);
            request.getRequestDispatcher("/student.jsp").forward(request, response);
        }
        // 其他的action处理
    }

    private Student createStudentFromRequest(HttpServletRequest request) {
        Student student = new Student();
        student.setSid(request.getParameter("sid"));
        student.setName(request.getParameter("name"));
        student.setGender(request.getParameter("gender").charAt(0));
        student.setAge(Integer.parseInt(request.getParameter("age")));

        String birthdayStr = request.getParameter("birthday");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            student.setBirthday(sdf.parse(birthdayStr));
        } catch (ParseException e) {
            e.printStackTrace();
            // 在实际情况中，您可能需要更好地处理这个异常，例如设置一个默认日期或返回错误信息
        }
        return student;
    }


    private void addStudent(HttpServletRequest request) {
        // 实现从请求创建学生对象的逻辑相同
        Student student = createStudentFromRequest(request);
        studentDAO.addStudent(student);
    }

    private void deleteStudent(HttpServletRequest request) {
        String sid = request.getParameter("sid");
        studentDAO.deleteStudentBySid(sid); // 假设您有这样一个方法在DAO
    }

    private void updateStudent(HttpServletRequest request) {
        Student student = createStudentFromRequest(request);
        studentDAO.updateStudent(student);
    }

    private void forwardStudentList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        // 转发到JSP页面显示学生列表
        request.getRequestDispatcher("/student.jsp").forward(request, response);
    }
}
