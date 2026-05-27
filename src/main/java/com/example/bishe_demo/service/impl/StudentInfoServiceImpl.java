package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bishe_demo.entity.StudentInfo;
import com.example.bishe_demo.mapper.StudentInfoMapper;
import com.example.bishe_demo.service.StudentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生信息扩展表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Override
    public StudentInfo getByUserId(Long userId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<StudentInfo>().eq(StudentInfo::getUserId, userId));
    }
}
