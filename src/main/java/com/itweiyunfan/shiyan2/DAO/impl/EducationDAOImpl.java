package com.itweiyunfan.shiyan2.DAO.impl;

import com.itweiyunfan.shiyan2.DAO.EducationDAO;
import com.itweiyunfan.shiyan2.Pojo.Education;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationDAOImpl implements EducationDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/prac";
    private static final String USER = "root";
    private static final String PASSWORD = "root2";

    @Override
    public Education getEducationByUserId(int userId) {
        String sql = "SELECT * FROM education WHERE user_id = ?";
        Education education = null;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    education = new Education();
                    education.setEducationId(resultSet.getInt("education_id"));
                    education.setUserId(resultSet.getInt("user_id"));
                    education.setLevel(resultSet.getString("level"));
                    education.setStartDate(resultSet.getDate("start_date"));
                    education.setEndDate(resultSet.getDate("end_date"));
                    education.setSchoolName(resultSet.getString("school_name"));
                    education.setDegree(resultSet.getString("degree"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return education;
    }

    @Override
    public void addEducation(Education education) {
        String sql = "INSERT INTO education (user_id, school_name, degree, level, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, education.getUserId());
            statement.setString(2, education.getSchoolName());
            statement.setString(3, education.getDegree());
            statement.setString(4, education.getLevel());
            statement.setDate(5, new java.sql.Date(education.getStartDate().getTime()));
            statement.setDate(6, new java.sql.Date(education.getEndDate().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateEducation(Education education) {
        String sql = "UPDATE education SET school_name = ?, degree = ?, level = ?, start_date = ?, end_date = ? WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, education.getSchoolName());
            statement.setString(2, education.getDegree());
            statement.setString(3, education.getLevel());
            statement.setDate(4, new java.sql.Date(education.getStartDate().getTime()));
            statement.setDate(5, new java.sql.Date(education.getEndDate().getTime()));
            statement.setInt(6, education.getUserId());
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            // 更详细的错误日志记录
            Logger logger = Logger.getLogger("EducationDAO");
            logger.log(Level.SEVERE, "Failed to update education", e);
            return false;
        }
    }


    @Override
    public boolean deleteEducationByUserId(int userId) {
        String sql = "DELETE FROM education WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
