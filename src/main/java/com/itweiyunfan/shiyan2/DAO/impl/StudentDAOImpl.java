package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.StudentDAO;
import com.itweiyunfan.shiyan2.Pojo.Student;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {
    // 数据库连接信息
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/prac";
    private static final String USER = "root";
    private static final String PASSWORD = "root2";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student";

        // 使用 try-with-resources 语句确保资源如连接（conn）和语句（stmt）在使用后自动关闭
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // 遍历结果集，为每行记录创建一个 Student 对象
            while (rs.next()) {
                Student student = new Student();
                student.setSid(rs.getString("sid"));
                student.setName(rs.getString("name"));
                student.setGender(rs.getString("gender").charAt(0)); // 假设数据库中性别字段为 'M' 或 'F'
                student.setAge(rs.getInt("age"));
                student.setBirthday(rs.getDate("birthday")); // 假设数据库中生日字段为日期类型

                // 将 Student 对象添加到列表中
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 在实际应用中应该有更合适的异常处理
        }
        return students;
    }


    @Override
    public void addStudent(Student student) {
        // SQL 语句用于插入学生信息
        String sql = "INSERT INTO student (sid, name, gender, age, birthday) VALUES (?, ?, ?, ?, ?)";

        // 使用 try-with-resources 语句自动管理数据库连接和预处理语句的关闭
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置预处理语句的参数
            pstmt.setString(1, student.getSid());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, String.valueOf(student.getGender())); // 假设数据库中的gender字段是以文本（如'M'或'F'）存储
            pstmt.setInt(4, student.getAge());
            pstmt.setDate(5, new java.sql.Date(student.getBirthday().getTime())); // java.util.Date 转换为 java.sql.Date

            // 执行SQL语句，插入学生信息
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // 实际项目中，应考虑更合适的异常处理方式，比如日志记录
        }
    }

    @Override
    public void updateStudent(Student student) {
        String sql = "UPDATE student SET name = ?, gender = ?, age = ?, birthday = ? WHERE sid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, String.valueOf(student.getGender()));
            pstmt.setInt(3, student.getAge());

            // 检查生日是否为null
            if (student.getBirthday() != null) {
                pstmt.setDate(4, new java.sql.Date(student.getBirthday().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE); // 设置SQL NULL值
            }
            pstmt.setString(5, student.getSid());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStudentBySid(String sid) {
        // SQL 语句用于删除学生信息
        String sql = "DELETE FROM student WHERE sid = ?";
        // 使用 try-with-resources 语句自动管理数据库连接和预处理语句的关闭
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 设置预处理语句的唯一参数
            pstmt.setString(1, sid);
            // 执行SQL语句，删除学生信息
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // 实际项目中，应考虑更合适的异常处理方式，比如日志记录
        }
    }


    @Override
    public Student getStudentBySid(String sid) {
        Student student = null;
        String sql = "SELECT * FROM student WHERE sid = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sid);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    student = new Student();
                    student.setSid(resultSet.getString("sid"));
                    student.setName(resultSet.getString("name"));
                    student.setGender(resultSet.getString("gender").charAt(0));
                    student.setAge(resultSet.getInt("age"));
                    student.setBirthday(resultSet.getDate("birthday"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }
}
