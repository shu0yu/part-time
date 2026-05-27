package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bishe_demo.constant.SystemConstant;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.entity.UserRole;
import com.example.bishe_demo.entity.Role;
import com.example.bishe_demo.mapper.UserMapper;
import com.example.bishe_demo.mapper.UserRoleMapper;
import com.example.bishe_demo.mapper.RoleMapper;
import com.example.bishe_demo.service.UserService;
import com.example.bishe_demo.vo.UserWithRoleVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户基础信息表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    /**
     * 构造函数注入依赖
     * @param userRoleMapper 用户角色关联Mapper
     * @param roleMapper 角色Mapper
     */
    public UserServiceImpl(UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    /**
     * 分页查询用户列表
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param name 用户姓名（模糊查询）
     * @return 用户分页结果
     */
    @Override
    public IPage<User> pageList(int pageSize, int currentPage, String name) {
        Page<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(User::getRealName, name);
        }
        
        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息，不存在则返回null
     */
    @Override
    public User findByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户信息，不存在则返回null
     */
    @Override
    public User getUserById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(Long userId, List<Byte> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoleList = new ArrayList<>();
            for (Byte roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleList.add(userRole);
            }
            for (UserRole userRole : userRoleList) {
                userRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Byte> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
        
        List<Role> roles = roleMapper.selectList(
            new LambdaQueryWrapper<Role>().in(Role::getId, roleIds)
        );
        
        return roles.stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
    }

    /**
     * 分页获取所有用户及其角色信息
     * @param pageSize 每页显示的条数
     * @param currentPage 要查询的页
     * @param roleName 角色名称（过滤条件）
     * @param status 用户状态（过滤条件）
     * @return 用户角色分页结果
     */
    @Override
    public IPage<UserWithRoleVO> getAllUsersWithRole(int pageSize, int currentPage, String roleName, Byte status) {
        Page<User> userPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            userWrapper.eq(User::getStatus, status);
        }
        
        IPage<User> userIPage = baseMapper.selectPage(userPage, userWrapper);
        List<User> users = userIPage.getRecords();
        
        List<UserWithRoleVO> voList = new ArrayList<>();
        if (!users.isEmpty()) {
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds)
            );
            
            Map<Long, List<UserRole>> userRoleMap = userRoles.stream()
                .collect(Collectors.groupingBy(UserRole::getUserId));
            
            List<Role> allRoles = roleMapper.selectList(null);
            Map<Byte, String> roleMap = allRoles.stream()
                .collect(Collectors.toMap(Role::getId, Role::getRoleName, (v1, v2) -> v1));
            
            for (User user : users) {
                UserWithRoleVO vo = new UserWithRoleVO();
                BeanUtils.copyProperties(user, vo);
                
                List<UserRole> roles = userRoleMap.get(user.getId());
                if (roles != null && !roles.isEmpty()) {
                    String userRoleName = roleMap.get(roles.get(0).getRoleId());
                    vo.setRoleName(userRoleName);
                }
                
                if (roleName == null || roleName.equals(vo.getRoleName())) {
                    voList.add(vo);
                }
            }
        }
        
        Page<UserWithRoleVO> resultPage = new Page<>(currentPage, pageSize);
        resultPage.setRecords(voList);
        resultPage.setTotal(userIPage.getTotal());
        
        return resultPage;
    }

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    public boolean updateUserStatus(Long userId, Byte status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return updateById(user);
    }
}
