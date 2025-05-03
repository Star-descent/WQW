package com.itweiyunfan.shiyan2.Pojo;

import java.util.Date;

public class Users {

    private Integer userId;       // 用户 ID
    private String name;      // 姓名
    private String gender;    // 性别
    private Date birthDate;   // 出生日期
    private String password;   // 密码
    private String email;      // 邮箱

    // 无参构造方法
//    public Users() {
//    }

    // 带参数的构造方法
    public Users(int userId, String name, String gender, Date birthDate, String password, String email) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.password = password;
        this.email = email;
    }

    // Getter 和 Setter 方法
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
