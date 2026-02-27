package com.itheima.ai.controller;

import com.itheima.ai.repository.InMemoryChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @author WU,Rowan
 * @date 2026/2/28
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class GameController {
    private final ChatClient gameChatClient;

    @RequestMapping(value = "/game", produces = "text/html; charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        //没有记录每次游戏内容的需求
        //所以直接通过chatClient调用模型
        return gameChatClient.prompt()     // 1. 创建提示构建器，commonConfiguration
                .user(prompt)          // 2. 设置用户输入
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                // 消费了 advisorSpec 对象并对其进行配置
                //.call()                // 3. 调用 AI 模型
                .stream()
                .content();            // 4. 获取响应文本

    }
}
