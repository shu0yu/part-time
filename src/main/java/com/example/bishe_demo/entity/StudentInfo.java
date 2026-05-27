package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 学生信息表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("student_info")
@Schema(name = "StudentInfo", description = "学生信息扩展表")
public class StudentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学生信息ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "学校名称")
    private String school;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "年级")
    private String grade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
            "id = " + id +
            ", userId = " + userId +
            ", school = " + school +
            ", major = " + major +
            ", grade = " + grade +
        "}";
    }
}