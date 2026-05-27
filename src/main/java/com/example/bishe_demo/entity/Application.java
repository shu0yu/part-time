package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 岗位申请表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("job_application")
@Schema(name = "Application", description = "岗位申请表")
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "申请的岗位ID")
    private Long jobId;

    @Schema(description = "申请学生的用户ID")
    private Long studentId;

    @Schema(description = "学生申请备注")
    private String applyRemark;

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

    @Override
    public String toString() {
        return "Application{" +
            "id = " + id +
            ", jobId = " + jobId +
            ", studentId = " + studentId +
            ", applyRemark = " + applyRemark +
        "}";
    }
}