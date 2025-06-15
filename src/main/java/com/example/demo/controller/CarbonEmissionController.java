package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CarbonEmissionDto;
import com.example.demo.entity.CarbonEmission;
import com.example.demo.service.CarbonEmissionService;
import com.example.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 碳排放数据管理控制器
 */
@RestController
@RequestMapping("/api/emissions")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CarbonEmissionController {

    private final CarbonEmissionService carbonEmissionService;
    private final JwtUtils jwtUtils;
    
    /**
     * 新增碳排放数据
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmission(
            @Valid @RequestBody CarbonEmissionDto dto,
            HttpServletRequest request) {
        
        log.info("=== 开始处理碳排放数据新增请求 ===");
        log.info("接收到的DTO数据: {}", dto);
        log.info("请求头信息: Authorization = {}", request.getHeader("Authorization"));
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromRequest(request);
            log.info("从请求中解析出的用户ID: {}", userId);
            
            log.info("调用service层创建碳排放数据...");
            CarbonEmission emission = carbonEmissionService.createEmission(dto, userId);
            log.info("service层返回的数据: {}", emission);
            
            response.put("success", true);
            response.put("message", "碳排放数据创建成功");
            response.put("data", emission);
            
            log.info("碳排放数据创建成功，返回响应: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("创建碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            log.error("返回错误响应: {}", response);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新碳排放数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmission(
            @PathVariable Long id,
            @Valid @RequestBody CarbonEmissionDto dto,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromRequest(request);
            CarbonEmission emission = carbonEmissionService.updateEmission(id, dto, userId);
            
            response.put("success", true);
            response.put("message", "碳排放数据更新成功");
            response.put("data", emission);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除碳排放数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmission(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = carbonEmissionService.deleteEmission(id);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "碳排放数据删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "数据不存在或删除失败");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("删除碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据ID获取碳排放数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmissionById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CarbonEmission emission = carbonEmissionService.getEmissionById(id);
            
            if (emission != null) {
                response.put("success", true);
                response.put("data", emission);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "数据不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("获取碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取数据失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 分页查询碳排放数据
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEmissionPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String enterpriseName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<CarbonEmission> page = new Page<>(current, size);
            IPage<CarbonEmission> result = carbonEmissionService.getEmissionPage(
                    page, enterpriseName, startDate, endDate);
            
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("分页查询碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取所有碳排放数据
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEmissions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CarbonEmission> emissions = carbonEmissionService.getAllEmissions();
            
            response.put("success", true);
            response.put("data", emissions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取所有碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取数据失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据企业名称查询数据
     */
    @GetMapping("/enterprise/{enterpriseName}")
    public ResponseEntity<Map<String, Object>> getEmissionsByEnterprise(
            @PathVariable String enterpriseName) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CarbonEmission> emissions = carbonEmissionService.getEmissionsByEnterprise(enterpriseName);
            
            response.put("success", true);
            response.put("data", emissions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("根据企业查询碳排放数据失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                // 这里简化处理，实际应该从用户服务获取用户ID
                // 暂时返回固定值，后续可以改进
                return 1L;
            } catch (Exception e) {
                log.warn("JWT token解析失败，使用默认用户ID: {}", e.getMessage());
            }
        }
        // 没有token或token无效时，返回默认用户ID（允许匿名访问）
        log.info("未提供有效token，使用默认用户ID: 1");
        return 1L;
    }
}