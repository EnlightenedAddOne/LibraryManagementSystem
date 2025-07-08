import request from '@/utils/http'

/**
 * 获取图书馆整体数据统计
 * @returns Promise<LibraryStatsResponse>
 */
export function getLibraryStats() {
  return request.get<LibraryStatsResponse>({
    url: '/library/stats'
  })
}

// 类型定义
export interface LibraryStatsResponse {
  code: number
  message: string
  data: {
    totalBooks: number // 当前馆藏图书总数
    totalBooksChange: string // 较上月变化百分比
    totalReaders: number // 当前读者总数
    totalReadersChange: string // 较上月变化百分比
    borrowCountThisMonth: number // 本月借阅量
    borrowCountChange: string // 较上月变化百分比
    newReadersThisMonth: number // 本月新增读者
    newReadersChange: string // 较上月变化百分比
    bookCategoryCount: Record<string, number> // 各分类图书数量

    lastWeekBorrowCountPerDay: Record<string, number> // 上周每天借阅量
    lastWeekTotalBorrow: number // 上周总借阅量
    currentBorrowingCount: number // 当前借阅中数量
    lastWeekAvgBorrow: number // 上周日均借阅量
    lastWeekReturnRate: string // 上周归还率
    totalBorrowCount: number // 系统总借阅量
  }
}
