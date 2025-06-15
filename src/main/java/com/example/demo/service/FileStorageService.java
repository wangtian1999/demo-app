package com.example.demo.service;

import com.example.demo.dto.FileUploadResponse;
import com.example.demo.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件上传响应
     */
    FileUploadResponse uploadFile(MultipartFile file, Long userId);
    
    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream downloadFile(Long fileId);
    
    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId);
    
    /**
     * 根据ID获取文件信息
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileStorage getFileById(Long fileId);
    
    /**
     * 获取用户的所有文件
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileStorage> getFilesByUserId(Long userId);
    
    /**
     * 验证文件类型是否允许
     * @param fileName 文件名
     * @return 是否允许
     */
    boolean isAllowedFileType(String fileName);
}