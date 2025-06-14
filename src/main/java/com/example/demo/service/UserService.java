package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);

    boolean checkPassword(String rawPassword, String encodedPassword);

    User createUser(String username, String password, String role);

    void initDefaultUsers();
}
