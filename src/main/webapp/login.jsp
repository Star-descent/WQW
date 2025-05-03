<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>登录页面</title>
    <link rel="stylesheet" href="style.css"> <!-- 引入外部CSS文件 -->
    <script src="script.js"></script> <!-- 引入外部JavaScript文件 -->
</head>
<body>
<div class="login-container">
    <form action="LoginServlet" method="post" class="login-form">
        <h2>用户登录</h2>

        <!-- 登录成功或失败的提示消息 -->
        <div class="login-message">
            <%
                Object message = session.getAttribute("loginMessage");
                if (message != null) {
            %>
            <div><%= message.toString() %></div>
            <%
                    session.removeAttribute("loginMessage");
                }
            %>
        </div>

        <div class="form-group">
            <label for="name">姓名:</label> <!-- 修改了字段名称 -->
            <input type="text" id="name" name="name"
            value="<%= request.getCookies() != null ? getCookieValue(request.getCookies(), "name") : "" %>" autofocus>
        </div>
        <div class="form-group">
            <label for="password">密码:</label>
            <input type="password" id="password" name="password"
                   value="<%= request.getCookies() != null ? getCookieValue(request.getCookies(), "password") : "" %>">
        </div>
        <div class="form-check">
            <input type="checkbox" id="rememberMe" name="rememberMe">
            <label for="rememberMe">记住密码</label>
        </div>
        <div class="form-submit">
            <input type="submit" value="登录">
        </div>
    </form>
</div>
</body>
</html>

<%!
    private String getCookieValue(Cookie[] cookies, String cookieName) {
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
%>
