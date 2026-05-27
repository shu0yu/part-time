package com.example.bishe_demo.vo;

import com.example.bishe_demo.entity.Application;
import com.example.bishe_demo.entity.ChatRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * 纠纷信息VO（包含聊天记录和申请记录）
 */
@Schema(name = "DisputeVO", description = "纠纷信息VO（包含聊天记录和申请记录）")
public class DisputeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "岗位ID")
    private Long jobId;

    @Schema(description = "岗位名称")
    private String jobName;

    @Schema(description = "学生用户ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "企业用户ID")
    private Long companyId;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "岗位申请记录")
    private Application application;

    @Schema(description = "聊天记录列表")
    private List<ChatRecord> chatRecords;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<ChatRecord> getChatRecords() {
        return chatRecords;
    }

    public void setChatRecords(List<ChatRecord> chatRecords) {
        this.chatRecords = chatRecords;
    }
}
