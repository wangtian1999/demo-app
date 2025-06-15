package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User findByUsername(String username) {
        logger.info("查找用户: {}", username);
        User user = this.baseMapper.findByUsername(username);
        if (user != null) {
            logger.info("找到用户: {}, ID: {}, 角色: {}", user.getUsername(), user.getId(), user.getRole());
        } else {
            logger.warn("未找到用户: {}", username);
        }
        return user;
    }
    
    /**
     * 验证用户密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        logger.info("验证密码 - 原始密码: {}, 加密密码: {}", rawPassword, encodedPassword);
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("密码匹配结果: {}", matches);
        return matches;
    }
    
    /**
     * 创建新用户
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 创建的用户
     */
    @Override
    public User createUser(String username, String password, String role) {
        logger.info("创建新用户: {}, 角色: {}", username, role);
        String encodedPassword = passwordEncoder.encode(password);
        logger.info("密码加密完成，原始密码: {}, 加密后: {}", password, encodedPassword);
        User user = new User(username, encodedPassword, role);
        save(user);
        logger.info("用户创建成功: {}", username);
        return user;
    }
    
    /**
     * 初始化默认用户
     */
    @Override
    public void initDefaultUsers() {
        logger.info("=== 开始初始化默认用户 ===");
        
        // 检查是否已存在admin用户
        logger.info("检查admin用户是否存在...");
        if (findByUsername("admin") == null) {
            logger.info("admin用户不存在，开始创建...");
            createUser("admin", "admin123", "ADMIN");
        } else {
            logger.info("admin用户已存在，跳过创建");
        }
        
        // 检查是否已存在user用户
        logger.info("检查user用户是否存在...");
        if (findByUsername("user") == null) {
            logger.info("user用户不存在，开始创建...");
            createUser("user", "user123", "USER");
        } else {
            logger.info("user用户已存在，跳过创建");
        }
        
        logger.info("=== 默认用户初始化完成 ===");
    }
}