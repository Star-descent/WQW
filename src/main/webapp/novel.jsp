<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.*" %>
<%@ page import="java.lang.Character" %>
<%
    // 获取请求属性
    List<Novel> novels = (List<Novel>) request.getAttribute("novels");
    List<Users> users = (List<Users>) request.getAttribute("users");
    List<Group> groups = (List<Group>) request.getAttribute("gps");
    List<GroupedNovels> groupedNovels = (List<GroupedNovels>) request.getAttribute("groupedNovels");
    Map<Integer, List<Character>> novelCharacterMap = (Map<Integer, List<Character>>) request.getAttribute("novelCharacterMap"); // 小说和角色的映射

    List<UserNovelWordCount> userWordCounts = (List<UserNovelWordCount>) request.getAttribute("userWordCounts"); // 获取用户小说字数统计

    // 确保 users 和 groupedNovels 不为 null
    if (users == null) {
        users = new ArrayList<>(); // 初始化为一个空列表
    }
    if (groupedNovels == null) {
        groupedNovels = new ArrayList<>(); // 初始化为空列表
    }
    if (groups == null) {
        groups = new ArrayList<>(); // 初始化为一个空列表
    }
    if (novelCharacterMap == null){
        novelCharacterMap = new HashMap<>();
    }

    Integer currentUserId = (Integer) session.getAttribute("userId"); // 获取当前登录用户ID
    int totalWords = 0;

    // 查找当前用户的小说总字数
    if (userWordCounts != null && currentUserId != null) {
        for (UserNovelWordCount userWordCount : userWordCounts) {
            if (userWordCount.getUserId() == currentUserId) {
                totalWords = userWordCount.getTotalWords();
                break;
            }
        }
    }

    String contextPath = request.getContextPath(); // 获取应用的上下文路径

    // 创建一个map来保存小说ID到分组ID的映射
    Map<Integer, List<Integer>> novelToGroupMap = new HashMap<>();
    for (GroupedNovels gn : groupedNovels) {
        novelToGroupMap.computeIfAbsent(gn.getNovelId(), k -> new ArrayList<>()).add(gn.getGroupId());
    }

    // 创建一个map来保存分组ID到分组名称的映射
    Map<Integer, String> groupIdToNameMap = new HashMap<>();
    for (Group group : groups) {
        groupIdToNameMap.put(group.getGroupId(), group.getGroupName());
    }

    Gson gson = new Gson();
    String novelToGroupMapJson = gson.toJson(novelToGroupMap);
    String groupIdToNameMapJson = gson.toJson(groupIdToNameMap);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>小说管理</title>
    <!-- 引入 Google 字体 -->
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f7f9fc;
            margin: 0;
            padding: 20px;
        }
        .word-count {
            position: absolute;
            top: 10px;
            left: 20px;
            font-size: 18px;
            color: #333;
            font-weight: bold;
        }
        h1 {
            text-align: center;
            color: #333;
        }
        h2 {
            color: #333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 10px;
            overflow: hidden;
        }
        th, td {
            padding: 15px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        button {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        button:hover {
            background-color: #0056b3;
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
            width: 50%;
            max-height: 80%;
            overflow: auto;
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
        .close:hover {
            color: #000;
            text-decoration: none;
        }
        .btn-container {
            text-align: center;
            margin-bottom: 20px;
        }
        .btn-container form {
            display: inline-block;
        }
        .modal input, .modal textarea {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        /* 响应式设计 */
        @media (max-width: 768px) {
            .modal-content {
                width: 90%;
            }
            table th, table td {
                padding: 10px;
            }
        }
    </style>
</head>
<body>

<!-- 显示用户小说总字数 -->
<div class="word-count">
    总字数：<%= totalWords %> 字
</div>

<div class="container">
    <h1>小说管理</h1>

    <!-- 新增按钮：前往所有小说页面 -->
    <div class="btn-container">
        <form action="<%= contextPath %>/NovelServlet/all" method="get">
            <button type="submit">查看所有小说</button>
        </form>
        <button onclick="openAddNovelModal()">添加小说</button>
    </div>

    <!-- 小说列表表格 -->
    <table>
        <tr>
            <th>小说标题</th>
            <th>作者</th>
            <th>分组归属</th>
            <th>操作</th>
        </tr>
        <%
            if (novels != null && !novels.isEmpty()) {
                for (Novel novel : novels) {
                    String authorName = ""; // 初始化作者名
                    for (Users user : users) {
                        if (user.getUserId() == novel.getUserId()) {
                            authorName = user.getName(); // 查找对应的作者名
                            break;
                        }
                    }
//
//                    // 获取小说对应的分组ID
//                    List<Integer> groupIds = novelToGroupMap.getOrDefault(novel.getNovelId(), new ArrayList<>());
//                    String groupNames = "未分组"; // 默认显示为"未分组"
//                    if (!groupIds.isEmpty()) {
//                        List<String> groupNamesList = new ArrayList<>();
//                        for (Integer groupId : groupIds) {
//                            String groupName = groupIdToNameMap.get(groupId);
//                            if (groupName != null) {
//                                groupNamesList.add(groupName);
//                            }
//                        }
//                        groupNames = String.join(", ", groupNamesList); // 将多个分组名用逗号分隔
//                    }
        %>
        <tr>
            <td><a href="javascript:void(0);" onclick="showContent('<%= novel.getContent().replace("\n", "\\n").replace("'", "\\'") %>')"><%= novel.getTitle() %></a></td>
            <td><%= authorName %></td>
            <td>
                <!-- 分组归属按钮 -->
                <button onclick="showGroups(<%= novel.getNovelId() %>)">查看分组</button>
            </td>
            <td>
                <button onclick="editNovel(<%= novel.getNovelId() %>, '<%= novel.getTitle() %>', '<%= novel.getContent().replace("\n", "\\n").replace("'", "\\'") %>')">编辑</button>
                <button onclick="deleteNovel(<%= novel.getNovelId() %>)">删除</button>
                <button onclick="showCharacters(<%= novel.getNovelId() %>)">角色信息</button>
            </td>

        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="4" style="text-align: center;">没有找到小说。</td>
        </tr>
        <%
            }
        %>
    </table>
</div>

<!-- 显示角色信息的模态弹窗 -->
<div id="characterModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeCharacterModal()">&times;</span>
        <h2>角色信息</h2>
        <ul id="characterList"></ul>

        <!-- 添加角色 -->
        <h3>添加新角色</h3>
        <form id="addCharacterForm" onsubmit="return addCharacter()">
            <input type="hidden" id="novelId" name="novelId">
            <label for="characterName">角色名称：</label>
            <input type="text" id="characterName" name="name" required>
            <label for="characterDescription">角色描述：</label>
            <textarea id="characterDescription" name="description" required></textarea>
            <button type="submit">添加角色</button>
        </form>
    </div>
</div>

<!-- 分组归属信息的模态弹窗 -->
<div id="groupModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeGroupModal()">&times;</span>
        <h2>分组归属信息</h2>
        <ul id="groupList"></ul>
    </div>
</div>


<!-- 添加小说的模态弹窗 -->
<div id="addNovelModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeAddNovelModal()">&times;</span>
        <h2>添加新小说</h2>
        <label for="addTitle">标题:</label>
        <input type="text" id="addTitle" required>
        <label for="addContent">内容:</label>
        <textarea id="addContent" rows="6" required></textarea>
        <br><br>
        <button onclick="addNovel()">保存</button>
    </div>
</div>

<!-- 显示小说内容的模态弹窗 -->
<div id="contentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>小说内容</h2>
        <div id="novelContent"></div>
    </div>
</div>

<!-- 编辑小说的模态弹窗 -->
<div id="editModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeEditModal()">&times;</span>
        <h2>编辑小说</h2>
        <input type="hidden" id="editNovelId">
        <label for="editTitle">标题:</label>
        <input type="text" id="editTitle" required>
        <label for="editContent">内容:</label>
        <textarea id="editContent" rows="6" required></textarea>
        <br><br>
        <button onclick="saveEdit()">保存</button>
    </div>
</div>

<script>
    // 显示添加小说模态弹窗
    function openAddNovelModal() {
        document.getElementById('addNovelModal').style.display = 'block';
    }

    // 关闭添加小说模态弹窗
    function closeAddNovelModal() {
        document.getElementById('addNovelModal').style.display = 'none';
    }

    // 添加小说
    function addNovel() {
        const title = document.getElementById('addTitle').value;
        const content = document.getElementById('addContent').value;

        // 使用 URLSearchParams 构造请求体
        const params = new URLSearchParams();
        params.append('title', title);
        params.append('content', content);

        fetch('<%= contextPath %>/NovelServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(response => {
                if (response.ok) {
                    location.reload(); // 成功后重新加载页面
                } else {
                    alert("添加小说失败");
                }
            })
            .catch(error => console.error('Error:', error));
    }

    const novelToGroupMap = <%= novelToGroupMapJson %>;
    const groupIdToNameMap = <%= groupIdToNameMapJson %>;

    // 显示小说的分组归属信息，并添加删除按钮
    function showGroups(novelId) {
        const groupIds = novelToGroupMap[novelId] || [];
        const groupList = document.getElementById('groupList');
        groupList.innerHTML = ''; // 清空之前的内容

        if (groupIds.length === 0) {
            groupList.innerHTML = '<li>该小说没有分组归属。</li>';
        } else {
            groupIds.forEach(groupId => {
                const groupName = groupIdToNameMap[groupId] || '未知分组';
                const li = document.createElement('li');
                li.innerHTML = groupName + ' <button onclick="deleteGroupFromNovel(' + novelId + ', ' + groupId + ')">删除</button>';
                groupList.appendChild(li);
            });
        }

        document.getElementById('groupModal').style.display = 'block';
    }

    // 删除小说的特定分组
    function deleteGroupFromNovel(novelId, groupId) {
        if (confirm("确定要删除该小说的分组吗？")) {
            fetch('<%= contextPath %>/GroupedNovelsServlet/' + novelId + '/' + groupId, {  // 使用字符串拼接代替模板字符串
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert("分组删除成功");
                        showGroups(novelId); // 更新显示的分组列表
                    } else {
                        alert("删除分组失败");
                    }
                })
                .catch(error => console.error('Error:', error));
        }
    }

    // 关闭分组归属模态弹窗
    function closeGroupModal() {
        document.getElementById('groupModal').style.display = 'none';
    }


    // 显示小说内容
    function showContent(content) {
        document.getElementById('novelContent').textContent = content;
        document.getElementById('contentModal').style.display = 'block';
    }

    // 关闭内容模态弹窗
    function closeModal() {
        document.getElementById('contentModal').style.display = 'none';
    }

    // 编辑小说
    function editNovel(novelId, title, content) {
        document.getElementById('editNovelId').value = novelId;
        document.getElementById('editTitle').value = title;
        document.getElementById('editContent').value = content;
        document.getElementById('editModal').style.display = 'block';
    }

    // 关闭编辑模态弹窗
    function closeEditModal() {
        document.getElementById('editModal').style.display = 'none';
    }

    // 保存编辑
    function saveEdit() {
        const novelId = document.getElementById('editNovelId').value;
        const title = document.getElementById('editTitle').value;
        const content = document.getElementById('editContent').value;

        fetch('<%= contextPath %>/NovelServlet/' + novelId, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ title: title, content: content })
        })
            .then(response => {
                if (response.ok) {
                    location.reload(); // 更新成功后重新加载页面
                } else {
                    alert("编辑失败");
                }
            })
            .catch(error => console.error('Error:', error));
    }

    // 删除小说
    function deleteNovel(novelId) {
        if (confirm("确定要删除该小说吗？")) {
            fetch('<%= contextPath %>/NovelServlet/' + novelId, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        location.reload(); // 删除成功后重新加载页面
                    } else {
                        alert("删除失败");
                    }
                })
                .catch(error => console.error('Error:', error));
        }
    }

    let currentNovelId = 0;

    // 显示角色信息的模态框
    function showCharacters(novelId) {
        currentNovelId = novelId;
        document.getElementById('novelId').value = novelId; // 设置隐藏域中的小说ID
        fetch('CharacterServlet?action=getCharacters&novelId=' + novelId)
            .then(response => response.json())
            .then(data => {
                const characterList = document.getElementById('characterList');
                characterList.innerHTML = ''; // 清空之前的内容

                if (data.length === 0) {
                    characterList.innerHTML = '<li>该小说没有角色信息。</li>';
                } else {
                    data.forEach(character => {
                        const li = document.createElement('li');
                        li.innerHTML = '<strong>' + character.name + '</strong>: ' + character.description +
                            ' <button onclick="deleteCharacter(' + character.characterId + ')">删除</button>' +
                            ' <button onclick="editCharacter(' + character.characterId + ', \'' + character.name + '\', \'' + character.description + '\')">编辑</button>';
                        characterList.appendChild(li);
                    });
                }

                document.getElementById('characterModal').style.display = 'block';
            });
    }

    // 添加角色
    function addCharacter() {
        const name = document.getElementById('characterName').value;
        const description = document.getElementById('characterDescription').value;

        fetch('CharacterServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
                action: 'add',
                novelId: currentNovelId,
                name: name,
                description: description
            })
        }).then(response => {
            if (response.ok) {
                showCharacters(currentNovelId); // 重新加载角色信息
            } else {
                alert('添加角色失败');
            }
        });

        return false; // 阻止表单默认提交
    }

    // 编辑角色
    function editCharacter(characterId, name, description) {
        const newName = prompt("编辑角色名称", name);
        const newDescription = prompt("编辑角色描述", description);

        if (newName !== null && newDescription !== null) {
            fetch('CharacterServlet', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({
                    action: 'update',
                    characterId: characterId,
                    name: newName,
                    description: newDescription
                })
            }).then(response => {
                if (response.ok) {
                    showCharacters(currentNovelId); // 重新加载角色信息
                } else {
                    alert('更新角色失败');
                }
            });
        }
    }

    // 删除角色
    function deleteCharacter(characterId) {
        if (confirm("确定要删除该角色吗？")) {
            fetch('CharacterServlet?action=delete&characterId=' + characterId)
                .then(response => {
                    if (response.ok) {
                        showCharacters(currentNovelId); // 重新加载角色信息
                    } else {
                        alert('删除角色失败');
                    }
                });
        }
    }

    // 关闭角色信息模态框
    function closeCharacterModal() {
        document.getElementById('characterModal').style.display = 'none';
    }
</script>

</body>
</html>
