package com.itheima.ai.controller;

import com.itheima.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author WU,Rowan
 * @date 2026/2/21
 */
@RequiredArgsConstructor //自动生成构造函数，getset 方法
@RestController
@RequestMapping("/ai")
public class CustomerServiceController {

    private final ChatClient serviceChatClient;
    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/service", produces = "text/html; charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        //1.保存会话id,不同类别
        chatHistoryRepository.save("service", chatId);
        //2. 通过chatClient调用模型
        return serviceChatClient.prompt()     // 1. 创建提示构建器，commonConfiguration
                .user(prompt)          // 2. 设置用户输入
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();            // 4. 获取响应文本

    }
}
