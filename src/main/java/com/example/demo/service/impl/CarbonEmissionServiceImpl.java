package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CarbonEmissionDto;
import com.example.demo.entity.CarbonEmission;
import com.example.demo.mapper.CarbonEmissionMapper;
import com.example.demo.service.CarbonEmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 碳排放数据服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonEmissionServiceImpl implements CarbonEmissionService {
    
    private final CarbonEmissionMapper carbonEmissionMapper;
    
    @Override
    public CarbonEmission createEmission(CarbonEmissionDto dto, Long userId) {
        log.info("=== Service层开始创建碳排放数据 ===");
        log.info("接收到的DTO: {}", dto);
        log.info("用户ID: {}", userId);
        
        CarbonEmission emission = new CarbonEmission();
        log.info("创建空的CarbonEmission对象");
        
        BeanUtils.copyProperties(dto, emission);
        log.info("复制属性后的emission对象: {}", emission);
        
        emission.setCreateTime(LocalDateTime.now());
        emission.setUpdateTime(LocalDateTime.now());
        emission.setCreateBy(userId);
        emission.setUpdateBy(userId);
        log.info("设置时间和用户信息后的emission对象: {}", emission);
        
        log.info("准备调用mapper.insert()方法插入数据库");
        int insertResult = carbonEmissionMapper.insert(emission);
        log.info("mapper.insert()返回结果: {}", insertResult);
        log.info("插入后的emission对象(包含生成的ID): {}", emission);
        
        log.info("新增碳排放数据成功，ID: {}, 企业: {}", emission.getId(), emission.getEnterpriseName());
        
        return emission;
    }
    
    @Override
    public CarbonEmission updateEmission(Long id, CarbonEmissionDto dto, Long userId) {
        CarbonEmission existingEmission = carbonEmissionMapper.selectById(id);
        if (existingEmission == null) {
            throw new RuntimeException("碳排放数据不存在");
        }
        
        // 复制属性，但保留原有的创建信息
        BeanUtils.copyProperties(dto, existingEmission, "id", "createTime", "createBy");
        existingEmission.setUpdateTime(LocalDateTime.now());
        existingEmission.setUpdateBy(userId);
        
        carbonEmissionMapper.updateById(existingEmission);
        log.info("更新碳排放数据，ID: {}, 企业: {}", existingEmission.getId(), existingEmission.getEnterpriseName());
        
        return existingEmission;
    }
    
    @Override
    public boolean deleteEmission(Long id) {
        CarbonEmission emission = carbonEmissionMapper.selectById(id);
        if (emission == null) {
            return false;
        }
        
        int result = carbonEmissionMapper.deleteById(id);
        if (result > 0) {
            log.info("删除碳排放数据，ID: {}, 企业: {}", id, emission.getEnterpriseName());
            return true;
        }
        return false;
    }
    
    @Override
    public CarbonEmission getEmissionById(Long id) {
        return carbonEmissionMapper.selectById(id);
    }
    
    @Override
    public IPage<CarbonEmission> getEmissionPage(Page<CarbonEmission> page, 
                                               String enterpriseName, 
                                               LocalDate startDate, 
                                               LocalDate endDate) {
        QueryWrapper<CarbonEmission> queryWrapper = new QueryWrapper<>();
        
        // 企业名称模糊查询
        if (StringUtils.hasText(enterpriseName)) {
            queryWrapper.like("enterprise_name", enterpriseName);
        }
        
        // 日期范围查询
        if (startDate != null) {
            queryWrapper.ge("emission_date", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("emission_date", endDate);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        return carbonEmissionMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public List<CarbonEmission> getAllEmissions() {
        QueryWrapper<CarbonEmission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return carbonEmissionMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<CarbonEmission> getEmissionsByEnterprise(String enterpriseName) {
        QueryWrapper<CarbonEmission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enterprise_name", enterpriseName);
        queryWrapper.orderByDesc("emission_date");
        return carbonEmissionMapper.selectList(queryWrapper);
    }
}