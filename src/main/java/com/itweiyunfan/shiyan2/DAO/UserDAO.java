package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Users;

import java.util.List;

public interface UserDAO {
    void addUser(Users user);                     // 添加用户
    Users getUserById(int userId);                // 根据用户ID获取用户
    List<Users> getAllUsers();                    // 获取所有用户
    boolean updateUser(Users user);                // 更新用户信息
    boolean deleteUser(int userId);                // 根据用户ID删除用户
}
