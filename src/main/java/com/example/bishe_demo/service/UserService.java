package com.example.bishe_demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bishe_demo.entity.User;
import com.example.bishe_demo.vo.UserWithRoleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 用户基础信息表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface UserService extends IService<User> {

    IPage<User> pageList(int pageSize, int currentPage, String name);

    User findByUsername(String username);

    User getUserById(Long id);

    void assignRole(Long userId, List<Byte> roleIds);

    List<String> getUserRoles(Long userId);

    /**
     * 管理员查看所有用户列表（包含角色信息）
     * @param pageSize 每页条数
     * @param currentPage 当前页码
     * @param roleName 角色名称筛选（可选）
     * @param status 状态筛选（可选）
     * @return 分页结果
     */
    IPage<UserWithRoleVO> getAllUsersWithRole(int pageSize, int currentPage, String roleName, Byte status);

    /**
     * 禁用/启用用户账号
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-正常）
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Byte status);
}