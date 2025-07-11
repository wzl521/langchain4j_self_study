package com.wzl.aicodehelper.ai;


import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class AiCodeHelperServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    /**
     * 注册一个Bean，用于创建AiCodeHelperService的实例
     * 反射 + 动态代理
     * @return
     */
    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        //会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        //构造AiService
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory) //会话记忆
                .contentRetriever(contentRetriever) //RAG
                .build();

        return aiCodeHelperService;
    }
}
