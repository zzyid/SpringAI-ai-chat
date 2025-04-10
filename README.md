# 🤖 SpringAI + 阿里云 OpenAI Chat Demos

这是一个基于 **SpringAI** 和 **阿里云 OpenAI 聊天模型** 的 AI 应用演示项目，涵盖了当前主流的四种 AI 开发模式，适合作为学习和项目原型开发的参考。

---

## 🧩 功能模块

### 1. 💬 聊天机器人
- 类似 ChatGPT 的通用对话机器人
- 使用 SpringAI 的标准对话接口实现
- 接入ollama本地部署模型

### 2. 🧸 哄哄模拟器（Prompt 工程）
- 提供“安慰、鼓励、陪伴”等特定角色对话
- 通过 Prompt 工程实现特定风格和语气

### 3. 🛎️ 智能客服机器人（Agent + Function Calling）
- 模拟真实客服处理流程
- 支持 Function Calling 和多轮任务式对话

### 4. 📄 ChatPDF（RAG：Retrieval-Augmented Generation）
- 支持上传 PDF 并进行语义问答
- 基于文档切片、向量检索和模型问答整合

---

## 🛠 技术栈

- **语言：** Java 17+
- **框架：** Spring Boot + SpringAI
- **模型服务：** 阿里云百炼 OpenAI API
- **RAG：** 支持向量数据库（如 FAISS / PGVector / Milvus）

---

## 🚀 快速开始
### 修改配置
请在 application.yml 中配置你的阿里云 API Key 和模型相关参数：
```yml
spring:
  application:
    name: spring-ai-chat
  ai:
spring:
  application:
    name: spring-ai-chat
  ai:
    ollama:
      base-url: http://localhost:11434 # ollama服务地址， 这是默认值，
      chat:
        model: deepseek-r1:7b # 需要更改为自己的模型名称
        options:
          temperature: 0.8 # 模型温度，影响模型生成结果的随机性，越小越稳定
    openai:
      base-url: {引用的大模型url：如阿里百炼：https://dashscope.aliyuncs.com/compatible-mode}
      api-key: {自己的API-key}
      chat:
        options:
          model: {使用的模型名称}
      embedding:
        options:
          model: {向量模型名称}
#注意：本项目提供的向量数据库为SpringAI自带的SimpleVectorStore数据库，如需更换为其他数据库，须自行配置
