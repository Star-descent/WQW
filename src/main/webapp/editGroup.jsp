<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.itweiyunfan.shiyan2.DAO.impl.GroupDAOImpl" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Group" %>
<%
    // 获取请求参数 groupId
    Integer groupId = Integer.parseInt(request.getParameter("groupId"));

    // 创建 DAO 实例
    GroupDAOImpl groupDAO = new GroupDAOImpl();

    // 获取分组信息
    Group group = groupDAO.getGroupById(groupId); // 需要添加 getGroupById 方法

    if (group == null) {
        response.sendRedirect("error.jsp"); // 如果未找到分组，重定向到错误页面
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>编辑分组</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        form {
            margin-top: 20px;
        }
        input[type="text"] {
            padding: 8px;
            width: 300px;
        }
        input[type="submit"] {
            padding: 8px 12px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<h1>编辑分组</h1>
<form action="updateGroup.jsp" method="post"> <!-- 修改为更新分组的页面 -->
    <input type="hidden" name="groupId" value="<%= group.getGroupId() %>">
    <input type="text" name="groupName" value="<%= group.getGroupName() %>" required placeholder="输入新的分组名称">
    <input type="submit" value="更新分组">
</form>
<a href="groups.jsp">返回我的分组</a> <!-- 假设有一个 groups.jsp 页面用于返回分组列表 -->
</body>
</html>
