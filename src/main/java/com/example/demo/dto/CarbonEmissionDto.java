package com.example.demo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 碳排放数据传输对象
 */
@Data
public class CarbonEmissionDto {
    
    private Long id;
    
    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;
    
    /**
     * 排放日期
     */
    @NotNull(message = "排放日期不能为空")
    private LocalDate emissionDate;
    
    /**
     * 排放量（吨CO2）
     */
    @NotNull(message = "排放量不能为空")
    @Positive(message = "排放量必须大于0")
    private BigDecimal emissionAmount;
    
    /**
     * 关联文件URL
     */
    private String fileUrl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}