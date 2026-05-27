package com.example.bishe_demo.websocket;

import com.example.bishe_demo.entity.ChatRecord;
import com.example.bishe_demo.service.ChatRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChatRecordService chatRecordService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        if (userId != null) {
            userSessionMap.put(userId, session);
            System.out.println("用户 " + userId + " 连接 WebSocket 成功");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> msgData = objectMapper.readValue(payload, Map.class);
        
        Long senderId = ((Number) msgData.get("senderId")).longValue();
        Long receiverId = ((Number) msgData.get("receiverId")).longValue();
        String msgContent = (String) msgData.get("message");
        Long jobId = msgData.get("jobId") != null ? ((Number) msgData.get("jobId")).longValue() : null;

        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSenderId(senderId);
        chatRecord.setReceiverId(receiverId);
        chatRecord.setMessage(msgContent);
        chatRecord.setIsRead((byte) 0);
        chatRecord.setJobId(jobId);

        chatRecordService.save(chatRecord);

        WebSocketSession receiverSession = userSessionMap.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            Map<String, Object> responseData = new ConcurrentHashMap<>();
            responseData.put("type", "new_message");
            responseData.put("data", chatRecord);
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseData)));
        }

        Map<String, Object> response = new ConcurrentHashMap<>();
        response.put("type", "message_sent");
        response.put("data", chatRecord);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        if (userId != null) {
            userSessionMap.remove(userId);
            System.out.println("用户 " + userId + " 断开 WebSocket 连接");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        if (userId != null) {
            userSessionMap.remove(userId);
            System.out.println("用户 " + userId + " WebSocket 传输错误");
        }
        if (exception.getMessage() != null) {
            System.out.println("错误信息：" + exception.getMessage());
        }
    }

    public static void sendMessageToUser(Long userId, TextMessage message) throws IOException {
        WebSocketSession session = userSessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }

    public static boolean isUserOnline(Long userId) {
        return userSessionMap.containsKey(userId);
    }
}
