package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Reply;

import java.util.List;

public interface ReplyDAO {
    List<Reply> getRepliesByCommentId(int commentId); // 获取某个评论的所有回复
    boolean addReply(Reply reply); // 添加回复
    boolean deleteReply(int replyId); // 删除回复
}
