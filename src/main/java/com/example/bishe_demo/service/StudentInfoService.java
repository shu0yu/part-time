package com.example.bishe_demo.service;

import com.example.bishe_demo.entity.StudentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生信息扩展表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface StudentInfoService extends IService<StudentInfo> {

    StudentInfo getByUserId(Long userId);
}
