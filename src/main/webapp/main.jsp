<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Group" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Novel" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.GroupedNovels" %>
<%@ page import="com.google.gson.Gson" %>
<%
    List<Group> groups = (List<Group>) request.getAttribute("gps"); // 获取分组数据
    List<Novel> novels = (List<Novel>) request.getAttribute("novels"); // 获取小说数据
    List<GroupedNovels> groupedNovels = (List<GroupedNovels>) request.getAttribute("groupedNovels");
    String contextPath = request.getContextPath();
    String errorMessage = (String) request.getAttribute("error");
    int groupCount = (groups != null) ? groups.size() : 0;

    // 创建一个map来保存分组ID到小说ID的映射
    Map<Integer, List<Integer>> groupToNovelsMap = new HashMap<>();
    for (GroupedNovels gn : groupedNovels) {
        groupToNovelsMap.computeIfAbsent(gn.getGroupId(), k -> new ArrayList<>()).add(gn.getNovelId());
    }

    Map<Integer, String> novelIdToNameMap = new HashMap<>();
    for (Novel novel : novels) {
        novelIdToNameMap.put(novel.getNovelId(), novel.getTitle());
    }

    Map<Integer, String> novelIdToContentMap = new HashMap<>();
    for (Novel novel : novels) {
        novelIdToContentMap.put(novel.getNovelId(), novel.getContent()); // 假设 getContent() 返回小说内容
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>分组管理</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f0f0f0; /* 背景色 */
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 阴影 */
        }
        th, td {
            padding: 12px;
            border: 1px solid #ddd; /* 边框 */
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        h1, h2 {
            color: #333;
            font-weight: normal;
        }
        form {
            margin-bottom: 20px;
        }
        form input[type="text"], form input[type="submit"] {
            padding: 10px;
            margin-right: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        form input[type="submit"] {
            background-color: #4CAF50; /* 按钮背景色 */
            color: white;
            border: none;
            cursor: pointer;
        }
        form input[type="submit"]:hover {
            background-color: #45a049; /* 按钮 hover 背景色 */
        }
        button {
            padding: 8px 12px;
            margin-right: 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #f0f0f0; /* 按钮 hover 背景色 */
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
        }
        .modal-content {
            background-color: #fff;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 30%;
            box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
            border-radius: 10px;
        }
        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }
        .close:hover, .close:focus {
            color: #000;
            text-decoration: none;
        }
    </style>
</head>
<body>

<h1>分组管理</h1>

<h2>添加新分组</h2>

<!-- 显示错误信息 -->
<% if (errorMessage != null) { %>
<p style="color: red;"><%= errorMessage %></p>
<% } %>

<form action="<%= contextPath %>/MainServlet" method="post">
    <input type="text" name="groupName" required placeholder="输入分组名称">
    <input type="submit" name="action" value="添加分组">
</form>

<!-- 前往用户界面的按钮 -->
<form action="<%= contextPath %>/UserServlet" method="get">
    <input type="submit" value="前往用户管理界面">
</form>

<!-- 前往小说页面按钮 -->
<form action="<%= contextPath %>/NovelServlet" method="get">
    <input type="submit" value="前往个人小说界面">
</form>

<!-- 分组表格 -->
<table>
    <thead>
    <tr>
        <th>分组名称</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <% if (groups != null && !groups.isEmpty()) { %>
    <% for (Group group : groups) { %>
    <tr>
        <td><a href="javascript:void(0);" onclick="showGroupNovels('<%= group.getGroupId() %>')"><%= group.getGroupName() %></a></td>
        <td>
            <button type="button" onclick="deleteGroup('<%= group.getGroupId() %>')">删除</button>
            <button type="button" onclick="openModal('<%= group.getGroupId() %>', '<%= group.getGroupName() %>')">编辑</button>
        </td>
    </tr>
    <% } %>
    <% } else { %>
    <tr><td colspan="2">没有找到分组。</td></tr>
    <% } %>
    </tbody>
</table>

<div id="editModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>编辑分组</h2>
        <input type="hidden" id="modalGroupId">
        <label for="modalGroupName">分组名称:</label>
        <input type="text" id="modalGroupName" required>
        <br><br>
        <button type="button" onclick="editGroup()">保存</button>
    </div>
</div>

<!-- 显示小说的模态框 -->
<div id="novelModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>分组内小说</h2>
        <ul id="novelList"></ul>
    </div>
</div>

<!-- 小说内容模态框 -->
<div id="novelContentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeNovelContentModal()">&times;</span>
        <div id="novelContentModalBody">
            <!-- 小说内容将动态填充到这里 -->
        </div>
    </div>
</div>


<script>
    const groupCount = <%= groupCount %>;

    document.querySelector('form[action="<%= contextPath %>/MainServlet"]').addEventListener('submit', function(event) {
        if (groupCount >= 7) {
            alert("分组数量不能超过7个！");
            event.preventDefault(); // 阻止表单提交
        }
    });

    function openModal(groupId, groupName) {
        document.getElementById('modalGroupId').value = groupId;
        document.getElementById('modalGroupName').value = groupName;
        document.getElementById('editModal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('editModal').style.display = 'none';
    }

    function deleteGroup(groupId) {
        // 检查该分组内是否有小说
        const novelIds = groupToNovelsMap[groupId] || []; // 获取该分组对应的小说ID列表

        if (novelIds.length > 0) {
            // 如果分组内有小说，显示提示信息并取消删除操作
            alert("该分组内有小说，无法删除！");
            return;
        }

        // 如果分组内没有小说，执行删除操作
        if (confirm("确定要删除该分组吗？")) {
            const url = '<%= contextPath %>/MainServlet/' + groupId;
            fetch(url, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert("删除失败");
                    }
                })
                .catch(error => console.error('Error:', error));
        }
    }


    function editGroup() {
        const groupId = document.getElementById('modalGroupId').value;
        const groupName = document.getElementById('modalGroupName').value;
        const url = '<%= contextPath %>/MainServlet/' + groupId;
        fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ groupName: groupName })
        })
            .then(response => {
                if (response.ok) {
                    closeModal();
                    location.reload();
                } else {
                    alert("编辑失败");
                }
            })
            .catch(error => console.error('Error:', error));
    }

    const groupToNovelsMap = <%= new Gson().toJson(groupToNovelsMap) %>; // 将分组到小说的映射转换为 JSON
    const novelIdToNameMap = <%= new Gson().toJson(novelIdToNameMap) %>;
    const novelIdToContentMap = <%= new Gson().toJson(novelIdToContentMap) %>; // 小说ID到内容的映射

    function showGroupNovels(groupId) {
        const novelIds = groupToNovelsMap[groupId] || []; // 获取该分组对应的小说 ID
        const novelListElement = document.getElementById('novelList');
        novelListElement.innerHTML = ''; // 清空之前的内容

        if (novelIds.length === 0) {
            novelListElement.innerHTML = '<li>该分组没有小说</li>';
        } else {
            novelIds.forEach(novelId => {
                const li = document.createElement('li');
                const novelName = novelIdToNameMap[novelId]; // 显示小说名称
                li.innerHTML =
                    '<span>' + novelName + '</span>' +
                    '<button onclick="viewNovel(' + novelId + ')">查看</button>' +
                    '<button onclick="deleteNovelFromGroup(' + novelId + ', ' + groupId + ')">删除</button>';
                novelListElement.appendChild(li);
            });
        }

        document.getElementById('novelModal').style.display = 'block'; // 显示模态框
    }

    // 查看小说内容，直接从 novelIdToContentMap 获取内容
    function viewNovel(novelId) {
        const novelTitle = novelIdToNameMap[novelId]; // 获取小说标题
        const novelContent = novelIdToContentMap[novelId]; // 获取小说内容

        // 将小说内容显示在模态框中
        const modalContent = document.getElementById('novelContentModalBody');
        modalContent.innerHTML = '<h3>' + novelTitle + '</h3><p>' + novelContent + '</p>';
        document.getElementById('novelContentModal').style.display = 'block'; // 显示模态框
    }


    // 删除小说，从 grouped_novels 表中移除
    function deleteNovelFromGroup(novelId, groupId) {
        if (confirm("确定要将小说从该分组中删除吗？")) {
            const url = '<%= contextPath %>/GroupedNovelsServlet/' + novelId + '/' + groupId;
            fetch(url, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert("小说已从分组中删除");
                        showGroupNovels(groupId); // 重新加载该分组的小说
                    } else {
                        alert("删除失败");
                    }
                })
                .catch(error => console.error('Error:', error));
        }
    }

    // 关闭小说内容的模态框
    function closeNovelContentModal() {
        document.getElementById('novelContentModal').style.display = 'none';
    }

    // 关闭小说列表的模态框
    function closeModal() {
        document.getElementById('novelModal').style.display = 'none';
    }
</script>

</body>
</html>
