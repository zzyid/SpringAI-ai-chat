package com.example.springaichat.config;

import com.example.springaichat.constants.SystemConstants;
import com.example.springaichat.model.AlibabaOpenAiChatModel;
import com.example.springaichat.tools.CourseTools;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;


@Configuration
public class CommonConfiguration {


    // 使用本地向量库，配置一个向量库的bean SimpleVectorStore
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel){
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    // 注意参数中的model就是使用的模型，这里用了Ollama，也可以选择OpenAIChatModel

    /**
     * 对话机器人的ChatClient
     * @param model
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient chatClient(OllamaChatModel model, ChatMemory chatMemory) {
        return ChatClient.builder(model) // 创建ChatClient工厂
                .defaultSystem("你是一个高冷，御姐的智能女助手，你的名字叫李诗雅，请以李诗雅的身份和预期回答问题")
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 添加一个日志拦截器
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)) // 添加一个会话历史记录的拦截器,用于会话记忆
                .build(); // 构建ChatClient实例
    }

    /**
     * 哄哄模拟器的ChatClient
     * @param model
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    /**
     *  服务端客服的ChatClient
     * @param model
     * @param chatMemory
     * @param courseTools
     * @return
     */
    @Bean
    public ChatClient serviceChatClient(
            AlibabaOpenAiChatModel model,
            ChatMemory chatMemory,
            CourseTools courseTools) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.CUSTOMER_SERVICE_SYSTEM)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
                        new SimpleLoggerAdvisor()) //
                .defaultTools(courseTools)  // 定义的Tools
                .build();
    }

    /**
     *  PDF查询的ChatClient
     * @param model // 使用的模型
     * @param chatMemory  // 会话历史记录
     * @param vectorStore // 向量库
     * @return
     */
    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model,
                                    ChatMemory chatMemory,
                                    VectorStore vectorStore ) {
        return ChatClient.builder(model) // 创建ChatClient工厂
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 添加一个日志拦截器
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)) // 添加一个会话历史记录的拦截器,用于会话记忆
                .defaultAdvisors(new QuestionAnswerAdvisor(
                        vectorStore, // 向量库
                        SearchRequest.builder() // 向量检索的请求参数
                                .similarityThreshold(0.6d) // 相似度阈值
                                .topK(2) // 返回匹配的文档片段数
                                .build()
                ))
                .build(); // 构建ChatClient实例
    }
    /**
     * 创建一个默认的ChatMemory，用于存储对话历史记录
     * @return
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
        String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
        String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
        String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
        String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
        Map<String, List<String>> connectionHeaders = new HashMap<>();
        if (StringUtils.hasText(projectId)) {
            connectionHeaders.put("OpenAI-Project", List.of(projectId));
        }

        if (StringUtils.hasText(organizationId)) {
            connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
        }
        RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
        WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
        AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry)observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
        Objects.requireNonNull(chatModel);
        observationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }
}
