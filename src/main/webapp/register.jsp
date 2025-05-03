<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>注册页面</title>
    <link rel="stylesheet" href="style.css"> <!-- 引入外部CSS文件 -->
    <script src="script.js"></script> <!-- 引入外部JavaScript文件 -->
</head>
<body>
<div class="register-container">
    <form action="RegisterServlet" method="post" class="register-form">
        <h2>用户注册</h2>

        <!-- 注册成功或失败的提示消息 -->
        <div class="register-message">
            <%
                Object message = session.getAttribute("registerMessage");
                if (message != null) {
            %>
            <div><%= message.toString() %></div>
            <%
                    session.removeAttribute("registerMessage");
                }
            %>
        </div>

        <div class="form-group">
            <label for="name">姓名:</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div class="form-group">
            <label for="gender">性别:</label>
            <select id="gender" name="gender" required>
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
            </select>
        </div>
        <div class="form-group">
            <label for="birthDate">出生日期:</label>
            <input type="date" id="birthDate" name="birthDate" required>
        </div>
        <div class="form-group">
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="email">邮箱:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-submit">
            <input type="submit" value="注册">
        </div>
    </form>
</div>
</body>
</html>
