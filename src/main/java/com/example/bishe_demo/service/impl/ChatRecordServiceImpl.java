package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bishe_demo.entity.ChatRecord;
import com.example.bishe_demo.entity.CompanyInfo;
import com.example.bishe_demo.entity.Job;
import com.example.bishe_demo.entity.StudentInfo;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.entity.UserRole;
import com.example.bishe_demo.entity.Role;
import com.example.bishe_demo.mapper.ChatRecordMapper;
import com.example.bishe_demo.mapper.CompanyInfoMapper;
import com.example.bishe_demo.mapper.JobMapper;
import com.example.bishe_demo.mapper.StudentInfoMapper;
import com.example.bishe_demo.mapper.UserMapper;
import com.example.bishe_demo.mapper.UserRoleMapper;
import com.example.bishe_demo.mapper.RoleMapper;
import com.example.bishe_demo.service.ChatRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bishe_demo.vo.AdminChatSessionVO;
import com.example.bishe_demo.vo.ChatSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 聊天记录表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private CompanyInfoMapper companyInfoMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 争议关键词列表
     */
    private static final Set<String> DISPUTE_KEYWORDS = new HashSet<>(Arrays.asList(
        "工资", "薪资", "钱", "不给", "拖欠", "扣", "骗", "假", "虚假", "违法", "报警", "投诉", "仲裁"
    ));

    @Override
    public List<ChatRecord> getChatHistory(Long userId, Long otherUserId, Long jobId) {
        // 使用Long类型进行查询，与ChatRecord类保持一致
        LambdaQueryWrapper<ChatRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq(ChatRecord::getSenderId, userId)
                .eq(ChatRecord::getReceiverId, otherUserId)
        ).or(wrapper -> wrapper
                .eq(ChatRecord::getSenderId, otherUserId)
                .eq(ChatRecord::getReceiverId, userId)
        );
        if (jobId != null) {
            queryWrapper.eq(ChatRecord::getJobId, jobId);
        }
        queryWrapper.orderByAsc(ChatRecord::getId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void markAsRead(Long userId, Long otherUserId) {
        // 使用Long类型进行查询，与ChatRecord类保持一致
        LambdaQueryWrapper<ChatRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatRecord::getSenderId, otherUserId)
                .eq(ChatRecord::getReceiverId, userId)
                .eq(ChatRecord::getIsRead, (byte) 0);
        ChatRecord updateRecord = new ChatRecord();
        updateRecord.setIsRead((byte) 1);
        baseMapper.update(updateRecord, queryWrapper);
    }

    @Override
    public List<ChatSessionVO> getChatSessions(Long userId) {
        // 使用Long类型进行查询，与ChatRecord类保持一致
        LambdaQueryWrapper<ChatRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatRecord::getSenderId, userId)
                .or()
                .eq(ChatRecord::getReceiverId, userId);
        List<ChatRecord> allRecords = baseMapper.selectList(queryWrapper);

        Map<Long, List<ChatRecord>> sessionMap = allRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    Long otherId = record.getSenderId().equals(userId) ? record.getReceiverId() : record.getSenderId();
                    return otherId;
                }));

        List<ChatSessionVO> sessions = new ArrayList<>();
        for (Map.Entry<Long, List<ChatRecord>> entry : sessionMap.entrySet()) {
            Long otherUserId = entry.getKey();
            List<ChatRecord> records = entry.getValue();

            ChatSessionVO vo = new ChatSessionVO();
            vo.setUserId(otherUserId);

            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser != null) {
                vo.setName(otherUser.getRealName());

                StudentInfo studentInfo = studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>()
                        .eq(StudentInfo::getUserId, otherUserId));

                if (studentInfo != null) {
                    vo.setType("student");
                } else {
                    vo.setType("company");
                    CompanyInfo companyInfo = companyInfoMapper.selectOne(new LambdaQueryWrapper<CompanyInfo>()
                            .eq(CompanyInfo::getUserId, otherUserId));
                    if (companyInfo != null) {
                        vo.setName(companyInfo.getCompanyName());
                    }
                }
            }

            ChatRecord lastRecord = records.stream()
                    .max((r1, r2) -> r1.getId().compareTo(r2.getId()))
                    .orElse(null);
            if (lastRecord != null) {
                vo.setLastMessage(lastRecord.getMessage());
                vo.setLastMessageTime(lastRecord.getId());
            }

            long unreadCount = records.stream()
                    .filter(r -> r.getReceiverId().equals(userId) && r.getIsRead() == 0)
                    .count();
            vo.setUnreadCount((int) unreadCount);

            sessions.add(vo);
        }

        sessions.sort((s1, s2) -> {
            if (s2.getLastMessageTime() == null) return -1;
            if (s1.getLastMessageTime() == null) return 1;
            return s2.getLastMessageTime().compareTo(s1.getLastMessageTime());
        });

        return sessions;
    }

    @Override
    public List<AdminChatSessionVO> getAllChatSessionsForAdmin() {
        List<ChatRecord> allRecords = baseMapper.selectList(null);
        
        Map<String, List<ChatRecord>> sessionMap = allRecords.stream()
            .collect(Collectors.groupingBy(record -> {
                Long minId = Math.min(record.getSenderId(), record.getReceiverId());
                Long maxId = Math.max(record.getSenderId(), record.getReceiverId());
                Long jobId = record.getJobId() != null ? record.getJobId() : 0L;
                return minId + "_" + maxId + "_" + jobId;
            }));
        
        Set<Long> userIds = new HashSet<>();
        Set<Long> jobIds = new HashSet<>();
        for (List<ChatRecord> records : sessionMap.values()) {
            for (ChatRecord record : records) {
                userIds.add(record.getSenderId());
                userIds.add(record.getReceiverId());
                if (record.getJobId() != null) {
                    jobIds.add(record.getJobId());
                }
            }
        }
        
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, u -> u));
        
        Map<Long, String> userNameMap = new java.util.HashMap<>();
        for (User user : userMap.values()) {
            userNameMap.put(user.getId(), user.getRealName());
        }
        
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds)
        );
        
        Map<Long, List<UserRole>> userRoleMap = userRoles.stream()
            .collect(Collectors.groupingBy(UserRole::getUserId));
        
        List<Role> allRoles = roleMapper.selectList(null);
        Map<Byte, String> roleMap = allRoles.stream()
            .collect(Collectors.toMap(Role::getId, Role::getRoleName));
        
        Map<Long, String> userRoleNameMap = new java.util.HashMap<>();
        for (Long userId : userIds) {
            List<UserRole> roles = userRoleMap.get(userId);
            if (roles != null && !roles.isEmpty()) {
                String userRoleName = roleMap.get(roles.get(0).getRoleId());
                userRoleNameMap.put(userId, userRoleName);
            } else {
                userRoleNameMap.put(userId, "UNKNOWN");
            }
        }
        
        List<CompanyInfo> companyInfos = companyInfoMapper.selectList(
            new LambdaQueryWrapper<CompanyInfo>().in(CompanyInfo::getUserId, userIds)
        );
        for (CompanyInfo info : companyInfos) {
            userNameMap.put(info.getUserId(), info.getCompanyName());
        }
        
        Map<Long, Job> jobMap = jobIds.isEmpty() ? new java.util.HashMap<>() :
            jobMapper.selectBatchIds(new ArrayList<>(jobIds)).stream()
                .collect(Collectors.toMap(Job::getId, j -> j));
        
        List<AdminChatSessionVO> sessions = new ArrayList<>();
        for (Map.Entry<String, List<ChatRecord>> entry : sessionMap.entrySet()) {
            List<ChatRecord> records = entry.getValue();
            ChatRecord lastRecord = records.stream()
                .max((r1, r2) -> r1.getId().compareTo(r2.getId()))
                .orElse(null);
            
            if (lastRecord == null) continue;
            
            AdminChatSessionVO vo = new AdminChatSessionVO();
            vo.setSessionId(entry.getKey());
            vo.setSenderId(lastRecord.getSenderId());
            vo.setSenderName(userNameMap.getOrDefault(lastRecord.getSenderId(), "未知"));
            vo.setSenderRole(userRoleNameMap.getOrDefault(lastRecord.getSenderId(), "UNKNOWN"));
            vo.setReceiverId(lastRecord.getReceiverId());
            vo.setReceiverName(userNameMap.getOrDefault(lastRecord.getReceiverId(), "未知"));
            vo.setReceiverRole(userRoleNameMap.getOrDefault(lastRecord.getReceiverId(), "UNKNOWN"));
            vo.setJobId(lastRecord.getJobId());
            vo.setLastMessage(lastRecord.getMessage());
            vo.setLastMessageTime(lastRecord.getId());
            
            if (lastRecord.getJobId() != null && jobMap.containsKey(lastRecord.getJobId())) {
                vo.setJobName(jobMap.get(lastRecord.getJobId()).getJobName());
            }
            
            boolean hasDispute = records.stream()
                .anyMatch(record -> {
                    String message = record.getMessage();
                    if (message == null) return false;
                    return DISPUTE_KEYWORDS.stream().anyMatch(keyword -> message.contains(keyword));
                });
            vo.setHasDispute(hasDispute);
            
            sessions.add(vo);
        }
        
        sessions.sort((s1, s2) -> {
            if (s2.getLastMessageTime() == null) return -1;
            if (s1.getLastMessageTime() == null) return 1;
            return s2.getLastMessageTime().compareTo(s1.getLastMessageTime());
        });
        
        return sessions;
    }
}
