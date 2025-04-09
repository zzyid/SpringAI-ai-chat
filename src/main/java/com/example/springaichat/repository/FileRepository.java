package com.example.springaichat.repository;

import org.springframework.core.io.Resource;

/**
 *  文件历史管理器
 */
public interface FileRepository {

    /**
     *  保存文件, 还要记录ChatId与文件的映射关系
     * @param chatId
     * @param resource
     * @return
     */
    boolean save(String chatId, Resource resource);

    /**
     *  根据ChatId获取文件
     * @param chatId
     * @return
     */
    Resource getFile(String chatId);

}
