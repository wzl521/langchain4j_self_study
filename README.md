# AI Code Helper - 智能编程助手

基于LangChain4j和Spring Boot构建的AI编程学习助手，专注于提供编程学习路线规划、项目建议、求职指导和面试题练习等功能。

## 🚀 项目概述

AI Code Helper是一个全栈AI应用，旨在帮助程序员和编程学习者：
- 制定清晰的编程学习路线
- 获取项目学习建议和最佳实践
- 提供程序员求职全流程指南
- 分享高频面试题和面试技巧

## 🛠️ 技术栈

### 后端技术
- **Java 21** - 使用最新LTS版本
- **Spring Boot 3.5.3** - 现代化Spring框架
- **LangChain4j** - Java版LangChain，简化AI应用开发
- **通义千问(Qwen)** - 阿里云大语言模型
- **Maven** - 项目构建和依赖管理
- **Lombok** - 减少样板代码

### 前端技术
- **React** - 用户界面构建
- **TypeScript** - 类型安全的JavaScript
- **Vite** - 快速的前端构建工具

### AI能力
- **RAG(检索增强生成)** - 基于知识库的智能问答
- **工具调用(Tool Calling)** - 集成外部API和服务
- **流式对话** - 实时响应用户输入
- **安全防护** - 敏感内容过滤
- **MCP支持** - Model Context Protocol集成

## 📋 功能特性

### 核心功能
1. **智能对话** - 基于通义千问的自然语言交互
2. **知识检索** - RAG技术增强的专业问答
3. **面试题搜索** - 集成面试鸭网站的题库查询
4. **学习路线** - 个性化编程学习建议
5. **求职指导** - 简历优化和面试技巧

### 技术特性
- **流式响应** - Server-Sent Events实现实时对话
- **会话记忆** - 支持多轮对话上下文
- **安全防护** - 输入内容安全检测
- **跨域支持** - 完整的CORS配置
- **模块化设计** - 清晰的代码架构

## 🏗️ 项目架构

```
src/main/java/com/wzl/aicodehelper/
├── AiCodeHelperApplication.java          # Spring Boot启动类
└── ai/
    ├── AiCodeHelper.java                 # 核心AI服务类
    ├── AiCodeHelperService.java          # AI服务接口
    ├── AiCodeHelperServiceFactory.java   # 服务工厂配置
    ├── config/
    │   └── CorsConfig.java              # 跨域配置
    ├── controller/
    │   └── AiController.java            # REST API控制器
    ├── guardrail/
    │   └── SafeInputGuardrail.java      # 输入安全防护
    ├── mcp/
    │   └── McpConfig.java               # MCP协议配置
    ├── rag/
    │   └── RagConfig.java               # RAG检索配置
    └── tools/
        └── InterviewQuestionTool.java    # 面试题搜索工具
```

## 🔧 配置说明

### 主要配置文件

#### `application.yml`
```yaml
spring:
  application:
    name: ai-code-helper
  profiles:
    active: local
server:
  port: 8080
  servlet:
    context-path: /api
```

#### `application-local.yml`
包含LangChain4j的模型配置：
- **Chat Model**: qwen-max (对话模型)
- **Streaming Model**: qwen-max (流式对话)
- **Embedding Model**: text-embedding-v4 (文本向量化)

### 系统提示词
位于`src/main/resources/system-prompt.txt`，定义了AI助手的角色和行为准则。

## 🚀 快速开始

### 环境要求
- Java 21+
- Maven 3.6+
- Node.js 16+ (前端开发)

### 后端启动
```bash
# 克隆项目
git clone [repository-url]
cd ai-code-helper

# 配置API密钥
# 编辑 src/main/resources/application-local.yml
# 设置通义千问API密钥

# 启动后端服务
./mvnw spring-boot:run
```

### 前端启动
```bash
cd ai-code-frontend
npm install
npm run dev
```

### 访问应用
- 后端API: http://localhost:8080/api
- 前端界面: http://localhost:5173

## 📚 API接口

### 流式对话接口
```
GET /api/ai/chat?memoryId={memoryId}&message={message}
```
- 支持Server-Sent Events
- 实时返回AI响应流
- 支持会话记忆

## 🎯 核心组件详解

### 1. AI服务层
- **AiCodeHelper**: 基础对话服务
- **AiCodeHelperService**: 高级AI服务接口
- **AiCodeHelperServiceFactory**: 服务构建和配置

### 2. RAG系统
- 文档加载和分段处理
- 向量化存储和检索
- 智能内容过滤

### 3. 工具集成
- **InterviewQuestionTool**: 面试题搜索
- **MCP工具**: 扩展第三方服务

### 4. 安全防护
- 敏感词过滤
- 输入内容验证
- 安全等级控制

## 📖 知识库

项目包含专业的编程学习资料：
- `从零开始学架构.md` - 软件架构学习指南
- `java学习路线.md` - Java技术学习路线

## 🔒 安全考虑

1. **输入防护**: SafeInputGuardrail防止恶意输入
2. **API密钥**: 敏感配置外部化管理
3. **跨域控制**: 精确的CORS策略配置

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 📝 开发笔记

### 技术亮点
- 使用LangChain4j简化AI应用开发
- 实现了完整的RAG检索增强生成
- 集成多种AI工具和外部服务
- 采用现代化的Spring Boot架构

### 扩展建议
- 增加更多专业领域的知识库
- 集成代码审查和建议功能
- 添加用户个性化学习计划
- 支持多语言编程语言学习

## 📄 许可证

[MIT License](LICENSE)

## 📞 联系方式

如有问题或建议，请提交Issue或联系项目维护者。
