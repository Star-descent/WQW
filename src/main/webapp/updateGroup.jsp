<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.itweiyunfan.shiyan2.DAO.impl.GroupDAOImpl" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Group" %>
<%
    // 获取请求参数
    String groupId = request.getParameter("groupId");
    String groupName = request.getParameter("groupName");

    // 创建 DAO 实例
    GroupDAOImpl groupDAO = new GroupDAOImpl();

    // 创建新的 Group 对象
    Group group = new Group();
    group.setGroupId(groupId);
    group.setGroupName(groupName);

    // 调用 DAO 方法更新分组
    groupDAO.updateGroup(group);

    // 重定向回分组管理页面
    response.sendRedirect("groups.jsp"); // 假设有一个 groups.jsp 页面用于展示分组列表
%>
