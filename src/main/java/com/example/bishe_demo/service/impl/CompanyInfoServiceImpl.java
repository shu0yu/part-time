package com.example.bishe_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bishe_demo.entity.CompanyInfo;
import com.example.bishe_demo.mapper.CompanyInfoMapper;
import com.example.bishe_demo.service.CompanyInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 企业信息扩展表 服务实现类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
@Service
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoMapper, CompanyInfo> implements CompanyInfoService {

    @Override
    public CompanyInfo getByUserId(Long userId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<CompanyInfo>().eq(CompanyInfo::getUserId, userId));
    }
}
