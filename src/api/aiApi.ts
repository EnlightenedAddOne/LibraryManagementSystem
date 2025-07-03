// src/api/aiApi.ts
import axios from 'axios'

/**
 * 向AI后端发送消息，获取AI回复
 * @param message 用户输入内容
 * @returns Promise<string> AI回复内容
 */
export async function askAI(message: string): Promise<string> {
  const res = await axios.post('http://localhost:8080/api/ai/ask', { message })
  if (res.data && res.data.code === 200) {
    return res.data.data
  } else {
    throw new Error(res.data?.message || 'AI回复失败')
  }
}
