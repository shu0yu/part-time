package com.example.bishe_demo.service;

import com.example.bishe_demo.entity.ChatRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bishe_demo.vo.AdminChatSessionVO;
import com.example.bishe_demo.vo.ChatSessionVO;

import java.util.List;

/**
 * <p>
 * 聊天记录表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface ChatRecordService extends IService<ChatRecord> {

    List<ChatRecord> getChatHistory(Long userId, Long otherUserId, Long jobId);

    void markAsRead(Long userId, Long otherUserId);

    List<ChatSessionVO> getChatSessions(Long userId);

    /**
     * 管理员获取所有聊天会话
     * @return 会话列表
     */
    List<AdminChatSessionVO> getAllChatSessionsForAdmin();
}
