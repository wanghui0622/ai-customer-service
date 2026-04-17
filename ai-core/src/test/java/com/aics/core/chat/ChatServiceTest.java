package com.aics.core.chat;

import com.aics.core.AiCoreAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiCoreAutoConfiguration.class)
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Test
    void chat() {
        String answer1 = chatService.chat("session-1", "我的订单，订单号 12345什么时候到");
        System.out.println(answer1);
        /*System.out.println("-------------------------");
        String answer2 = chatService.chat("session-1", "你好");
        System.out.println(answer2);
        System.out.println("-------------------------");
        String answer3 = chatService.chat("session-3", "你好");
        System.out.println(answer3);*/
    }
}