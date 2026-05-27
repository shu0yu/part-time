package com.example.bishe_demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApplicationWithStudent", description = "申请记录及学生信息")
public class ApplicationWithStudent {
    
    @Schema(description = "申请 ID")
    private Long id;
    
    @Schema(description = "申请的岗位 ID")
    private Long jobId;
    
    @Schema(description = "申请学生的用户 ID")
    private Long studentId;
    
    @Schema(description = "学生申请备注")
    private String applyRemark;
    
    @Schema(description = "学生用户名")
    private String studentUsername;
    
    @Schema(description = "学生真实姓名")
    private String studentRealName;
    
    @Schema(description = "学生手机号")
    private String studentPhone;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getApplyRemark() {
        return applyRemark;
    }
    
    public void setApplyRemark(String applyRemark) {
        this.applyRemark = applyRemark;
    }
    
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }
    
    public String getStudentRealName() {
        return studentRealName;
    }
    
    public void setStudentRealName(String studentRealName) {
        this.studentRealName = studentRealName;
    }
    
    public String getStudentPhone() {
        return studentPhone;
    }
    
    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }
}
