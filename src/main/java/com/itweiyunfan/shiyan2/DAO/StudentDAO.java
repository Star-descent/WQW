package com.itweiyunfan.shiyan2.DAO;

import com.itweiyunfan.shiyan2.Pojo.Student;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.List;

public interface StudentDAO {
    void addStudent(Student student);
    void deleteStudentBySid(String sid);
    void updateStudent(Student student);
    List<Student> getAllStudents();
    Student getStudentBySid(String sid);
}
