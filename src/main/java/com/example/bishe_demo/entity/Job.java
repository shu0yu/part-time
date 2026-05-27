package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 兼职岗位表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("job")
@Schema(name = "Job", description = "兼职岗位表")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "岗位ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "发布企业的用户ID")
    private Long companyId;

    @Schema(description = "岗位名称")
    private String jobName;

    @Schema(description = "岗位类型（如家教、促销、实习）")
    private String jobType;

    @Schema(description = "最低薪资（元/小时/天）")
    private BigDecimal salaryMin;

    @Schema(description = "最高薪资（元/小时/天）")
    private BigDecimal salaryMax;

    @Schema(description = "工作地点")
    private String workAddress;

    @Schema(description = "工作时间（如每周一至周五18:00-20:00）")
    private String workTime;

    @Schema(description = "岗位描述")
    private String jobDesc;

    @Schema(description = "岗位要求")
    private String jobRequire;

    @Schema(description = "是否删除（0-未删，1-已删）")
    private Byte isDelete;

    @Schema(description = "公司名称（非数据库字段）")
    @TableField(exist = false)
    private String companyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobRequire() {
        return jobRequire;
    }

    public void setJobRequire(String jobRequire) {
        this.jobRequire = jobRequire;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "Job{" +
            "id = " + id +
            ", companyId = " + companyId +
            ", jobName = " + jobName +
            ", jobType = " + jobType +
            ", salaryMin = " + salaryMin +
            ", salaryMax = " + salaryMax +
            ", workAddress = " + workAddress +
            ", workTime = " + workTime +
            ", jobDesc = " + jobDesc +
            ", jobRequire = " + jobRequire +
            ", isDelete = " + isDelete +
            ", companyName = " + companyName +
        "}";
    }
}