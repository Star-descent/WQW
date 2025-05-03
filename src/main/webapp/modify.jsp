<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*, com.itweiyunfan.shiyan2.DAO.*, com.itweiyunfan.shiyan2.Pojo.Student"%>
<%@ page import="com.itweiyunfan.shiyan2.DAO.impl.StudentDAOImpl" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="static java.sql.DriverManager.println" %>
<!DOCTYPE html>
<html>
<head>
    <title>修改学生信息</title>
</head>
<body>
<%
    String sid = request.getParameter("sid");
    StudentDAO dao = new StudentDAOImpl();
    Student student = dao.getStudentBySid(sid);
    if (student != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = student.getBirthday() != null ? sdf.format(student.getBirthday()) : "";
%>
<h1>修改学生信息</h1>
<form action="StudentServlet" method="post">
    <input type="hidden" name="action" value="update">
    <div>
        <label for="sid">学号：</label>
        <input type="text" id="sid" name="sid" value="<%= student.getSid() %>" readonly>
    </div>
    <div>
        <label for="name">姓名：</label>
        <input type="text" id="name" name="name" value="<%= student.getName() %>" required>
    </div>
    <div>
        <label>性别：</label>
        <input type="radio" id="male" name="gender" value="M" <%= "M".equals(String.valueOf(student.getGender())) ? "checked" : "" %>>男
        <input type="radio" id="female" name="gender" value="F" <%= "F".equals(String.valueOf(student.getGender())) ? "checked" : "" %>>女
    </div>
    <div>
        <label for="age">年龄：</label>
        <input type="number" id="age" name="age" value="<%= student.getAge() %>" required min="1">
    </div>
    <div>
        <label for="birthday">生日：</label>
        <input type="date" id="birthday" name="birthday" value="<%= birthday %>" required>
    </div>
    <div>
        <button type="submit">提交</button>
    </div>
</form>
<%
    } else {
        println("<p>错误：未找到学生信息。</p>");
    }
%>
</body>
</html>
