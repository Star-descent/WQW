package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Education;

public interface EducationDAO {
    Education getEducationByUserId(int userId);
    void addEducation(Education education);
    boolean updateEducation(Education education);
    boolean deleteEducationByUserId(int userId); // 新增方法
}
