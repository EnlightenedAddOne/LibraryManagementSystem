import request from '@/utils/http'
import { BaseResponse } from '@/types/api'

// 更新接口定义以匹配API请求格式
export interface BorrowRecordRequest {
  bookId: {
    bookId: number
  }
  user: {
    userId: number
  }
  borrowDate: string
  dueDate: string
}

export interface BorrowRecordResponse {
  borrowId: number
  bookId: number
  userId: string
  borrowDate: string
  dueDate: string
  returnDate: string
  actualReturnDate?: string
  status: number // 0: 借阅中, 1: 已归还, 2: 逾期
  title: string
}

export class BorrowService {
  // 借书 - 使用嵌套对象格式
  static createBorrowRecord(data: BorrowRecordRequest) {
    return request.post<BaseResponse>({
      url: '/borrow-records',
      data
    })
  }

  // 获取借阅记录列表
  static getBorrowRecords(params?: {
    current?: number
    size?: number
    userId?: string | number
    bookId?: number
  }) {
    return request.get<
      BaseResponse<{
        records: BorrowRecordResponse[]
        total: number
        current: number
        size: number
      }>
    >({
      url: '/borrow-records',
      params
    })
  }

  // 添加按用户ID查询借阅记录的方法
  static getBorrowRecordsByUserId(userId: number) {
    return request.get<BaseResponse<BorrowRecordResponse[]>>({
      url: `/borrow-records/user/${userId}`
    })
  }

  // 归还图书
  static returnBook(borrowId: number, data: { actualReturnDate: string }) {
    return request.put<BaseResponse>({
      url: `/borrow-records/${borrowId}/return`,
      data
    })
  }

  // 检查图书是否可借阅
  static checkBookAvailability(bookId: number) {
    return request.get<BaseResponse<{ available: boolean }>>({
      url: `/books/${bookId}/availability`
    })
  }
}
