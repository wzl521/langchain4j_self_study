package com.wzl.aicodehelper.ai;

import dev.langchain4j.service.Result;
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

    @Test
    void chatForReport() {
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport("你好，我是程序员小武,学习编程两年，请给我一份学习报告");
        System.out.println(report);
    }


    @Test
    void chatWithRag() {
        String chat = aiCodeHelperService.chatWithRag("怎么学习java，并且成为java架构师");
        System.out.println(chat);
    }

    @Test
    void chatWithRagForReport() {
        Result<String> stringResult = aiCodeHelperService.chatWithRagForReport("怎么学习java，并且成为java架构师");
        System.out.println(stringResult.sources());
        System.out.println(stringResult.content());
    }

    @Test
    void chatWithTools() {
        String chat = aiCodeHelperService.chatWithRag("有哪些常见的计算机网络面试题？");
        System.out.println(chat);
    }

    @Test
    void chatWithMcp() {
        String chat = aiCodeHelperService.chat("开源的llm模型项目有哪些？");
        System.out.println(chat);
    }
}