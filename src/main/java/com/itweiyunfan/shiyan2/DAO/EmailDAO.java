package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Emails;
import java.util.List;

public interface EmailDAO {
    // 获取指定用户的所有邮箱
    List<Emails> getEmailsByUserId(int userId);

    // 添加新的邮箱
    void addEmail(Emails email);

    // 设置某个邮箱为用户名
    void setEmailAsUsername(int emailId, int userId);

    // 删除邮箱
    void deleteEmail(int emailId);

    boolean isEmailExists(String email);
}
