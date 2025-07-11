package com.wzl.aicodehelper.ai;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@AiService
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
}
