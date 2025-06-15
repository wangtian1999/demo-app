package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.FileStorage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件存储Mapper接口
 */
@Mapper
public interface FileStorageMapper extends BaseMapper<FileStorage> {
}