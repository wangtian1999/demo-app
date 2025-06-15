package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CarbonEmissionDto;
import com.example.demo.entity.CarbonEmission;

import java.time.LocalDate;
import java.util.List;

/**
 * 碳排放数据服务接口
 */
public interface CarbonEmissionService {
    
    /**
     * 新增碳排放数据
     * @param dto 碳排放数据DTO
     * @param userId 用户ID
     * @return 保存的数据
     */
    CarbonEmission createEmission(CarbonEmissionDto dto, Long userId);
    
    /**
     * 更新碳排放数据
     * @param id 数据ID
     * @param dto 碳排放数据DTO
     * @param userId 用户ID
     * @return 更新的数据
     */
    CarbonEmission updateEmission(Long id, CarbonEmissionDto dto, Long userId);
    
    /**
     * 删除碳排放数据
     * @param id 数据ID
     * @return 是否删除成功
     */
    boolean deleteEmission(Long id);
    
    /**
     * 根据ID获取碳排放数据
     * @param id 数据ID
     * @return 碳排放数据
     */
    CarbonEmission getEmissionById(Long id);
    
    /**
     * 分页查询碳排放数据
     * @param page 分页参数
     * @param enterpriseName 企业名称（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    IPage<CarbonEmission> getEmissionPage(Page<CarbonEmission> page, 
                                         String enterpriseName, 
                                         LocalDate startDate, 
                                         LocalDate endDate);
    
    /**
     * 获取所有碳排放数据
     * @return 数据列表
     */
    List<CarbonEmission> getAllEmissions();
    
    /**
     * 根据企业名称查询数据
     * @param enterpriseName 企业名称
     * @return 数据列表
     */
    List<CarbonEmission> getEmissionsByEnterprise(String enterpriseName);
}