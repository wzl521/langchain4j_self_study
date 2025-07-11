import axios from 'axios'

// API 基础配置
const API_BASE_URL = 'http://localhost:8080/api'

// SSE 聊天接口
export const chatWithAI = async (
    memoryId: number,
    message: string,
    onChunk: (chunk: string) => void
): Promise<void> => {
    return new Promise((resolve, reject) => {
        // 构建 URL 参数
        const params = new URLSearchParams({
            memoryId: memoryId.toString(),
            message: message
        })

        // 创建 EventSource 连接
        const eventSource = new EventSource(`${API_BASE_URL}/ai/chat?${params}`)

        let hasReceivedData = false
        let lastDataTime = Date.now()

        // 检查是否长时间没有数据的定时器
        const checkTimeout = setInterval(() => {
            const now = Date.now()
            // 如果超过5秒没有新数据且已经接收到数据，则认为流结束
            if (hasReceivedData && (now - lastDataTime) > 5000) {
                console.log('SSE流超时，自动关闭连接')
                eventSource.close()
                clearInterval(checkTimeout)
                resolve()
            }
        }, 1000)

        eventSource.onmessage = (event) => {
            try {
                // 解析 SSE 数据
                const data = event.data
                lastDataTime = Date.now()

                console.log('收到SSE数据:', data)

                // 如果收到结束信号，关闭连接
                if (data === '[DONE]' || data === 'DONE' || data === '') {
                    console.log('收到结束信号，关闭连接')
                    eventSource.close()
                    clearInterval(checkTimeout)
                    resolve()
                    return
                }

                hasReceivedData = true
                // 调用回调函数处理数据块
                onChunk(data)
            } catch (error) {
                console.error('解析 SSE 数据失败:', error)
                eventSource.close()
                clearInterval(checkTimeout)
                reject(error)
            }
        }

        eventSource.onerror = (error) => {
            console.error('SSE 连接错误:', error)
            console.log('EventSource readyState:', eventSource.readyState)
            console.log('已接收数据:', hasReceivedData)

            clearInterval(checkTimeout)
            eventSource.close()

            // 如果已经接收到数据，很可能是服务器正常关闭连接
            // 大多数SSE服务器在流结束时会触发error事件，这是正常行为
            if (hasReceivedData) {
                console.log('已接收到数据，将连接关闭视为正常结束')
                resolve()
            } else {
                // 只有在没有接收到任何数据时才认为是真正的错误
                console.log('未接收到数据，认为是连接错误')
                reject(new Error('与服务器连接失败'))
            }
        }

        eventSource.onopen = () => {
            console.log('SSE连接已建立')
        }

        // 设置总超时时间
        setTimeout(() => {
            if (eventSource.readyState !== EventSource.CLOSED) {
                console.log('请求总超时，关闭连接')
                eventSource.close()
                clearInterval(checkTimeout)
                if (hasReceivedData) {
                    resolve() // 如果已经收到数据，认为是成功的
                } else {
                    reject(new Error('请求超时'))
                }
            }
        }, 60000) // 60秒总超时
    })
}

// 测试连接
export const testConnection = async (): Promise<boolean> => {
    try {
        const response = await axios.get(`${API_BASE_URL}/ai/chat?memoryId=999&message=test`, {
            timeout: 5000
        })
        return response.status === 200
    } catch (error) {
        console.error('连接测试失败:', error)
        return false
    }
} 