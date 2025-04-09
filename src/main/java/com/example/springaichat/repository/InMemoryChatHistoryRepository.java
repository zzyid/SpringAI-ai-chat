package com.example.springaichat.repository;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // 添加这个注解，表示这个类是一个组件，可以被Spring容器管理
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final Map<String, List<String>> chatHistory = new HashMap<>();

    /**
     *
     * @param type 业务类型， 如：chat,service，pdf
     * @param chatId 会话id
     */
    @Override
    public void save(String type, String chatId) {

        List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
        //
        if (chatIds.contains(chatId)) {
            return;
        }
        chatIds.add(chatId);
    }

    @Override
    public List<String> getChatIds(String type) {
                /*List<String> chatIds = chatHistory.get(type);
        return chatIds == null ? List.of() : chatIds;*/
        return chatHistory.getOrDefault(type, List.of());
    }
}
