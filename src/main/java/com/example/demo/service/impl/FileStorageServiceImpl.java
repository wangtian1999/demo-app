package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.FileUploadResponse;
import com.example.demo.entity.FileStorage;
import com.example.demo.mapper.FileStorageMapper;
import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    private final FileStorageMapper fileStorageMapper;
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;
    
    // 允许的文件类型
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "jpg", "jpeg", "png", "gif", "bmp", "svg",
            "txt", "csv"
    );
    

    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file, Long userId) {
        try {
            // 验证文件
            validateFile(file);
            
            // 确保上传目录存在
            ensureUploadDirExists();
            
            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            
            // 构建文件路径
            Path uploadDir = Paths.get(uploadPath);
            Path filePath = uploadDir.resolve(uniqueFileName);
            
            // 保存文件到本地
            Files.copy(file.getInputStream(), filePath);
            log.info("文件保存到: {}", filePath.toAbsolutePath());
            
            // 保存文件信息到数据库
            FileStorage fileStorage = new FileStorage();
            fileStorage.setFileName(originalFileName);
            fileStorage.setFileType(fileExtension);
            fileStorage.setFileUrl(uniqueFileName);  // 存储相对路径
            fileStorage.setFileSize(file.getSize());
            fileStorage.setCreateTime(LocalDateTime.now());
            fileStorage.setCreateBy(userId);
            
            fileStorageMapper.insert(fileStorage);
            
            return new FileUploadResponse(
                    fileStorage.getId(),
                    originalFileName,
                    uniqueFileName,
                    file.getSize(),
                    fileExtension,
                    "文件上传成功"
            );
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public InputStream downloadFile(Long fileId) {
        try {
            FileStorage fileStorage = fileStorageMapper.selectById(fileId);
            if (fileStorage == null) {
                throw new RuntimeException("文件不存在");
            }
            
            // 构建文件路径
            Path uploadDir = Paths.get(uploadPath);
            Path filePath = uploadDir.resolve(fileStorage.getFileUrl());
            
            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                throw new RuntimeException("文件不存在: " + filePath.toAbsolutePath());
            }
            
            return Files.newInputStream(filePath);
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(Long fileId) {
        try {
            FileStorage fileStorage = fileStorageMapper.selectById(fileId);
            if (fileStorage == null) {
                return false;
            }
            
            // 构建文件路径
            Path uploadDir = Paths.get(uploadPath);
            Path filePath = uploadDir.resolve(fileStorage.getFileUrl());
            
            // 删除本地文件
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("删除文件: {}", filePath.toAbsolutePath());
            }
            
            // 从数据库删除记录
            fileStorageMapper.deleteById(fileId);
            
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public FileStorage getFileById(Long fileId) {
        return fileStorageMapper.selectById(fileId);
    }
    
    @Override
    public List<FileStorage> getFilesByUserId(Long userId) {
        QueryWrapper<FileStorage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", userId);
        queryWrapper.orderByDesc("create_time");
        return fileStorageMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean isAllowedFileType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        String extension = getFileExtension(fileName).toLowerCase();
        return ALLOWED_FILE_TYPES.contains(extension);
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小不能超过" + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        if (!isAllowedFileType(file.getOriginalFilename())) {
            throw new RuntimeException("不支持的文件类型");
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * 确保上传目录存在
     */
    private void ensureUploadDirExists() {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("创建上传目录失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件存储目录初始化失败");
        }
    }
}