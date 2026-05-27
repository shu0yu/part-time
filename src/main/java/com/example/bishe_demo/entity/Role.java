package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("role")
@Schema(name = "Role", description = "角色表")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Byte id;

    @Schema(description = "角色名称（ADMIN/COMPANY/STUDENT）")
    private String roleName;

    @Schema(description = "角色描述")
    private String roleDesc;

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public String toString() {
        return "Role{" +
            "id = " + id +
            ", roleName = " + roleName +
            ", roleDesc = " + roleDesc +
        "}";
    }
}