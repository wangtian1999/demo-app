package com.example.demo;

import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.example.demo"})
@MapperScan("com.example.demo.mapper")
@Slf4j
public class DemoApplication implements CommandLineRunner {


    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    private UserService userService;

    public static void main(String[] args) throws UnknownHostException {
        // 设置时区
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        
        // 启动应用
        ConfigurableApplicationContext application = SpringApplication.run(DemoApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "8080");
        String path = env.getProperty("server.servlet.context-path", "");
        
//        log.info("\n----------------------------------------------------------\n\t" +
//                "Application is running! Access URLs:\n\t" +
//                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
//                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
//                "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
//                "----------------------------------------------------------");
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化默认用户
        userService.initDefaultUsers();
        log.info("---------------------默认用户初始化完成-------------------");
        log.info("测试账号：admin/admin123 (管理员)");
        log.info("测试账号：user/user123 (普通用户)");

        log.info("-------------------------------------------------------");
    }
}