package com.example.bishe_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 用户信息VO（包含角色信息）
 */
@Schema(name = "UserWithRoleVO", description = "用户信息VO（包含角色信息）")
public class UserWithRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "状态（0-禁用，1-正常）")
    private Byte status;

    @Schema(description = "角色名称（ADMIN/COMPANY/STUDENT）")
    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
