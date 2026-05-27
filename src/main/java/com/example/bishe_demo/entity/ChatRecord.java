package com.example.bishe_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 聊天记录表
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@TableName("chat_record")
@Schema(name = "ChatRecord", description = "聊天记录表")
public class ChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "聊天记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "发送者用户ID")
    private Long senderId;

    @Schema(description = "接收者用户ID")
    private Long receiverId;

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "是否已读（0-未读，1-已读）")
    private Byte isRead;

    @Schema(description = "关联的岗位ID（便于定位聊天场景）")
    private Long jobId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "ChatRecord{" +
            "id = " + id +
            ", senderId = " + senderId +
            ", receiverId = " + receiverId +
            ", message = " + message +
            ", isRead = " + isRead +
            ", jobId = " + jobId +
        "}";
    }
}