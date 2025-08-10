// 图书馆公告 API 定义
// 调整响应类型以匹配后端实际返回 { code, message, data }
import request from '@/utils/http'

export interface Notice {
  id: number
  title: string
  content: string
  createdAt?: string
  updatedAt?: string
}

export interface NoticePayload {
  title: string
  content: string
}

interface BackendResp<T> {
  code: number
  message: string
  data: T
}

const baseUrl = '/notices'

// 获取公告列表
export const getNotices = () =>
  request.get<BackendResp<Notice[]>>({
    url: baseUrl
  })

// 根据ID获取公告
export const getNoticeById = (id: number) =>
  request.get<BackendResp<Notice>>({
    url: `${baseUrl}/${id}`
  })

// 创建公告
export const createNotice = (payload: NoticePayload) =>
  request.post<BackendResp<Notice>>({
    url: baseUrl,
    data: payload
  })

// 更新公告
export const updateNotice = (id: number, payload: NoticePayload) =>
  request.put<BackendResp<Notice>>({
    url: `${baseUrl}/${id}`,
    data: payload
  })

// 删除公告
export const deleteNotice = (id: number) =>
  request.del<BackendResp<Notice>>({
    url: `${baseUrl}/${id}`
  })
