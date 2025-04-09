package com.example.springaichat.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

@Service
@Slf4j
public class LocalPdfFileRepository implements FileRepository{

    @Autowired
    private VectorStore vectorStore;

    // 会话id与文件名的对应关系，方便查询会话历史时重新加载文件
    // Properties 类是一个键值对集合，用于存储和检索键值对。它提供了一些方便和实用的方法，如put()、get()、remove()等，用于添加、获取和删除键值对。
    private final Properties chatFiles = new Properties();

    /**
     *  保存文件, 还要记录ChatId与文件的映射关系
     * @param chatId
     * @param resource
     * @return
     */
    @Override
    public boolean save(String chatId, Resource resource) {
        // 2. 保存到本地磁盘（也可以保存到数据库等等）
        String filename = resource.getFilename(); // 获取文件名
        File target = new File(Objects.requireNonNull(filename)); // 创建目标文件，即这个文件要保存的位置，我们现在保存在相对路径下
        if(!target.exists()){
            try{
                // 将resource文件保存在target中，即本地磁盘中了
                Files.copy(resource.getInputStream(), target.toPath());
            } catch (IOException e) {
                log.error("保存文件失败");
                return false;
            }
        }
        // 3. 保存ChatId与文件的映射关系
        chatFiles.put(chatId, filename);
        return true;
    }

    /**
     *  根据ChatId获取文件
     * @param chatId
     * @return
     */
    @Override
    public Resource getFile(String chatId) {
        return new FileSystemResource(chatFiles.getProperty(chatId));
    }

    /**
     *  初始化，加载文件
     */
    @PostConstruct // 在构造方法之后执行
    private void init() {
        FileSystemResource pdfResource =  new FileSystemResource("chat.pdf.properties");
        if(pdfResource.exists()){
            try {
                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileSystemResource vectorResource =  new FileSystemResource("chat-pdf.json");
        if(vectorResource.exists()){
            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
            simpleVectorStore.load(vectorResource);
        }
    }

    /**
     *  持久化，保存文件
     */
    @PreDestroy // 在销毁方法之前执行
    private void persistent(){
        try {
            chatFiles.store(new FileWriter("chat-pdf.properties"), LocalDateTime.now().toString());
            SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
            simpleVectorStore.save(new File("chat-pdf.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
