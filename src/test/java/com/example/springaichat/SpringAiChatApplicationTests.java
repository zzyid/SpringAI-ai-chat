package com.example.springaichat;


import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringAiChatApplicationTests {

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;
    @Autowired
    private VectorStore vectorStore;
    @Test
    public void testVectorStore() {
        Resource resource = new FileSystemResource("中二知识笔记.pdf");
        // 1. 创建PDF的读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                 resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)  // 每一页PDF拆分为一个Document对象
                        .build()
        );
        //2. 读取PDF文档，拆分为Document对象
        List<Document> documents =reader.read();
        //3. 将Document对象插入到向量数据库中
        vectorStore.add(documents);
        //4. 搜索
        SearchRequest request = SearchRequest.builder()
                .query("论语中教育的目的") // 问题
                .topK(1) // 返回的匹配结果数量
                .similarityThreshold(0.7) // 匹配度阈值, 范围0-1,作用: 匹配度越高，越相似
                .filterExpression("file_name == '中二知识笔记.pdf'") // 设置过滤条件，只搜索文件名字叫中二知识笔记的文档
                // 这个过滤条件的设置的目的就是，为了防止搜索到其他文档。
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);


        if(docs == null){
            System.out.println("没有找到匹配的文档");
            return;
        }
        for (Document o :docs) {
            System.out.println(o.getId()); // 文档ID
            System.out.println(o.getScore()); // 匹配度
            System.out.println(o.getText()); // 文档内容
        }
    }
    @Test
    void contextLoads() {
        float[] helloWorlds = embeddingModel.embed("hello world");
        System.out.println(Arrays.toString(helloWorlds));
    }

}
