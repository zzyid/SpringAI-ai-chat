package com.example.springaichat.controller;

import com.example.springaichat.entity.vo.Result;
import com.example.springaichat.repository.ChatHistoryRepository;
import com.example.springaichat.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Slf4j
@RestController
@RequestMapping("/ai/pdf")
public class PdfController {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private VectorStore vectorStore;

    @jakarta.annotation.Resource
    private ChatClient pdfChatClient;

    @jakarta.annotation.Resource
    private ChatHistoryRepository chatHistoryRepository;

    /**
     *  文件上传
     * @param chatId
     * @param file
     * @return
     */
    @RequestMapping ("/upload/{chatId}")
    public Result uploadPdf(@PathVariable String chatId, @RequestParam("file")MultipartFile file) {
        try {
            // 1. 校验文件是否为PDF格式
            if(!Objects.equals(file.getContentType(), "application/pdf")){
                return Result.fail("只能上传PDF文件");
            }
            // 2. 保存文件
            boolean save = fileRepository.save(chatId, file.getResource());
            if(!save){
                return Result.fail("文件保存失败");
            }
            // 3. 写入向量库
            this.writeToVectorStore(file.getResource());
            return Result.ok("上传成功");
        } catch (Exception e){
            log.error("Failed to upload PDF",e);
            return Result.fail("上传失败");
        }
    }
    private void writeToVectorStore(Resource resource) {
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
    }

    /**
     *  文件下载
     * @param chatId
     * @return
     */
    @GetMapping("/file/{chatId}")
    public ResponseEntity<Resource> download(@PathVariable String chatId){
        // 1. 读取文件
        Resource resource = fileRepository.getFile(chatId);
        if(resource == null){
            return ResponseEntity.notFound().build();
        }
        // 2. 文件名编码写入响应头
        String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
        // 3. 返回文件
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     *  对话和检索文件
     * @param prompt
     * @param chatId
     * @return
     */
    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chatStream(@RequestParam() String prompt , String chatId) {
        chatHistoryRepository.save("pdf",chatId);
        Resource file = fileRepository.getFile(chatId);
        String filename = file.getFilename();
        return pdfChatClient
                .prompt()
                .user(prompt)// 传入user提示词
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "file_name == '"+filename+"'" )) //
                .stream() // 同步请求，会等待AI全部输出完才返回结果
                .content(); //返回响应内容

    }
}
