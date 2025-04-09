package com.example.springaichat.controller;

import com.example.springaichat.entity.vo.MessageVO;
import com.example.springaichat.repository.ChatHistoryRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.messages.Message;
import java.util.List;

@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    @Resource
    private  ChatHistoryRepository chatHistoryRepository;

    @Resource
    private  ChatMemory chatMemory;

    /**
     * 查询会话历史列表
     * @param type 业务类型，如：chat,service,pdf
     * @return chatId列表
     */
    @GetMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type) {
        return chatHistoryRepository.getChatIds(type);
    }

    /**
     * 根据业务类型、chatId查询会话历史
     * @param type 业务类型，如：chat,service,pdf
     * @param chatId 会话id
     * @return 指定会话的历史消息
     */
    @GetMapping("/{type}/{chatId}")
    public List<MessageVO> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        List<Message> messages = chatMemory.get(chatId, Integer.MAX_VALUE); // 获取指定会话的所有消息
        if(messages == null) {
            return List.of();
        }
        return messages.stream().map(MessageVO::new).toList();
    }
}
