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
public class ChatController {

    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat", produces = "text/html; charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        //1.保存会话id
        chatHistoryRepository.save("chat", chatId);
        //2. 通过chatClient调用模型
        return chatClient.prompt()     // 1. 创建提示构建器，commonConfiguration
                .user(prompt)          // 2. 设置用户输入
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                // 消费了 advisorSpec 对象并对其进行配置
                //.call()                // 3. 调用 AI 模型
                .stream()
                .content();            // 4. 获取响应文本

    }
}
