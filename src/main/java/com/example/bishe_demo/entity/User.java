package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 用户基础信息表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("user")
@Schema(name = "User", description = "用户基础信息表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "登录账号（唯一）")
    private String username;

    @Schema(description = "密码（MD5加密存储）")
    private String password;

    @Schema(description = "手机号（唯一）")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "状态（0-禁用，1-正常）")
    private Byte status;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "User{" +
            "id = " + id +
            ", username = " + username +
            ", password = " + password +
            ", phone = " + phone +
            ", realName = " + realName +
            ", status = " + status +
        "}";
    }
}