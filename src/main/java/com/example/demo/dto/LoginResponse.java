package com.example.demo.dto;

public class LoginResponse {
    
    private String token;
    private String username;
    private String role;
    private String message;
    
    // 构造函数
    public LoginResponse() {}
    
    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.message = "登录成功";
    }
    
    public LoginResponse(String message) {
        this.message = message;
    }
    
    // Getter和Setter方法
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}