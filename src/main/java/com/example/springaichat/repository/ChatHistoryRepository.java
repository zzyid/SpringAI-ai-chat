package com.example.springaichat.repository;

import java.util.List;

/**
 *  会话记录管理器
 */
public interface ChatHistoryRepository {
    /**
     * 保存会话记录
     * @param chatId 会话id
     * @param type 业务类型， 如：chat,service，pdf
     */
    void save(String type,String chatId);

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);
}
