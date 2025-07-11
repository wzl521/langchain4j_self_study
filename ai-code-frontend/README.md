# AI 编程小助手

一个基于 Vue3 的智能编程助手聊天应用，帮助开发者解答编程学习和求职面试相关的问题。

## 功能特性

- 🤖 智能对话：与 AI 助手进行实时对话
- 💬 聊天界面：现代化的聊天室界面设计
- 📝 Markdown 支持：消息内容支持 Markdown 格式渲染
- 🎨 代码高亮：自动识别并高亮显示代码块
- 🔄 流式响应：通过 SSE 技术实现实时消息流
- 📱 响应式设计：适配不同屏幕尺寸

## 技术栈

- **前端框架**: Vue 3 + TypeScript
- **构建工具**: Vite 5.x
- **HTTP 客户端**: Axios
- **Markdown 渲染**: marked
- **代码高亮**: highlight.js
- **样式**: CSS3 + Flexbox

## 快速开始

### 环境要求

- Node.js 18.x 或更高版本
- npm 或 yarn

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动

### 构建生产版本

```bash
npm run build
```

## 后端接口

应用需要连接到 SpringBoot 后端服务，接口信息：

- **基础地址**: `http://localhost:8080/api`
- **聊天接口**: `GET /ai/chat`
  - 参数: `memoryId` (会话ID), `message` (用户消息)
  - 响应: Server-Sent Events 流式数据

### 后端接口示例

```java
@RestController
@RequestMapping("/ai")
public class AiController {

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(int memoryId, String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk).build());
    }
}
```

## 项目结构

```
src/
├── components/          # 可复用组件
├── views/              # 页面组件
│   └── HomeView.vue    # 聊天主页
├── services/           # API 服务
│   └── api.ts         # SSE 聊天接口
├── router/            # 路由配置
├── assets/            # 静态资源
└── main.ts           # 应用入口
```

## 使用说明

1. 打开应用后，会自动生成一个随机的会话ID
2. 在输入框中输入您的编程问题
3. 按 Enter 键或点击发送按钮发送消息
4. AI 助手会通过流式响应实时回复您的问题
5. 支持 Markdown 格式，包括代码块、链接等

## 开发

### 代码检查

```bash
npm run lint
```

### 类型检查

```bash
npm run type-check
```

### 代码格式化

```bash
npm run format
```

## 注意事项

- 确保后端服务正常运行在 `http://localhost:8080`
- 需要支持 CORS 跨域请求
- 建议在现代浏览器中使用以获得最佳体验

## 许可证

MIT License
