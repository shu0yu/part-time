package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 企业信息表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("company_info")
@Schema(name = "CompanyInfo", description = "企业信息扩展表")
public class CompanyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "企业信息ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业地址")
    private String address;

    @Schema(description = "联系人")
    private String contactPerson;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return "CompanyInfo{" +
            "id = " + id +
            ", userId = " + userId +
            ", companyName = " + companyName +
            ", address = " + address +
            ", contactPerson = " + contactPerson +
        "}";
    }
}