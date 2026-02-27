package com.itheima.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rowan,Wu
 * @date 2026/2/21
 */
@Configuration
public class CommonConfiguration {
    @Bean
    //1.先定义存储的方式
    public ChatMemoryRepository chatMemoryRepository(){
        return new InMemoryChatMemoryRepository(); // 内存存储
        // return new JdbcChatMemoryRepository(); // 数据库存储
        // return new RedisChatMemoryRepository(); // Redis存储
    }

    @Bean
    //记忆管理的策略
    //可以看到MessageWindowChatMemory final参数有三个
    //用build()的方法创建，可以选取需要的参数声明
    //因为它的构造器是private
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository) //告诉ChatMememory存到哪里
                .maxMessages(18)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model) // 创建构建器
                .defaultSystem("你是一个能冷静分析的智能助手，你叫Snowy,请以Snowy的身份语气回答问题") //系统提示词
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(), // 请求前和响应后的日志记录
                        MessageChatMemoryAdvisor.builder(chatMemory) // 记录拼接到chatMemory的Advisor
                                .build()
                        // builder() 是"开始构建"，build() 是"完成构建"
                )
                .build();  // 构建最终实例， 返回 fianl ChatClient
    }
}
