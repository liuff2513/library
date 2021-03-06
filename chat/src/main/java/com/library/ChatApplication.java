package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @ClassName: ChatApplication
 * @Description: TODO
 * @author feifei.liu
 * @date 2018/11/15 13:48
 */
@SpringBootApplication
@ComponentScan("com.library")
@EnableWebSocket
@EnableTransactionManagement
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatApplication.class);
        application.run(args);
    }
}
