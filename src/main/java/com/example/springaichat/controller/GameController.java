package com.example.springaichat.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/ai")
public class GameController {

    @Resource
    private ChatClient gameChatClient;

    @RequestMapping(value = "/game", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId){
        return gameChatClient
                .prompt()
                .user(prompt)// 传入user提示词
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)) // 添加一个会话历史记录的拦截器,用于会话记忆
                .stream() // 同步请求，会等待AI全部输出完才返回结果
                .content(); //返回响应内容
    }
}
