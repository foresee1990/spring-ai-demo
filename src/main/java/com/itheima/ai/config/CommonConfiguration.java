package com.itheima.ai.config;

import com.itheima.ai.constants.SystemConstants;
import com.itheima.ai.entity.query.CourseQuery;
import com.itheima.ai.tools.CourseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rowan,Wu
 * @date 2026/2/21
 */
@Configuration
public class CommonConfiguration {
    @Bean
    public ChatMemory chatMemory() {

        return new InMemoryChatMemory();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model) // 创建构建器
                .defaultSystem("你是一个能冷静分析的智能助手，你叫Snowy,请以Snowy的身份语气回答问题") //系统提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), // 请求前和响应后的日志记录
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();  // 构建最终实例， 返回 fianl ChatClient
    }

    @Bean
    public ChatClient serviceChatClient(OpenAiChatModel model, ChatMemory chatMemory, CourseTools courseTools) {
        return ChatClient
                .builder(model) // 创建构建器
                .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT) //系统提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), // 请求前和响应后的日志记录
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .defaultTools(courseTools)
                .build();  // 构建最终实例， 返回 fianl ChatClient
    }

    /**
     * 创建一个星露谷会话游戏专用的ChatClient
     */
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model) // 创建构建器
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT) //系统提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),// 请求前和响应后的日志记录
                        MessageChatMemoryAdvisor.builder(chatMemory) // 记录拼接到chatMemory的Advisor
                                .build()
                )
                .build();  // 构建最终实例， 返回 fianl ChatClient
    }
}
