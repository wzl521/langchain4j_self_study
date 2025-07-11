<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'
import { chatWithAI } from '@/services/api'

// 响应式数据
const memoryId = ref<number>(0)
const messages = ref<Array<{
  type: 'user' | 'ai'
  content: string
  timestamp: Date
}>>([])
const inputMessage = ref('')
const isLoading = ref(false)
const isTyping = ref(false)
const messagesContainer = ref<HTMLElement>()
const messageInput = ref<HTMLTextAreaElement>()

// 初始化
onMounted(() => {
  // 生成随机会话ID
  memoryId.value = Math.floor(Math.random() * 1000000)
  
  // 添加欢迎消息
  messages.value.push({
    type: 'ai',
    content: '您好！我是AI编程小助手，很高兴为您解答编程学习和求职面试相关的问题。请告诉我您想了解什么？',
    timestamp: new Date()
  })
})

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true
})

// 渲染 Markdown
const renderMarkdown = (content: string) => {
  if (!content || content.trim() === '') {
    return ''
  }
  
  try {
    // 使用marked渲染markdown
    let html = marked(content) as string
    
    // 处理代码块高亮（支持更多格式）
    html = html.replace(/<pre><code(?:\s+class="language-(\w+)")?>([\s\S]*?)<\/code><\/pre>/g, 
      (match: string, lang: string | undefined, code: string) => {
        try {
          // 解码HTML实体
          const decodedCode = code
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
            .replace(/&amp;/g, '&')
            .replace(/&quot;/g, '"')
            .replace(/&#39;/g, "'")
          
          if (lang && hljs.getLanguage(lang)) {
            const highlighted = hljs.highlight(decodedCode, { language: lang }).value
            return `<pre><code class="hljs language-${lang}">${highlighted}</code></pre>`
          } else {
            const highlighted = hljs.highlightAuto(decodedCode).value
            return `<pre><code class="hljs">${highlighted}</code></pre>`
          }
        } catch (err) {
          console.error('代码高亮错误:', err)
          return `<pre><code class="hljs">${code}</code></pre>`
        }
      }
    )
    
    // 处理无语言标识的代码块
    html = html.replace(/<pre><code>([\s\S]*?)<\/code><\/pre>/g, 
      (match: string, code: string) => {
        try {
          const decodedCode = code
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
            .replace(/&amp;/g, '&')
            .replace(/&quot;/g, '"')
            .replace(/&#39;/g, "'")
          
          const highlighted = hljs.highlightAuto(decodedCode).value
          return `<pre><code class="hljs">${highlighted}</code></pre>`
        } catch (err) {
          console.error('代码高亮错误:', err)
          return match
        }
      }
    )
    
    return html
  } catch (error) {
    console.error('Markdown渲染错误:', error)
    // 如果渲染失败，返回带换行的纯文本
    return content.replace(/\n/g, '<br>')
  }
}

// 格式化时间
const formatTime = (date: Date) => {
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 自动调整textarea高度
const autoResize = () => {
  if (messageInput.value) {
    messageInput.value.style.height = 'auto'
    messageInput.value.style.height = messageInput.value.scrollHeight + 'px'
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 重置textarea高度
  if (messageInput.value) {
    messageInput.value.style.height = 'auto'
  }

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userMessage,
    timestamp: new Date()
  })

  scrollToBottom()
  isLoading.value = true
  isTyping.value = true

  try {
    // 添加AI消息占位符
    messages.value.push({
      type: 'ai',
      content: '',
      timestamp: new Date()
    })

    const aiMessageIndex = messages.value.length - 1
    let hasReceivedContent = false

    console.log('开始发送消息:', userMessage)

    // 调用SSE接口
    await chatWithAI(memoryId.value, userMessage, (chunk: string) => {
      console.log('接收到数据块:', chunk)
      hasReceivedContent = true
      // 更新AI消息内容
      messages.value[aiMessageIndex].content += chunk
      scrollToBottom()
    })

    console.log('SSE连接成功完成')
    
    // 如果没有接收到任何内容，显示提示信息
    if (!hasReceivedContent) {
      messages.value[aiMessageIndex].content = '抱歉，没有收到回复内容，请重试。'
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    
    // 移除失败的消息
    if (messages.value.length > 0 && messages.value[messages.value.length - 1].content === '') {
      messages.value.pop()
    }
    
    // 添加更详细的错误消息
    const errorMessage = error instanceof Error ? error.message : '未知错误'
    messages.value.push({
      type: 'ai',
      content: `连接失败: ${errorMessage}。请检查后端服务是否正常运行，或稍后重试。`,
      timestamp: new Date()
    })
  } finally {
    isLoading.value = false
    isTyping.value = false
    scrollToBottom()
  }
}
</script>

<template>
  <div class="chat-container">
    <!-- 标题栏 -->
    <div class="chat-header">
      <h1>AI 编程小助手</h1>
      <div class="chat-id">会话ID: {{ memoryId }}</div>
    </div>

    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.type]"
      >
        <div class="message-content">
          <div class="avatar">
            {{ message.type === 'user' ? 'U' : 'AI' }}
          </div>
          <div class="message-text">
            <div v-if="message.type === 'user'">{{ message.content }}</div>
            <div v-else v-html="renderMarkdown(message.content)"></div>
          </div>
        </div>
        <div class="message-time">{{ formatTime(message.timestamp) }}</div>
      </div>
      
      <!-- 打字指示器 -->
      <div v-if="isTyping" class="message ai typing-indicator">
        <div class="message-content">
          <div class="avatar">AI</div>
          <div class="typing-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input">
      <div class="input-container">
        <textarea
          v-model="inputMessage"
          placeholder="请输入您的编程问题..."
          @input="autoResize"
          ref="messageInput"
          :disabled="isLoading"
        ></textarea>
        <button 
          @click="sendMessage" 
          :disabled="!inputMessage.trim() || isLoading"
          class="send-button"
        >
          {{ isLoading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 50%, #0d1117 100%);
  position: fixed;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
}

.chat-header {
  background: linear-gradient(90deg, #0d1117 0%, #161b22 50%, #21262d 100%);
  border-bottom: 1px solid #30363d;
  color: #f0f6fc;
  padding: 1rem 2rem;
  text-align: center;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
}

.chat-header h1 {
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
  color: #00d4ff;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.chat-id {
  font-size: 0.875rem;
  color: #7c3aed;
  margin-top: 0.25rem;
  text-shadow: 0 0 5px rgba(124, 58, 237, 0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  background: linear-gradient(180deg, #0a0a0a 0%, #1a1a1a 100%);
  min-height: 0;
  padding: 0;
}

.message {
  width: 100%;
  padding: 1.5rem 0;
  display: flex;
  justify-content: center;
  animation: fadeIn 0.3s ease-in;
  border-bottom: 1px solid rgba(48, 54, 61, 0.3);
}

.message.user {
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
}

.message.ai {
  background: linear-gradient(90deg, rgba(33, 38, 45, 0.8) 0%, rgba(13, 17, 23, 0.8) 100%);
}

.message-content {
  width: 100%;
  max-width: 48rem;
  padding: 0 2rem;
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
  border: 2px solid;
}

.message.user .avatar {
  background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%);
  color: #000;
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.message.ai .avatar {
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border-color: #7c3aed;
  box-shadow: 0 0 15px rgba(124, 58, 237, 0.4);
}

.message-text {
  flex: 1;
  line-height: 1.6;
  font-size: 1rem;
  color: #f0f6fc;
  word-wrap: break-word;
  min-width: 0;
}

.message.user .message-text {
  color: #e6ffff;
}

.message-time {
  font-size: 0.75rem;
  color: #7d8590;
  margin-top: 0.5rem;
  text-align: right;
}

.message.ai .message-time {
  text-align: left;
}

.typing-indicator {
  background: linear-gradient(90deg, rgba(33, 38, 45, 0.8) 0%, rgba(13, 17, 23, 0.8) 100%);
}

.typing-indicator .message-content {
  opacity: 0.8;
}

.typing-dots {
  display: flex;
  gap: 0.25rem;
  align-items: center;
}

.typing-dots span {
  width: 0.5rem;
  height: 0.5rem;
  background: #7c3aed;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
  box-shadow: 0 0 5px rgba(124, 58, 237, 0.5);
}

.typing-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

.chat-input {
  background: linear-gradient(180deg, #161b22 0%, #0d1117 100%);
  border-top: 1px solid #30363d;
  padding: 1.5rem 0 2rem 0;
  flex-shrink: 0;
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: center;
  box-shadow: 0 -4px 20px rgba(0, 212, 255, 0.1);
}

.input-container {
  width: 100%;
  max-width: 48rem;
  padding: 0 2rem;
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
}

textarea {
  flex: 1;
  min-height: 3rem;
  max-height: 12rem;
  padding: 0.75rem 1rem;
  border: 2px solid #30363d;
  border-radius: 0.75rem;
  resize: none;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.5;
  transition: all 0.3s;
  background: rgba(13, 17, 23, 0.8);
  color: #f0f6fc;
  backdrop-filter: blur(10px);
}

textarea::placeholder {
  color: #7d8590;
}

textarea:focus {
  outline: none;
  border-color: #00d4ff;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.3);
  background: rgba(13, 17, 23, 0.9);
}

textarea:disabled {
  background: rgba(13, 17, 23, 0.5);
  color: #7d8590;
  cursor: not-allowed;
}

.send-button {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%);
  color: #000;
  border: none;
  border-radius: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 1rem;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.send-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.send-button:hover:not(:disabled)::before {
  left: 100%;
}

.send-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #00e5ff 0%, #00b0ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.5);
}

.send-button:disabled {
  background: linear-gradient(135deg, #30363d 0%, #21262d 100%);
  color: #7d8590;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Markdown 样式 */
:deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
  margin: 1.5rem 0 0.75rem 0;
  font-weight: 600;
  line-height: 1.3;
  color: #00d4ff;
  text-shadow: 0 0 5px rgba(0, 212, 255, 0.3);
}

:deep(h1) { font-size: 1.875rem; }
:deep(h2) { font-size: 1.5rem; }
:deep(h3) { font-size: 1.25rem; }
:deep(h4) { font-size: 1.125rem; }

:deep(p) {
  margin: 0.75rem 0;
  line-height: 1.6;
  color: #f0f6fc;
}

:deep(ul), :deep(ol) {
  margin: 0.75rem 0;
  padding-left: 1.5rem;
}

:deep(li) {
  margin: 0.25rem 0;
  line-height: 1.6;
  color: #f0f6fc;
}

:deep(blockquote) {
  border-left: 3px solid #7c3aed;
  margin: 1rem 0;
  padding: 0.75rem 1rem;
  background: rgba(124, 58, 237, 0.1);
  color: #e6ffff;
  border-radius: 0 0.375rem 0.375rem 0;
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.2);
}

:deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  border-radius: 0.5rem;
  overflow: hidden;
  border: 1px solid #30363d;
  background: rgba(13, 17, 23, 0.8);
}

:deep(th), :deep(td) {
  border: 1px solid #30363d;
  padding: 0.75rem;
  text-align: left;
}

:deep(th) {
  background: rgba(0, 212, 255, 0.1);
  font-weight: 600;
  color: #00d4ff;
}

:deep(td) {
  color: #f0f6fc;
}

/* 代码块样式 */
:deep(pre) {
  background: linear-gradient(135deg, #0d1117 0%, #161b22 100%) !important;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Source Code Pro', monospace;
  font-size: 0.875rem;
  line-height: 1.5;
  border: 1px solid #30363d;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

:deep(code) {
  background: rgba(0, 212, 255, 0.1);
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Source Code Pro', monospace;
  color: #00d4ff;
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: 0 0 5px rgba(0, 212, 255, 0.1);
}

:deep(pre code) {
  background: transparent;
  padding: 0;
  color: #f0f6fc;
  border: none;
  font-size: 0.875rem;
  box-shadow: none;
}

:deep(a) {
  color: #00d4ff;
  text-decoration: none;
  font-weight: 500;
  text-shadow: 0 0 5px rgba(0, 212, 255, 0.3);
}

:deep(a:hover) {
  text-decoration: underline;
  color: #00e5ff;
}

:deep(strong) {
  font-weight: 600;
  color: #00d4ff;
}

:deep(em) {
  font-style: italic;
  color: #a855f7;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(13, 17, 23, 0.5);
}

.chat-messages::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #7c3aed 0%, #00d4ff 100%);
  border-radius: 4px;
  box-shadow: 0 0 5px rgba(0, 212, 255, 0.3);
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #a855f7 0%, #00e5ff 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 1rem;
  }
  
  .chat-header h1 {
    font-size: 1.25rem;
  }
  
  .message {
    padding: 1rem 0;
  }
  
  .message-content {
    max-width: none;
    padding: 0 1rem;
    gap: 1rem;
  }
  
  .avatar {
    width: 1.75rem;
    height: 1.75rem;
    font-size: 0.75rem;
  }
  
  .message-text {
    font-size: 0.95rem;
  }
  
  .chat-input {
    padding: 1rem 0 1.5rem 0;
  }
  
  .input-container {
    max-width: none;
    padding: 0 1rem;
  }
  
  textarea {
    font-size: 1rem;
    padding: 0.75rem;
  }
  
  .send-button {
    padding: 0.75rem 1.25rem;
    font-size: 0.9rem;
  }
  
  :deep(h1) { font-size: 1.5rem; }
  :deep(h2) { font-size: 1.25rem; }
  :deep(h3) { font-size: 1.125rem; }
  :deep(h4) { font-size: 1rem; }
  
  :deep(pre) {
    padding: 0.75rem;
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .chat-header {
    padding: 0.75rem;
  }
  
  .chat-header h1 {
    font-size: 1.125rem;
  }
  
  .chat-id {
    font-size: 0.8rem;
  }
  
  .message {
    padding: 0.75rem 0;
  }
  
  .message-content {
    padding: 0 0.75rem;
    gap: 0.75rem;
  }
  
  .avatar {
    width: 1.5rem;
    height: 1.5rem;
    font-size: 0.7rem;
  }
  
  .message-text {
    font-size: 0.9rem;
  }
  
  .input-container {
    padding: 0 0.75rem;
    gap: 0.5rem;
  }
  
  textarea {
    padding: 0.625rem;
    border-radius: 0.5rem;
  }
  
  .send-button {
    padding: 0.625rem 1rem;
    font-size: 0.875rem;
    border-radius: 0.5rem;
  }
}
</style>
