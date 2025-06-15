package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件存储实体类
 */
@Data
@TableName("file_storage")
public class FileStorage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 创建者ID
     */
    private Long createBy;
}