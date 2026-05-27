package com.example.bishe_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChatSessionVO", description = "聊天会话视图对象")
public class ChatSessionVO {

    @Schema(description = "沟通对象用户 ID")
    private Long userId;

    @Schema(description = "沟通对象姓名/企业名称")
    private String name;

    @Schema(description = "沟通对象类型：student-学生，company-企业")
    private String type;

    @Schema(description = "最后一条消息内容")
    private String lastMessage;

    @Schema(description = "未读消息数")
    private Integer unreadCount;

    @Schema(description = "最后消息时间戳")
    private Long lastMessageTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
