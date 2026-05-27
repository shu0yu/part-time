package com.example.bishe_demo.service;

import com.example.bishe_demo.entity.CompanyInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 企业信息扩展表 服务类
 * </p>
 *
 * @author shuoyu
 * @since 2026-02-26
 */
public interface CompanyInfoService extends IService<CompanyInfo> {

    CompanyInfo getByUserId(Long userId);
}
