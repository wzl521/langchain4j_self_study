package com.wzl.aicodehelper.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperTest  {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void chat(){
        aiCodeHelper.chat("你好，我是程序员小武");
    }
}