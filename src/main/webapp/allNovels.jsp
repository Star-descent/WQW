<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Novel" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Users" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Group" %>
<%@ page import="com.itweiyunfan.shiyan2.Pojo.Comment" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%
    // 获取请求属性
    List<Novel> novels = (List<Novel>) request.getAttribute("novels");
    List<Users> users = (List<Users>) request.getAttribute("users");
    List<Group> groups = (List<Group>) request.getAttribute("gps");
    List<Comment> comments = (List<Comment>) request.getAttribute("comments");

    // 确保 users 不为 null
    if (users == null) {
        users = new ArrayList<>(); // 初始化为一个空列表
    }

    Map<Integer, String> userIdToNameMap = new HashMap<>();
    for (Users user : users) {
        userIdToNameMap.put(user.getUserId(), user.getName());
    }

    String contextPath = request.getContextPath(); // 获取应用的上下文路径
    String userIdToNameJson = new Gson().toJson(userIdToNameMap);
%>
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>所有小说</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f7f9fc;
            margin: 0;
            padding: 20px;
        }
        h1 {
            text-align: center;
            color: #333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .novel-list {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
        }
        .novel-card {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
            padding: 20px;
            width: calc(33.333% - 20px);
            box-sizing: border-box;
            transition: transform 0.3s ease;
        }
        .novel-card:hover {
            transform: scale(1.05);
        }
        .novel-card h2 {
            margin: 0;
            font-size: 20px;
            color: #007bff;
            cursor: pointer;
        }
        .novel-card p {
            color: #555;
            margin-top: 10px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-align: center;
            transition: background-color 0.3s ease;
        }
        .btn:hover {
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
            margin: 10% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 50%;
            max-height: 80%;
            overflow-y: auto;
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
            cursor: pointer;
        }
        .small-btn {
            padding: 5px 10px; /* 调整按钮大小 */
            font-size: 12px;   /* 调整按钮文字大小 */
            background-color: #ff4d4d; /* 删除按钮颜色 */
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }

        .small-btn:hover {
            background-color: #cc0000; /* 删除按钮悬停颜色 */
        }
        .small-text {
            font-size: 12px; /* 调整回复内容的字体大小 */
            color: #555; /* 调整回复内容的颜色，确保可读性 */
        }

        @media (max-width: 768px) {
            .novel-card {
                width: calc(50% - 20px);
            }
        }
        @media (max-width: 576px) {
            .novel-card {
                width: 100%;
            }
            .modal-content {
                width: 90%;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <h1>所有小说</h1>

    <!-- 新增按钮：前往小说页面 -->
    <div style="text-align: center; margin-bottom: 20px;">
        <form action="<%= contextPath %>/NovelServlet" method="get">
            <input type="submit" class="btn" value="前往个人小说界面">
        </form>
    </div>

<%--    <div style="text-align: center; margin-bottom: 20px;">--%>
<%--        <form action="<%= contextPath %>/MainServlet"+u method="get">--%>
<%--            <input type="submit" class="btn" value="前往个人小说界面">--%>
<%--        </form>--%>
<%--    </div>--%>

    <!-- 小说卡片展示 -->
    <div class="novel-list">
        <% if (novels != null && !novels.isEmpty()) {
            for (Novel novel : novels) {
                String authorName = ""; // 初始化作者名
                for (Users user : users) {
                    if (user.getUserId() == novel.getUserId()) {
                        authorName = user.getName(); // 查找对应的作者名
                        break;
                    }
                }
        %>
        <div class="novel-card">
            <h2 onclick="showContent('<%= novel.getTitle().replace("\n", "\\n").replace("'", "\\'") %>', '<%= novel.getContent().replace("\n", "\\n").replace("'", "\\'") %>')">
                <%= novel.getTitle() %>
            </h2>
            <p><strong>作者:</strong> <%= authorName %></p>
            <p><strong>发布时间:</strong> <%= novel.getPublishDate() != null ? novel.getPublishDate() : "无" %></p>
            <p><strong>最后更新时间:</strong> <%= novel.getLastUpdate() != null ? novel.getLastUpdate() : "无" %></p>
            <button class="btn" type="button" onclick="showGroupModal(<%= novel.getNovelId() %>)">加入分组</button>
            <button class="btn" type="button" onclick="showCommentsModal(<%= novel.getNovelId() %>)">查看评论</button> <!-- 新增查看评论按钮 -->
        </div>
        <% }
        } else { %>
        <p style="text-align: center; width: 100%;">没有找到小说。</p>
        <% } %>
    </div>

    <div style="text-align: center; margin-bottom: 20px;">
        <button class="btn" onclick="showGroupedNovels()">显示已分组小说</button>
    </div>

    <!-- 已分组小说的模态框 -->
    <div id="groupedNovelsModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeGroupedNovelsModal()">&times;</span>
            <h2>已分组小说列表</h2>
            <ul id="groupedNovelsList"></ul> <!-- 这里显示已分组小说的名称 -->
        </div>
    </div>

    <!-- 加入分组的模态框 -->
    <div id="groupModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeGroupModal()">&times;</span>
            <h2>选择分组</h2>
            <form id="groupForm">
                <input type="hidden" id="novelId">
                <label for="groupId">选择分组:</label>
                <select id="groupId" required>
                    <% if (groups != null && !groups.isEmpty()) {
                        for (Group group : groups) { %>
                    <option value="<%= group.getGroupId() %>"><%= group.getGroupName() %></option>
                    <% } } else { %>
                    <option value="" disabled>没有可用的分组</option>
                    <% } %>
                </select>
                <br><br>
                <button type="button" class="btn" onclick="addToGroup()">确定</button>
            </form>
        </div>
    </div>

    <!-- 显示小说内容的模态框 -->
    <div id="contentModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeContentModal()">&times;</span>
            <h2 id="novelTitle"></h2>
            <div id="novelContent"></div>
        </div>
    </div>

    <!-- 评论模态框 -->
    <div id="commentsModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeCommentsModal()">&times;</span>
            <h2>评论列表</h2>
            <div id="commentsList"></div> <!-- 评论列表展示区域 -->

<%--            <!-- 添加评论表单 -->--%>
<%--            <div>--%>
<%--                <textarea id="newCommentContent" rows="3" placeholder="添加评论..."></textarea><br>--%>
<%--                <button class="btn" type="button" onclick="addComment()">提交评论</button>--%>
<%--            </div>--%>
        </div>
    </div>

</div>

<script>
    const userIdToNameMap = <%= userIdToNameJson %>;

    // 显示小说内容的模态框
    function showContent(title, content) {
        document.getElementById('novelTitle').innerText = title;
        document.getElementById('novelContent').innerText = content;
        document.getElementById('contentModal').style.display = 'block';
    }

    // 关闭模态框
    function closeModal() {
        document.getElementById('contentModal').style.display = 'none';
        document.getElementById('groupModal').style.display = 'none';
    }

    // 显示加入分组的模态框
    function showGroupModal(novelId) {
        document.getElementById('novelId').value = novelId;
        document.getElementById('groupModal').style.display = 'block';
    }// 显示评论的模态框

    function showCommentsModal(novelId) {
        fetch('<%= contextPath %>/CommentServlet?novelId=' + novelId)
            .then(response => response.json())
            .then(comments => {
                const commentsList = document.getElementById('commentsList');
                commentsList.innerHTML = ''; // 清空之前的评论内容

                if (comments.length === 0) {
                    commentsList.innerHTML = '<p>暂无评论</p>';
                } else {
                    comments.forEach(comment => {
                        const userName = userIdToNameMap[comment.userId] || '未知用户';
                        const commentElement = document.createElement('div');
                        commentElement.innerHTML =
                            '<p>' + userName + ' ： ' + comment.content +
                            ' <small>(' + comment.timestamp + ')</small></p>' +
                            '<button class="btn" onclick="deleteComment(' + comment.commentId + ', ' + novelId + ')">删除</button>' +
                            '<button class="btn" onclick="showReplyForm(' + comment.commentId + ')">回复</button>' + // 添加回复按钮
                            '<div id="replies-' + comment.commentId + '"></div>'; // 添加展示回复的容器
                        commentsList.appendChild(commentElement);

                        // 加载并展示该评论的回复
                        loadReplies(comment.commentId);
                    });
                }

                // 添加评论输入框和提交按钮到评论列表的末尾
                commentsList.innerHTML +=
                    '<div>' +
                    '<textarea id="newCommentContent" rows="3" placeholder="添加评论..."></textarea><br>' +
                    '<button class="btn" type="button" onclick="addComment(' + novelId + ')">提交评论</button>' +
                    '</div>';

                document.getElementById('commentsModal').style.display = 'block'; // 显示评论模态框
            })
            .catch(error => {
                console.error('Error:', error);
                alert('加载评论失败');
            });
    }

    function showReplyForm(commentId) {
        const replyForm =
            '<div>' +
            '<textarea id="replyContent-' + commentId + '" rows="3" placeholder="输入你的回复..."></textarea><br>' +
            '<button class="btn" type="button" onclick="addReply(' + commentId + ')">提交回复</button>' +
            '</div>';

        document.getElementById('replies-' + commentId).innerHTML = replyForm;
    }

    function addReply(commentId) {
        const replyContent = document.getElementById('replyContent-' + commentId).value;

        if (!replyContent) {
            alert("回复内容不能为空");
            return;
        }

        // 构造请求参数，假设当前登录的用户ID为当前用户（这需要在实际代码中动态获取）
        const params = new URLSearchParams();
        params.append('commentId', commentId);
        params.append('content', replyContent);

        fetch('<%= contextPath %>/ReplyServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(response => {
                if (response.ok) {
                    alert('回复提交成功');
                    loadReplies(commentId); // 提交后重新加载回复
                } else {
                    alert('提交回复失败');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('请求失败');
            });
    }

    function loadReplies(commentId) {
        fetch('<%= contextPath %>/ReplyServlet?commentId=' + commentId)
            .then(response => response.json())
            .then(replies => {
                const repliesContainer = document.getElementById('replies-' + commentId);
                repliesContainer.innerHTML = ''; // 清空之前的回复内容

                if (replies.length === 0) {
                    repliesContainer.innerHTML = '<p>暂无回复</p>';
                } else {
                    replies.forEach(reply => {
                        const userName = userIdToNameMap[reply.userId] || '未知用户';
                        const replyElement = document.createElement('div');
                        replyElement.innerHTML =
                            '<p class="small-text">'  + userName + ' 回复' + ': ' + reply.content +
                            ' <small>(' + reply.timestamp + ')</small></p>' +
                            '<button class="btn small-btn" onclick="deleteReply(' + reply.replyId + ', ' + commentId + ')">删除</button>';
                        repliesContainer.appendChild(replyElement);
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('加载回复失败');
            });
    }


    function addComment(novelId) {
        // const novelId = document.getElementById('novelId').value; // 获取小说ID
        // //const novelId = 4;
        const content = document.getElementById('newCommentContent').value; // 获取评论内容

        if (!novelId || !content) {
            alert("小说ID和评论内容不能为空");
            return;
        }

        // 使用 URLSearchParams 构造 `application/x-www-form-urlencoded` 格式的请求体
        const params = new URLSearchParams();
        params.append('novelId', novelId);
        params.append('content', content);

        fetch('<%= contextPath %>/CommentServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(response => {
                if (response.ok) {
                    alert('评论添加成功');
                    // 重新加载评论，假设有一个函数刷新评论列表
                    showCommentsModal(novelId);
                } else if (response.status === 401) {
                    alert('请先登录后再评论');
                } else {
                    alert('添加评论失败');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('请求失败');
            });
    }

    // 删除评论
    function deleteComment(commentId, novelId) {
        if (confirm("确定要删除这条评论吗？")) {
            fetch('<%= contextPath %>/CommentServlet/' + commentId, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    alert('评论删除成功');
                    showCommentsModal(novelId); // 重新加载评论
                } else {
                    alert('删除评论失败');
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('请求失败');
            });
        }
    }

    function deleteReply(replyId, commentId) {
        if (confirm("确定要删除这条回复吗？")) {
            fetch('<%= contextPath %>/ReplyServlet/' + replyId, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    alert('回复删除成功');
                    loadReplies(commentId); // 删除后重新加载回复
                } else {
                    alert('删除回复失败');
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('请求失败');
            });
        }
    }


    // 加入分组的功能
    function addToGroup() {
        const novelId = document.getElementById('novelId').value;
        const groupId = document.getElementById('groupId').value;

        if (!groupId) {
            alert('请选择一个分组');
            return;
        }

        fetch('<%= contextPath %>/GroupedNovelsServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                novelId: novelId,
                groupId: groupId
            })
        }).then(response => {
            if (response.ok) {
                alert('已成功加入分组');
                closeModal();
            } else {
                alert('加入分组失败');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('请求失败');
        });
    }

    // 关闭小说内容模态框
    function closeContentModal() {
        document.getElementById('contentModal').style.display = 'none';
    }

    // 关闭加入分组模态框
    function closeGroupModal() {
        document.getElementById('groupModal').style.display = 'none';
    }

    // 关闭评论模态框
    function closeCommentsModal() {
        document.getElementById('commentsModal').style.display = 'none';
    }

    // 当点击模态框外部时关闭模态框
    window.onclick = function(event) {
        if (event.target == document.getElementById('contentModal')) {
            closeContentModal();
        }
        if (event.target == document.getElementById('groupModal')) {
            closeGroupModal();
        }
        if (event.target == document.getElementById('commentsModal')) {
            closeCommentsModal();
        }
    }

    function showGroupedNovels() {
        // 向服务器请求已分组的小说列表
        fetch('<%= contextPath %>/NovelServlet/grouped')
            .then(response => response.json())
            .then(groupedNovels => {
                const groupedNovelsList = document.getElementById('groupedNovelsList');
                groupedNovelsList.innerHTML = ''; // 清空之前的内容

                if (groupedNovels.length === 0) {
                    groupedNovelsList.innerHTML = '<li>暂无已分组小说</li>';
                } else {
                    groupedNovels.forEach(novel => {
                        const li = document.createElement('li');
                        li.textContent = novel.title; // 假设小说对象包含 title 字段
                        groupedNovelsList.appendChild(li);
                    });
                }

                // 显示模态框
                document.getElementById('groupedNovelsModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('加载已分组小说失败');
            });
    }

    // 关闭已分组小说模态框
    function closeGroupedNovelsModal() {
        document.getElementById('groupedNovelsModal').style.display = 'none';
    }


</script>

</body>
</html>


