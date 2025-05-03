<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" import="java.util.*, com.itweiyunfan.shiyan2.Pojo.Student"%>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <title>学生列表</title>
    <script type="text/javascript">
        function confirmDelete(sid) {
            var delConfirm = confirm("您确定要删除学号为 " + sid + " 的学生吗?");
            if (delConfirm) {
                window.location.href = "StudentServlet?action=delete&sid=" + sid;
            }
        }
    </script>
</head>
<body>
<h1>学生列表</h1>
<a href="add.html">新增学生</a>
<table border="1">
    <thead>
    <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>性别</th>
        <th>年龄</th>
        <th>生日</th> <!-- 添加显示生日的表头 -->
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Student> students = (List<Student>) request.getAttribute("students");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 用于格式化生日日期
        if (students != null) {
            for (Student student : students) {
    %>
    <tr>
        <td><%= student.getSid() %></td>
        <td><%= student.getName() %></td>
        <td><%= student.getGender() == 'M' ? "男" : "女" %></td>
        <td><%= student.getAge() %></td>
        <td><%= student.getBirthday() != null ? sdf.format(student.getBirthday()) : "" %></td> <!-- 显示生日 -->
        <td>
            <a href="modify.jsp?sid=<%= student.getSid() %>">修改</a>
            | <a href="javascript:confirmDelete('<%= student.getSid() %>')">删除</a>
        </td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table>
</body>
</html>
