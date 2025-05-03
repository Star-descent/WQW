package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Comment;

import java.util.List;

public interface CommentDAO {
    List<Comment> getCommentsByNovelId(int novelId); // 获取指定小说的评论列表
    boolean addComment(Comment comment);             // 添加评论
    boolean deleteComment(int commentId);            // 删除评论
}
