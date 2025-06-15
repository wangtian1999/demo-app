package com.example.demo.controller;

import com.example.demo.dto.FileUploadResponse;
import com.example.demo.entity.FileStorage;
import com.example.demo.service.FileStorageService;
import com.example.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    
    private final FileStorageService fileStorageService;
    private final JwtUtils jwtUtils;
    
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        log.info("收到文件上传请求，文件名: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查Authorization头
            String authHeader = request.getHeader("Authorization");
            log.info("Authorization头: {}", authHeader != null ? "Bearer ***" : "null");
            
            // 从请求头获取用户ID
            Long userId = getUserIdFromRequest(request);
            log.info("获取到用户ID: {}", userId);
            
            // 上传文件
            log.info("开始上传文件到存储服务");
            FileUploadResponse uploadResponse = fileStorageService.uploadFile(file, userId);
            log.info("文件上传成功，文件ID: {}", uploadResponse.getFileId());
            
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("data", uploadResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 文件下载
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long fileId) {
        try {
            FileStorage fileStorage = fileStorageService.getFileById(fileId);
            if (fileStorage == null) {
                return ResponseEntity.notFound().build();
            }
            
            InputStream inputStream = fileStorageService.downloadFile(fileId);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=" + URLEncoder.encode(fileStorage.getFileName(), StandardCharsets.UTF_8));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long fileId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = fileStorageService.deleteFile(fileId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "文件删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "文件不存在或删除失败");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户的文件列表
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserFiles(HttpServletRequest request) {
        log.info("收到获取用户文件列表请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查Authorization头
            String authHeader = request.getHeader("Authorization");
            log.info("获取文件列表 - Authorization头: {}", authHeader != null ? "Bearer ***" : "null");
            
            Long userId = getUserIdFromRequest(request);
            log.info("获取文件列表 - 用户ID: {}", userId);
            
            List<FileStorage> files = fileStorageService.getFilesByUserId(userId);
            log.info("获取文件列表 - 找到 {} 个文件", files.size());
            
            response.put("success", true);
            response.put("data", files);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取用户文件列表失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 检查文件类型是否支持
     */
    @GetMapping("/check-type")
    public ResponseEntity<Map<String, Object>> checkFileType(@RequestParam String fileName) {
        Map<String, Object> response = new HashMap<>();
        
        boolean allowed = fileStorageService.isAllowedFileType(fileName);
        response.put("success", true);
        response.put("allowed", allowed);
        response.put("message", allowed ? "文件类型支持" : "文件类型不支持");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("原始Authorization头: {}", token);
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            log.info("提取的token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                log.info("从token中获取的用户名: {}", username);
                // 这里简化处理，实际应该从用户服务获取用户ID
                // 暂时返回固定值，后续可以改进
                return 1L;
            } catch (Exception e) {
                log.error("解析token失败: {}", e.getMessage());
                // 临时fallback：如果token解析失败，使用默认用户ID
                log.warn("使用默认用户ID: 1");
                return 1L;
            }
        }
        
        log.warn("未找到Authorization头或格式不正确，使用默认用户ID: 1");
        // 临时fallback：如果没有token，使用默认用户ID
        return 1L;
    }
}