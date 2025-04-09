package com.example.springaichat.controller;

import com.example.springaichat.repository.ChatHistoryRepository;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Resource
    private  ChatClient chatClient;
    // 请求方式和路径不要改动，将来要与前端联调
    @Resource
    private ChatHistoryRepository chatHistoryRepository;

    /**
     *  不是流式调用，直接返回一个字符串
     * @param prompt
     * @return
     */
    @RequestMapping("/chat/call")
    public String chatCall( String prompt) {
        return chatClient
                .prompt(prompt) // 传入user提示词
                .call() // 同步请求，会等待AI全部输出完才返回结果
                .content(); //返回响应内容
    }
    @RequestMapping(value = "/chat/stream",produces = "text/html;charset=utf-8")
    public Flux<String> chatStream(@RequestParam() String prompt , String chatId) {
        chatHistoryRepository.save("chat",chatId);
        return chatClient
                .prompt()
                .user(prompt)// 传入user提示词
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream() // 同步请求，会等待AI全部输出完才返回结果
                .content(); //返回响应内容

    }
}
