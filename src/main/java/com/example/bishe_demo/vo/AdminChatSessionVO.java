package com.example.bishe_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 管理员聊天会话VO
 */
@Schema(name = "AdminChatSessionVO", description = "管理员聊天会话VO")
public class AdminChatSessionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会话唯一标识（senderId_receiverId_jobId）")
    private String sessionId;

    @Schema(description = "发送者用户ID")
    private Long senderId;

    @Schema(description = "发送者名称")
    private String senderName;

    @Schema(description = "接收者用户ID")
    private Long receiverId;

    @Schema(description = "接收者名称")
    private String receiverName;

    @Schema(description = "关联的岗位ID")
    private Long jobId;

    @Schema(description = "岗位名称")
    private String jobName;

    @Schema(description = "最后一条消息")
    private String lastMessage;

    @Schema(description = "最后消息时间（ID）")
    private Long lastMessageTime;

    @Schema(description = "是否有争议（根据关键词判断）")
    private Boolean hasDispute;

    @Schema(description = "发送者角色（STUDENT/COMPANY/ADMIN）")
    private String senderRole;

    @Schema(description = "接收者角色（STUDENT/COMPANY/ADMIN）")
    private String receiverRole;

    public String getSessionId() {
        return sessionId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Boolean getHasDispute() {
        return hasDispute;
    }

    public void setHasDispute(Boolean hasDispute) {
        this.hasDispute = hasDispute;
    }
}
