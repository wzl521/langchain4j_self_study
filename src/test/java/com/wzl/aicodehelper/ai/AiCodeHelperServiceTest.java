package com.wzl.aicodehelper.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat(){
        String chat = aiCodeHelperService.chat("你好，我是程序员小武");
        System.out.println(chat);
    }

    @Test
    void chatWithMemory(){
        String chat = aiCodeHelperService.chat("你好，我是程序员小武");
        System.out.println(chat);
        String chat1 = aiCodeHelperService.chat("我是谁？");
        System.out.println(chat1);
    }
}