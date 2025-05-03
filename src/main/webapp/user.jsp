<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Users" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Education" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Emails" %>
<%@ page import="java.util.List" %>

<%
    Users user = (Users) request.getAttribute("user");
    Education education = (Education) request.getAttribute("education");
    List<Emails> emails = (List<Emails>) request.getAttribute("emails");

    if (user == null) {
        response.sendRedirect("login.jsp"); // 未登录重定向
    }
    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户信息</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <style>
        /* 页面样式 */
        body { font-family: 'Roboto', sans-serif; background-color: #f7f9fc; margin: 0; padding: 20px; }
        .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
        h1 { text-align: center; color: #333; }
        label { display: block; margin-top: 10px; font-weight: bold; }
        input, select { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 5px; }
        button { padding: 10px 20px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; width: 100%; }
        button:hover { background-color: #0056b3; }
        .modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); }
        .modal-content { background-color: #fff; margin: 10% auto; padding: 20px; width: 50%; max-height: 80%; overflow-y: auto; border-radius: 10px; }
        .close { color: #aaa; float: right; font-size: 28px; cursor: pointer; }
        .close:hover { color: #000; }
    </style>
</head>
<body>

<div class="container">
    <h1>用户信息</h1>
    <div id="viewMode">
        <p><strong>姓名:</strong> <span id="viewName"><%= user.getName() %></span></p>
        <p><strong>性别:</strong> <span id="viewGender"><%= user.getGender() %></span></p>
        <p><strong>生日:</strong> <span id="viewBirthDate"><%= user.getBirthDate() %></span></p>
        <p><strong>邮箱:</strong> <span id="viewEmail"><%= user.getEmail() %></span></p>
        <p><strong>密码:</strong> <span id="viewPassword"><%= user.getPassword() %></span></p>
        <div class="btn-container">
            <button onclick="enterEditMode()">编辑信息</button>
            <button onclick="openEmailModal()">管理电子邮箱</button>
            <button onclick="openEducationModal()">查看教育经历</button> <!-- 新增按钮 -->
        </div>
    </div>

    <!-- 邮箱管理模态框 -->
    <div id="emailModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEmailModal()">&times;</span>
            <h2>电子邮箱管理</h2>
            <ul id="emailList">
                <% if (emails != null) {
                    for (Emails email : emails) { %>
                <li>
                    <%= email.getEmail() %>
                    <% if (email.getIsUsername()) { %>
                    (用户名)
                    <% } %>
                    <button onclick="setAsUsername('<%= email.getEmailId() %>')">设为用户名</button>
                    <button onclick="deleteEmail('<%= email.getEmailId() %>')">删除</button>
                </li>
                <% } } %>
            </ul>
            <h3>添加新邮箱</h3>
            <input type="email" id="newEmail" placeholder="请输入新邮箱">
            <button onclick="addEmail()">添加邮箱</button>
        </div>
    </div>

    <!-- 教育经历模态框 -->
    <div id="educationModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEducationModal()">&times;</span>
            <h2>教育经历</h2>
            <div id="educationDetails">
                <%
                    if (education != null) { %>
                <div>
                    <p><strong>学校:</strong> <%= education.getSchoolName() %></p>
                    <p><strong>学位:</strong> <%= education.getDegree() %></p>
                    <p><strong>层次:</strong> <%= education.getLevel() %></p>
                    <p><strong>开始日期:</strong> <%= education.getStartDate() %></p>
                    <p><strong>结束日期:</strong> <%= education.getEndDate() %></p>
                    <button onclick="editEducation('<%= education.getEducationId() %>', '<%= education.getSchoolName() %>', '<%= education.getDegree() %>', '<%= education.getLevel() %>', '<%= education.getStartDate() %>', '<%= education.getEndDate() %>')">编辑</button>
                    <button onclick="deleteEducation()">删除</button>
                </div>
                <% } else { %>
                <p>没有教育经历记录。</p>
                <button onclick="openAddEducationModal()">添加教育经历</button>
                <% }
                %>
            </div>
        </div>
    </div>

    <!-- 添加/编辑教育经历模态框 -->
    <div id="educationEditModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEducationEditModal()">&times;</span>
            <h2 id="educationModalTitle">添加教育经历</h2>
            <input type="hidden" id="educationId">
            <label for="schoolName">学校名称:</label>
            <input type="text" id="schoolName" required>
            <label for="degree">学位:</label>
            <input type="text" id="degree" required>
            <label for="level">层次:</label>
            <input type="text" id="level" required>
            <label for="startDate">开始日期:</label>
            <input type="date" id="startDate" required>
            <label for="endDate">结束日期:</label>
            <input type="date" id="endDate" required>
            <button onclick="saveEducation()">保存</button>
        </div>
    </div>

    <!-- 编辑模式 -->
    <div id="editMode" style="display: none;">
        <label for="editName">姓名:</label>
        <input type="text" id="editName" value="<%= user.getName() %>" required>

        <label for="editGender">性别:</label>
        <select id="editGender">
            <option value="男" <%= user.getGender().equals("男") ? "selected" : "" %>>男</option>
            <option value="女" <%= user.getGender().equals("女") ? "selected" : "" %>>女</option>
        </select>

        <label for="editBirthDate">生日:</label>
        <input type="date" id="editBirthDate" value="<%= user.getBirthDate() %>" required>

        <label for="editEmail">邮箱:</label>
        <input type="email" id="editEmail" value="<%= user.getEmail() %>" required>

        <label for="editPassword">密码:</label>
        <input type="text" id="editPassword" value="<%= user.getPassword() %>" required> <!-- 明文显示密码 -->

        <div class="btn-container">
            <button onclick="saveUserInfo()">保存</button>
            <button onclick="cancelEdit()">取消</button>
        </div>
        <p class="alert" id="errorMessage" style="display: none;">保存失败，请稍后再试。</p>
    </div>
</div>

<script>

    // 进入编辑模式
    function enterEditMode() {
        document.getElementById('viewMode').style.display = 'none';
        document.getElementById('editMode').style.display = 'block';
    }

    // 取消编辑模式
    function cancelEdit() {
        document.getElementById('viewMode').style.display = 'block';
        document.getElementById('editMode').style.display = 'none';
    }

    // 保存用户信息
    function saveUserInfo() {
        const name = document.getElementById('editName').value;
        const gender = document.getElementById('editGender').value;
        const birthDate = document.getElementById('editBirthDate').value;
        const email = document.getElementById('editEmail').value;
        const password = document.getElementById('editPassword').value;

        fetch('<%= contextPath %>/UserServlet', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, gender, birthDate, email, password })
        })
            .then(response => {
                if (response.ok) {
                    location.reload(); // 成功后重新加载页面
                } else {
                    document.getElementById('errorMessage').style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('errorMessage').style.display = 'block';
            });
    }

    function openEducationModal() {
        document.getElementById('educationModal').style.display = 'block';
    }

    function closeEducationModal() {
        document.getElementById('educationModal').style.display = 'none';
    }

    function openAddEducationModal() {
        document.getElementById('educationModalTitle').textContent = '添加教育经历';
        document.getElementById('educationId').value = '';
        document.getElementById('schoolName').value = '';
        document.getElementById('degree').value = '';
        document.getElementById('level').value = '';
        document.getElementById('startDate').value = '';
        document.getElementById('endDate').value = '';
        document.getElementById('educationEditModal').style.display = 'block';
    }

    function editEducation(id, schoolName, degree, level, startDate, endDate) {
        document.getElementById('educationId').value = id;
        document.getElementById('schoolName').value = schoolName;
        document.getElementById('degree').value = degree;
        document.getElementById('level').value = level;
        document.getElementById('startDate').value = startDate;
        document.getElementById('endDate').value = endDate;
        document.getElementById('educationModalTitle').textContent = '编辑教育经历';
        document.getElementById('educationEditModal').style.display = 'block';
    }

    function closeEducationEditModal() {
        document.getElementById('educationEditModal').style.display = 'none';
    }

    function saveEducation() {
        const id = document.getElementById('educationId').value;
        const schoolName = document.getElementById('schoolName').value;
        const degree = document.getElementById('degree').value;
        const level = document.getElementById('level').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        const data = { schoolName, degree, level, startDate, endDate };
        const url = '<%= contextPath %>/EducationServlet';
        const method = id ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(() => {
            closeEducationEditModal();
            location.reload();
        });
    }

    function deleteEducation() {
        if (confirm("确定要删除此教育经历吗？")) {
            fetch('<%= contextPath %>/EducationServlet', { method: 'DELETE' })
                .then(() => location.reload());
        }
    }

    function openEducationModal() {
        document.getElementById('educationModal').style.display = 'block';
    }

    function closeEducationModal() {
        document.getElementById('educationModal').style.display = 'none';
    }

    // 打开和关闭邮箱管理模态框
    function openEmailModal() {
        const userId = '<%= user.getUserId() %>';  // 获取当前用户的ID
        fetch('<%= contextPath %>/EmailServlet?userId=' + userId)
            .then(response => response.json())
            .then(data => {
                const emailList = document.getElementById('emailList');
                emailList.innerHTML = '';  // 清空之前的内容

                data.forEach(email => {
                    const li = document.createElement('li');
                    li.innerHTML = email.email + (email.isUsername ? ' (用户名)' : '') +
                        ' <button onclick="setAsUsername(' + email.emailId + ')">设为用户名</button>' +
                        ' <button onclick="deleteEmail(' + email.emailId + ')">删除</button>';
                    emailList.appendChild(li);
                });
            });

        document.getElementById('emailModal').style.display = 'block';
    }

    function closeEmailModal() {
        document.getElementById('emailModal').style.display = 'none';
    }

    // 添加新邮箱
    function addEmail() {
        const newEmail = document.getElementById('newEmail').value;
        const userId = '<%= user.getUserId() %>';  // 获取当前用户的ID

        // 前端验证邮箱是否为空
        if (!newEmail) {
            alert('请输入有效的邮箱');
            return;
        }

        fetch('<%= contextPath %>/EmailServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
                action: 'add',
                userId: userId,
                email: newEmail
            })
        }).then(response => {
            if (response.ok) {
                openEmailModal();  // 刷新邮箱列表
            } else if (response.status === 409) { // 服务器返回409表示邮箱重复
                alert('该邮箱已存在，请使用其他邮箱');
            } else {
                alert('添加邮箱失败');
            }
        }).catch(error => {
            console.error('Error:', error);
        });
    }

    // 设置某个邮箱为用户名
    function setAsUsername(emailId) {
        const userId = '<%= user.getUserId() %>';  // 获取当前用户的ID

        fetch('<%= contextPath %>/EmailServlet?action=setAsUsername&emailId=' + emailId + '&userId=' + userId, {
            method: 'PUT'
        }).then(response => {
            if (response.ok) {
                openEmailModal();  // 刷新邮箱列表
            } else {
                alert('设置用户名失败');
            }
        });
    }

    // 删除邮箱
    function deleteEmail(emailId) {
        if (confirm("确定要删除此邮箱吗？")) {
            fetch('<%= contextPath %>/EmailServlet?action=delete&emailId=' + emailId, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    openEmailModal();  // 刷新邮箱列表
                } else {
                    alert('删除邮箱失败');
                }
            });
        }
    }

</script>

</body>
</html>
