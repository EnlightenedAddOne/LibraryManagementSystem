import request from '@/utils/http'
import { BaseResponse } from '@/types/api'

export interface Book {
  bookId: number // 图书唯一标识
  title: string // 图书标题
  author: string // 作者
  publisher: string // 出版社
  isbn: string // ISBN编号
  category: string // 图书分类
  description?: string // 图书描述（可选）
  cover?: string // 图书封面图片地址（可选）
  status: number // 图书状态：0表示可借阅，1表示已借出
  createTime: string // 图书创建时间
  publishDate: string // 图书出版日期
}

export interface BookQueryParams {
  current?: number // 当前页码（可选）
  size?: number // 每页显示的条数（可选）
  title?: string // 按书名查询（可选）
  author?: string // 按作者查询（可选）
  category?: string // 按分类查询（可选）
}

export class BookService {
  // 获取图书列表
  static getBookList(params: BookQueryParams) {
    return request.get<BaseResponse>({
      url: '/books',
      params
    })
  }

  // 添加图书
  static addBook(data: Partial<Book>) {
    return request.post<BaseResponse>({
      url: '/books',
      data
    })
  }

  // 更新图书
  static updateBook(id: number, data: Partial<Book>) {
    return request.put<BaseResponse>({
      url: `/books/${id}`,
      data
    })
  }

  // 删除图书
  static deleteBook(id: number) {
    return request.del<BaseResponse>({
      url: `/books/${id}`
    })
  }

  // 获取图书详情
  static getBookDetail(id: number) {
    return request.get<BaseResponse>({
      url: `/books/${id}`
    })
  }
}
