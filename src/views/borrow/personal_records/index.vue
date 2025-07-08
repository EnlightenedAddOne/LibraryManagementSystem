<template>
  <ArtTableFullScreen>
    <div class="personal-records-page">
      <!-- 统计卡片和搜索框 -->
      <div class="top-section">
        <div class="stats-cards">
          <div class="stat-card total">
            <div class="stat-number">{{ borrowRecords.length }}</div>
            <div class="stat-label">总借阅数</div>
          </div>
          <div class="stat-card overdue">
            <div class="stat-number">{{ overdueCount }}</div>
            <div class="stat-label">已逾期</div>
          </div>
          <div class="stat-card borrowing">
            <div class="stat-number">{{ borrowingCount }}</div>
            <div class="stat-label">借阅中</div>
          </div>
        </div>

        <div class="search-section">
          <ElInput
            v-model="searchKeyword"
            placeholder="搜索图书名称"
            clearable
            style="width: 300px"
            prefix-icon="Search"
            @input="filterRecords"
          />
        </div>
      </div>

      <!-- 筛选器 -->
      <ElCard shadow="never" class="filter-card">
        <div class="filter-row">
          <ElSelect
            v-model="statusFilter"
            placeholder="选择状态"
            clearable
            style="width: 200px"
            @change="filterRecords"
          >
            <ElOption label="全部" value="" />
            <ElOption label="借阅中" value="borrowing" />
            <ElOption label="已归还" value="returned" />
            <ElOption label="已逾期" value="overdue" />
            <ElOption label="逾期归还" value="ReturnOver" />
          </ElSelect>
        </div>
      </ElCard>

      <!-- 借阅记录表格 -->
      <ElCard shadow="never" class="table-card">
        <div class="table-container">
          <ElTable
            v-loading="loading"
            :data="filteredRecords"
            stripe
            style="width: 100%"
            :empty-text="loading ? '加载中...' : '暂无借阅记录'"
            height="600"
          >
            <!-- 图书信息列 -->
            <ElTableColumn label="图书信息" min-width="300">
              <template #default="{ row }">
                <div class="book-info">
                  <div class="book-cover-container">
                    <img
                      v-if="row.bookCover"
                      :src="row.bookCover"
                      :alt="row.bookTitle"
                      class="book-cover"
                      @error="handleImageError"
                    />
                    <div v-else class="no-cover"> 暂无封面 </div>
                  </div>
                  <div class="book-details">
                    <div class="book-title">{{ row.bookTitle }}</div>
                    <div class="book-meta">
                      <span class="author">{{ row.bookAuthor }}</span>
                      <span class="divider">•</span>
                      <span class="publisher">{{ row.bookPublisher }}</span>
                    </div>
                    <div class="book-category">{{ row.bookCategory }}</div>
                  </div>
                </div>
              </template>
            </ElTableColumn>

            <!-- 借阅信息列 -->
            <ElTableColumn label="借阅信息" min-width="200">
              <template #default="{ row }">
                <div class="borrow-info">
                  <div class="info-row">
                    <span class="label">借阅日期:</span>
                    <span class="value">{{ formatDate(row.borrowDate) }}</span>
                  </div>
                  <div class="info-row" v-if="row.dueDate">
                    <span class="label">应还日期:</span>
                    <span class="value">{{ formatDate(row.dueDate) }}</span>
                  </div>
                  <div class="info-row" v-if="row.returnDate">
                    <span class="label">归还日期:</span>
                    <span class="value">{{ formatDate(row.returnDate) }}</span>
                  </div>
                </div>
              </template>
            </ElTableColumn>

            <!-- 状态列 -->
            <ElTableColumn label="状态" width="120" align="center">
              <template #default="{ row }">
                <ElTag :type="getStatusTagType(row)" size="large">
                  <i :class="getStatusIcon(row)"></i>
                  {{ getStatusText(row) }}
                </ElTag>
              </template>
            </ElTableColumn>

            <!-- 操作列 -->
            <ElTableColumn label="操作" width="120" align="center">
              <template #default="{ row }">
                <ElButton
                  v-if="!row.returnDate"
                  type="primary"
                  size="small"
                  :disabled="loading"
                  @click="showReturnDialog(row)"
                >
                  归还图书
                </ElButton>
                <span v-else class="returned-text">已归还</span>
              </template>
            </ElTableColumn>
          </ElTable>
        </div>

        <!-- 分页器 -->
        <div class="pagination-container" v-if="borrowRecords.length > 0">
          <ElPagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </ElCard>

      <!-- 归还图书对话框 -->
      <ElDialog
        v-model="returnDialogVisible"
        title="归还图书"
        width="500px"
        :close-on-click-modal="false"
        align-center
      >
        <div class="return-dialog-content">
          <div class="book-summary">
            <div class="dialog-book-cover-container">
              <img
                v-if="currentReturnBook?.bookCover"
                :src="currentReturnBook?.bookCover"
                :alt="currentReturnBook?.bookTitle"
                class="dialog-book-cover"
              />
              <div v-else class="dialog-no-cover"> 暂无封面 </div>
            </div>
            <div class="dialog-book-info">
              <h4>{{ currentReturnBook?.bookTitle }}</h4>
              <p>{{ currentReturnBook?.bookAuthor }}</p>
              <p class="borrow-date">借阅日期: {{ formatDate(currentReturnBook?.borrowDate) }}</p>
              <p class="due-date">应还日期: {{ formatDate(currentReturnBook?.dueDate) }}</p>
            </div>
          </div>

          <ElDivider />

          <ElForm label-width="100px">
            <ElFormItem label="归还日期" required>
              <ElInput
                :value="formatDate(returnDate)"
                readonly
                style="width: 100%"
                placeholder="系统当前时间"
              />
            </ElFormItem>
          </ElForm>
        </div>

        <template #footer>
          <div class="dialog-footer">
            <ElButton @click="returnDialogVisible = false" :disabled="returnLoading">
              取消
            </ElButton>
            <ElButton type="primary" @click="handleReturn" :loading="returnLoading">
              确认归还
            </ElButton>
          </div>
        </template>
      </ElDialog>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted, watch } from 'vue'
  import { ElMessage } from 'element-plus'
  import { useUserStore } from '@/store/modules/user'
  import { BorrowService } from '@/api/borrowApi'
  import { ApiStatus } from '@/utils/http/status'

  defineOptions({ name: 'PersonalRecords' })

  const userStore = useUserStore()

  // 响应式数据
  const loading = ref(false)
  const returnLoading = ref(false)
  const borrowRecords = ref<any[]>([])
  const filteredRecords = ref<any[]>([])
  const returnDialogVisible = ref(false)
  const returnDate = ref('')
  const statusFilter = ref('')
  const searchKeyword = ref('')
  const currentReturnBook = ref<any>(null)

  // 分页数据
  const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
  })

  // 计算统计数据
  const borrowingCount = computed(() => {
    return borrowRecords.value.filter((record) => getStatusText(record) === '借阅中').length
  })

  const overdueCount = computed(() => {
    return borrowRecords.value.filter((record) => getStatusText(record) === '已逾期').length
  })

  // 获取当前用户借阅记录
  // 获取当前用户借阅记录
  const loadRecords = async () => {
    try {
      loading.value = true
      const userId = userStore.info?.userId

      if (!userId) {
        ElMessage.error('用户信息不存在，请重新登录')
        return
      }

      console.log('Loading records for user:', userId)

      const res = await BorrowService.getBorrowRecordsByUserId(userId)
      console.log('API Response:', res)

      if (res.code === ApiStatus.success) {
        // 处理单条记录或多条记录的情况
        let records: any[] = []

        if (res.data) {
          if (Array.isArray(res.data)) {
            records = res.data
          } else {
            // 如果返回的是单个对象，转换为数组
            records = [res.data]
          }
        }

        // 数据转换和扁平化
        borrowRecords.value = records
          .map((record: any) => {
            console.log('Processing record:', record)

            return {
              recordId: record.recordId,
              borrowId: record.borrowId || record.recordId,
              bookTitle: record.bookId?.title || record.title || '未知图书',
              bookAuthor: record.bookId?.author || record.author || '未知作者',
              bookPublisher: record.bookId?.publisher || record.publisher || '未知出版社',
              bookCategory: record.bookId?.category || record.category || '未分类',
              bookCover: record.bookId?.cover || record.cover,
              borrowDate: record.borrowDate,
              dueDate: record.dueDate, // 应还日期
              returnDate: record.returnDate, // 实际归还日期
              // actualReturnDate: record.actualReturnDate,
              status: record.status,
              userId: record.user?.userId || record.userId,
              bookId: record.bookId?.bookId || record.bookId,
              // 保留原始数据
              _original: record
            }
          })
          // 按借阅日期降序排序，最新的借阅记录排在前面
          .sort((a, b) => {
            const dateA = new Date(a.borrowDate || 0).getTime()
            const dateB = new Date(b.borrowDate || 0).getTime()
            return dateB - dateA // 降序排序
          })

        console.log('Processed records:', borrowRecords.value)

        // 初始化过滤
        filterRecords()

        if (borrowRecords.value.length === 0) {
          ElMessage.info('暂无借阅记录')
        }
      } else {
        ElMessage.error(res.message || '获取借阅记录失败')
        borrowRecords.value = []
      }
    } catch (error) {
      console.error('Load records error:', error)
      ElMessage.error('获取借阅记录失败，请重试')
      borrowRecords.value = []
    } finally {
      loading.value = false
    }
  }

  // 过滤记录
  const filterRecords = () => {
    let filtered = [...borrowRecords.value]

    // 状态过滤
    if (statusFilter.value) {
      filtered = filtered.filter((record) => {
        const status = getRecordStatus(record)
        return status === statusFilter.value
      })
    }

    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter(
        (record) =>
          record.bookTitle.toLowerCase().includes(keyword) ||
          record.bookAuthor.toLowerCase().includes(keyword)
      )
    }

    // 应用分页
    pagination.total = filtered.length
    const start = (pagination.currentPage - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    filteredRecords.value = filtered.slice(start, end)
  }

  // 获取记录状态 - 修复状态判断逻辑
  const getRecordStatus = (record: any) => {
    // 如果有实际归还日期且归还日期在应还日期之后，则为逾期归还
    if (record.returnDate && record.returnDate > record.dueDate) {
      return 'ReturnOver'
    }
    // 如果已经有实际归还日期，则为已归还
    if (record.returnDate) return 'returned'

    // 如果没有应还日期，则为借阅中
    if (!record.returnDate) return 'borrowing'

    // 比较当前日期与应还日期（dueDate）
    const currentDate = new Date()
    const dueDate = new Date(record.dueDate)

    // 设置时间为当天的开始时间以便准确比较
    currentDate.setHours(0, 0, 0, 0)
    dueDate.setHours(0, 0, 0, 0)

    return currentDate > dueDate ? 'overdue' : 'borrowing'
  }

  // 格式化日期
  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return 'N/A'
    try {
      const date = new Date(dateString)
      if (isNaN(date.getTime())) return 'N/A'
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    } catch {
      return 'N/A'
    }
  }

  // 获取状态文本 - 使用修复后的状态判断逻辑
  const getStatusText = (record: any) => {
    const status = getRecordStatus(record)
    switch (status) {
      case 'returned':
        return '已归还'
      case 'overdue':
        return '已逾期'
      case 'borrowing':
        return '借阅中'
      case 'ReturnOver':
        return '逾期归还'
      default:
        return '借阅中'
    }
  }

  // 获取状态标签类型
  const getStatusTagType = (record: any) => {
    const status = getStatusText(record)
    switch (status) {
      case '已归还':
        return 'success'
      case '已逾期':
        return 'danger'
      case '逾期归还':
        return 'warning'
      case '借阅中':
        return 'warning'
      default:
        return 'info'
    }
  }

  // 获取状态图标
  const getStatusIcon = (record: any) => {
    const status = getStatusText(record)
    switch (status) {
      case '已归还':
        return 'el-icon-check'
      case '已逾期':
        return 'el-icon-warning'
      case '借阅中':
        return 'el-icon-time'
      default:
        return 'el-icon-info'
    }
  }

  // 显示归还对话框
  const showReturnDialog = (record: any) => {
    currentReturnBook.value = record
    // 设置为当前系统时间，格式化为 YYYY-MM-DD
    returnDate.value = new Date().toISOString().split('T')[0]
    returnDialogVisible.value = true
  }

  // 执行归还操作
  const handleReturn = async () => {
    if (!returnDate.value) {
      ElMessage.error('请选择归还日期')
      return
    }

    if (!currentReturnBook.value) {
      ElMessage.error('未选择要归还的图书')
      return
    }

    try {
      returnLoading.value = true

      const borrowId = currentReturnBook.value.borrowId || currentReturnBook.value.recordId

      await BorrowService.returnBook(borrowId, {
        actualReturnDate: returnDate.value
      })

      ElMessage.success('归还成功！')
      returnDialogVisible.value = false

      // 重新加载数据
      await loadRecords()
    } catch (error) {
      console.error('Return book error:', error)
      ElMessage.error('归还失败，请重试')
    } finally {
      returnLoading.value = false
    }
  }

  // 处理图片加载错误
  const handleImageError = (event: Event) => {
    const img = event.target as HTMLImageElement
    img.style.display = 'none'
    const container = img.parentElement
    if (container) {
      container.innerHTML = '<div class="no-cover">暂无封面</div>'
    }
  }

  // 分页处理
  const handleSizeChange = (size: number) => {
    pagination.pageSize = size
    pagination.currentPage = 1
    filterRecords()
  }

  const handleCurrentChange = (page: number) => {
    pagination.currentPage = page
    filterRecords()
  }

  // 监听过滤条件变化
  watch([statusFilter, searchKeyword], () => {
    pagination.currentPage = 1
    filterRecords()
  })

  onMounted(() => {
    loadRecords()
  })
</script>

<style lang="scss" scoped>
  .personal-records-page {
    .top-section {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;

      .stats-cards {
        display: flex;
        gap: 16px;

        .stat-card {
          min-width: 100px;
          padding: 20px;
          color: white;
          text-align: center;
          border-radius: 12px;

          &.total {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            box-shadow: 0 4px 12px rgb(102 126 234 / 30%);
          }

          &.overdue {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            box-shadow: 0 4px 12px rgb(240 147 251 / 30%);
          }

          &.borrowing {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            box-shadow: 0 4px 12px rgb(79 172 254 / 30%);
          }

          .stat-number {
            margin-bottom: 8px;
            font-size: 28px;
            font-weight: bold;
          }

          .stat-label {
            font-size: 14px;
            opacity: 0.9;
          }
        }
      }

      .search-section {
        flex-shrink: 0;
      }
    }

    .filter-card {
      margin-bottom: 20px;

      .filter-row {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
        align-items: center;
      }
    }

    .table-card {
      .table-container {
        max-height: 70vh; // 限制最大高度
        overflow: auto; // 添加滚动支持
      }

      // 确保表格内容可以正常滚动
      :deep(.el-table) {
        .el-table__body-wrapper {
          overflow-y: auto !important;
        }
      }

      .book-info {
        display: flex;
        gap: 12px;
        align-items: center;

        .book-cover-container {
          display: flex;
          flex-shrink: 0;
          align-items: center;
          justify-content: center;
          width: 50px;
          height: 65px;
          overflow: hidden;
          background: var(--el-fill-color-light);
          border-radius: 6px;
          box-shadow: 0 2px 8px rgb(0 0 0 / 10%);

          .book-cover {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .no-cover {
            padding: 4px;
            font-size: 12px;
            line-height: 1.2;
            color: var(--el-text-color-placeholder);
            text-align: center;
          }
        }

        .book-details {
          flex: 1;
          min-width: 0;

          .book-title {
            margin-bottom: 4px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            word-break: break-word;
          }

          .book-meta {
            margin-bottom: 4px;
            font-size: 13px;
            color: var(--el-text-color-regular);

            .divider {
              margin: 0 6px;
              opacity: 0.5;
            }
          }

          .book-category {
            display: inline-block;
            padding: 2px 8px;
            font-size: 12px;
            color: var(--el-text-color-secondary);
            background: var(--el-fill-color-light);
            border-radius: 12px;
          }
        }
      }

      .borrow-info {
        .info-row {
          margin-bottom: 6px;
          font-size: 14px;

          &:last-child {
            margin-bottom: 0;
          }

          .label {
            margin-right: 8px;
            color: var(--el-text-color-secondary);
          }

          .value {
            font-weight: 500;
            color: var(--el-text-color-primary);
          }
        }
      }

      .returned-text {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      .pagination-container {
        display: flex;
        justify-content: flex-end;
        padding-top: 20px;
        margin-top: 20px;
        border-top: 1px solid var(--el-border-color-lighter);
      }
    }

    .return-dialog-content {
      .book-summary {
        display: flex;
        gap: 16px;
        align-items: center;
        padding: 16px;
        margin-bottom: 20px;
        background: var(--el-fill-color-extra-light);
        border-radius: 8px;

        .dialog-book-cover-container {
          display: flex;
          flex-shrink: 0;
          align-items: center;
          justify-content: center;
          width: 60px;
          height: 80px;
          overflow: hidden;
          background: var(--el-fill-color-light);
          border-radius: 6px;
          box-shadow: 0 2px 8px rgb(0 0 0 / 10%);

          .dialog-book-cover {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .dialog-no-cover {
            padding: 4px;
            font-size: 12px;
            line-height: 1.2;
            color: var(--el-text-color-placeholder);
            text-align: center;
          }
        }

        .dialog-book-info {
          flex: 1;

          h4 {
            margin: 0 0 8px;
            font-weight: 600;
            color: var(--el-text-color-primary);
          }

          p {
            margin: 4px 0;
            font-size: 14px;
            color: var(--el-text-color-regular);

            &.borrow-date {
              margin-top: 8px;
              font-size: 13px;
              color: var(--el-text-color-secondary);
            }

            &.due-date {
              font-size: 13px;
              color: var(--el-text-color-secondary);
            }
          }
        }
      }
    }
  }

  // 响应式设计
  @media (width <= 768px) {
    .personal-records-page {
      .top-section {
        flex-direction: column;
        align-items: stretch;

        .stats-cards {
          justify-content: space-between;

          .stat-card {
            flex: 1;
            min-width: 0;
          }
        }

        .search-section {
          width: 100%;

          .el-input {
            width: 100% !important;
          }
        }
      }

      .filter-card .filter-row {
        flex-direction: column;
        align-items: stretch;

        .el-select {
          width: 100% !important;
        }
      }
    }
  }
</style>
