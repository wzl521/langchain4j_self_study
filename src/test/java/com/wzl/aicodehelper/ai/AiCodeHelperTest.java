package com.wzl.aicodehelper.ai;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperTest {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void chat() {
        aiCodeHelper.chat("你好，我是程序员小武");
    }

    @Test
    void chatWithMessage() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.mksucai.com%2Fimg%2Fqwgpxone.html&psig=AOvVaw1Hbu7aojQU-d6GpZr4k7hG&ust=1752281779264000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCKjYo9_Ms44DFQAAAAAdAAAAABAE")
        );
        aiCodeHelper.chatWithMessage(userMessage);
    }

    @Test
    void chatWithPrompt() {
        aiCodeHelper.chatWithPrompt("你好，我是程序员小武");
    }
}