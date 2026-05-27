package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 用户-角色关联表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("user_role")
@Schema(name = "UserRole", description = "用户-角色关联表")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Byte roleId;

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

    public Byte getRoleId() {
        return roleId;
    }

    public void setRoleId(Byte roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserRole{" +
            "id = " + id +
            ", userId = " + userId +
            ", roleId = " + roleId +
        "}";
    }
}