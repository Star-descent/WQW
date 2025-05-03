package com.itweiyunfan.shiyan2.Pojo;

import java.util.Date;

public class Education {
    private int educationId;
    private int userId;
    private String level;
    private Date startDate;
    private Date endDate;
    private String schoolName;
    private String degree;

    // Constructors, Getters, and Setters
    public Education() {}

    public Education(int educationId, int userId, String level, Date startDate, Date endDate, String schoolName, String degree) {
        this.educationId = educationId;
        this.userId = userId;
        this.level = level;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schoolName = schoolName;
        this.degree = degree;
    }

    public int getEducationId() { return educationId; }
    public void setEducationId(int educationId) { this.educationId = educationId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
}
