package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.CarbonEmission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 碳排放数据Mapper接口
 */
@Mapper
public interface CarbonEmissionMapper extends BaseMapper<CarbonEmission> {
}