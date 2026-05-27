package com.example.bishe_demo.controller;

import com.example.bishe_demo.common.Result;
import com.example.bishe_demo.entity.ChatRecord;
import com.example.bishe_demo.service.ChatRecordService;
import com.example.bishe_demo.utils.ThreadLocalUtil;
import com.example.bishe_demo.vo.ChatSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 聊天记录表 前端控制器
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Tag(name = "聊天记录接口")
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class ChatRecordController {
    @Autowired
    private ChatRecordService chatRecordService;

    /**
     * 发送消息
     * @param chatRecord 聊天记录
     * @return Result<ChatRecord>
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送消息给指定用户")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<ChatRecord> send(@RequestBody Map<String, Object> chatRecordMap) {
        // 获取当前登录用户的 ID
        Long senderId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (senderId == null) {
            return Result.error("用户 ID 不能为空");
        }
        
        // 创建ChatRecord对象并设置属性
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSenderId(senderId);
        
        // 处理receiverId
        Object receiverIdObj = chatRecordMap.get("receiverId");
        if (receiverIdObj != null) {
            if (receiverIdObj instanceof String) {
                chatRecord.setReceiverId(Long.valueOf((String) receiverIdObj));
            } else if (receiverIdObj instanceof Number) {
                chatRecord.setReceiverId(((Number) receiverIdObj).longValue());
            }
        }
        
        // 处理message
        Object messageObj = chatRecordMap.get("message");
        if (messageObj != null) {
            chatRecord.setMessage(messageObj.toString());
        }
        
        // 处理jobId
        Object jobIdObj = chatRecordMap.get("jobId");
        if (jobIdObj != null) {
            if (jobIdObj instanceof String) {
                chatRecord.setJobId(Long.valueOf((String) jobIdObj));
            } else if (jobIdObj instanceof Number) {
                chatRecord.setJobId(((Number) jobIdObj).longValue());
            }
        }
        
        chatRecord.setIsRead((byte) 0); // 初始状态为未读
        boolean saved = chatRecordService.save(chatRecord);
        if (saved) {
            return Result.success(chatRecord);
        }
        return Result.error("发送失败");
    }

    /**
     * 获取聊天历史
     * @param otherUserId 对方用户 ID
     * @param jobId 关联的岗位 ID
     * @return Result<List<ChatRecord>>
     */
    @GetMapping("/history")
    @Operation(summary = "获取聊天历史", description = "获取与指定用户的聊天历史")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "otherUserId", description = "对方用户 ID", required = true),
            @Parameter(name = "jobId", description = "关联的岗位 ID", required = false)
    })
    public Result<List<ChatRecord>> getHistory(@RequestParam Long otherUserId, @RequestParam(required = false) Long jobId) {
        // 获取当前登录用户的 ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户 ID 不能为空");
        }
        // 标记消息为已读
        chatRecordService.markAsRead(userId, otherUserId);
        List<ChatRecord> records = chatRecordService.getChatHistory(userId, otherUserId, jobId);
        return Result.success(records);
    }

    /**
     * 获取聊天会话列表
     * @return Result<List<ChatSessionVO>>
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取聊天会话列表", description = "获取当前用户的聊天会话列表（按沟通对象排序，显示未读消息数）")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token")
    })
    public Result<List<ChatSessionVO>> getSessions() {
        // 获取当前登录用户的 ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户 ID 不能为空");
        }
        List<ChatSessionVO> sessions = chatRecordService.getChatSessions(userId);
        return Result.success(sessions);
    }

    /**
     * 标记消息已读
     * @param otherUserId 对方用户 ID
     * @return Result
     */
    @PostMapping("/markAsRead")
    @Operation(summary = "标记消息已读", description = "标记与指定用户的聊天消息为已读")
    @Parameters({
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "token"),
            @Parameter(name = "otherUserId", description = "对方用户 ID", required = true)
    })
    public Result markAsRead(@RequestParam Long otherUserId) {
        // 获取当前登录用户的 ID
        Long userId = com.example.bishe_demo.utils.UserIdUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户 ID 不能为空");
        }
        chatRecordService.markAsRead(userId, otherUserId);
        return Result.success();
    }
}
