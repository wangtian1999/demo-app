package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("=== 开始处理登录请求 ===");
        log.info("接收到的用户名: {}", loginRequest.getUsername());
        log.info("接收到的密码: {}", loginRequest.getPassword());
        
        try {
            // 查找用户
            log.info("正在查找用户: {}", loginRequest.getUsername());
            User user = userService.findByUsername(loginRequest.getUsername());
            
            if (user == null) {
                log.warn("用户不存在: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest()
                    .body(new LoginResponse("用户不存在"));
            }
            
            log.info("找到用户: {}, 角色: {}", user.getUsername(), user.getRole());
            log.info("数据库中的密码哈希: {}", user.getPassword());
            
            // 验证密码
            log.info("开始验证密码...");
            boolean passwordMatch = userService.checkPassword(loginRequest.getPassword(), user.getPassword());
            log.info("密码验证结果: {}", passwordMatch);
            
            if (!passwordMatch) {
                log.warn("密码验证失败，用户: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest()
                    .body(new LoginResponse("密码错误"));
            }
            
            // 生成JWT令牌
            log.info("密码验证成功，开始生成JWT令牌...");
            String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
            log.info("JWT令牌生成成功");
            
            // 返回登录成功响应
            LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRole());
            log.info("登录成功，用户: {}, 角色: {}", user.getUsername(), user.getRole());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("登录过程中发生异常: ", e);
            return ResponseEntity.internalServerError()
                .body(new LoginResponse("登录失败：" + e.getMessage()));
        }
    }
    
    /**
     * 用户登出
     * @return 登出响应
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // 简单的登出响应，实际的令牌失效由前端处理
        return ResponseEntity.ok("登出成功");
    }
    
    /**
     * 验证令牌
     * @param token JWT令牌
     * @return 验证结果
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            if (jwtUtils.validateToken(token, username)) {
                return ResponseEntity.ok("令牌有效");
            } else {
                return ResponseEntity.badRequest().body("令牌无效");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("令牌验证失败");
        }
    }
}