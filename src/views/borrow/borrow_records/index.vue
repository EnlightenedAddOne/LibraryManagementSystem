<template>
  <ArtTableFullScreen>
    <div class="account-page" id="table-full-screen">
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="handleSearch"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="handleRefresh"
        >
          <template #left>
            <ElButton @click="showDialog('add')">新增用户</ElButton>
          </template>
        </ArtTableHeader> -->

        <ArtTable
          ref="tableRef"
          row-key="id"
          :loading="loading"
          :data="tableData"
          :currentPage="pagination.currentPage"
          :pageSize="pagination.pageSize"
          :total="pagination.total"
          :marginTop="10"
          @selection-change="handleSelectionChange"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
          <template #default>
            <ElTableColumn v-for="col in columns" :key="col.prop || col.type" v-bind="col" />
          </template>
        </ArtTable>

        <!-- 借阅记录对话框 -->
        <ElDialog v-model="borrowDialogVisible" width="80%" align-center>
          <template #title>
            <div style="font-size: 18px; text-align: center">用户借阅记录</div>
          </template>
          <div v-if="!borrowLoading && borrowRecords.length === 0" class="empty-state">
            <p>该用户暂无借阅记录</p>
          </div>

          <!-- 使用 ElTable 直接显示借阅记录 -->
          <ElTable
            v-loading="borrowLoading"
            :data="borrowRecords"
            style="width: 100%"
            stripe
            border
          >
            <ElTableColumn
              prop="bookTitle"
              label="书籍名称"
              min-width="180"
              show-overflow-tooltip
            />
            <ElTableColumn prop="borrowerName" label="借阅人" min-width="120" />
            <ElTableColumn
              prop="borrowDate"
              label="借阅日期"
              min-width="120"
              :formatter="(row) => formatDate(row.borrowDate)"
            />
            <ElTableColumn
              prop="returnDate"
              label="应还日期"
              min-width="120"
              :formatter="(row) => formatDate(row.returnDate)"
            />
            <ElTableColumn
              prop="actualReturnDate"
              label="实际归还日期"
              min-width="130"
              :formatter="
                (row) => (row.actualReturnDate ? formatDate(row.actualReturnDate) : '未归还')
              "
            />
            <ElTableColumn label="借阅状态" min-width="100" :formatter="formatBorrowStatus">
              <template #default="{ row }">
                <ElTag :type="getStatusTagType(row)">
                  {{ formatBorrowStatus(row) }}
                </ElTag>
              </template>
            </ElTableColumn>
          </ElTable>

          <!-- 分页器 -->
          <div class="pagination-container" style="margin-top: 20px; text-align: right">
            <ElPagination
              v-model:current-page="borrowPagination.currentPage"
              v-model:page-size="borrowPagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="borrowPagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleBorrowSizeChange"
              @current-change="handleBorrowCurrentChange"
            />
          </div>

          <template #footer>
            <ElButton @click="borrowDialogVisible = false">关闭</ElButton>
          </template>
        </ElDialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { h } from 'vue'
  import { ROLE_LIST_DATA, ACCOUNT_TABLE_DATA } from '@/mock/temp/formData'
  import { BorrowService } from '@/api/borrowApi'
  import { ElDialog, ElTag, ElTable, ElTableColumn, ElPagination } from 'element-plus'
  import { ElMessage } from 'element-plus'
  //   import type { FormRules } from 'element-plus'
  import { useCheckedColumns } from '@/composables/useCheckedColumns'

  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import ArtTable from '@/components/core/tables/ArtTable.vue'
  import { UserService } from '@/api/usersApi'
  import { ApiStatus } from '@/utils/http/status'
  import { SearchChangeParams, SearchFormItem } from '@/types'

  defineOptions({ name: 'User' })

  //   const dialogType = ref('add')
  //   const dialogVisible = ref(false)
  const loading = ref(false)

  // 定义表单搜索初始值
  const initialSearchState = {
    name: '',
    phone: '',
    email: '',
    date: '',
    daterange: ''
  }

  // 借阅记录相关状态
  const borrowDialogVisible = ref(false)
  const borrowLoading = ref(false)
  const borrowRecords = ref<any[]>([])
  const borrowPagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
  })

  let currentViewUserId = ref<number | null>(null)
  let currentViewUserName = ref<string>('')

  // 格式化日期函数
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

  // 格式化借阅状态
  const formatBorrowStatus = (row: any) => {
    // 如果有实际归还日期，则已归还
    if (row.actualReturnDate) {
      return '已归还'
    }

    // 如果returnDate为null，表示正在借阅中
    if (row.returnDate === null || row.returnDate === undefined) {
      return '借阅中'
    }

    // 如果有returnDate，检查是否逾期
    try {
      const returnDate = new Date(row.returnDate)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      returnDate.setHours(0, 0, 0, 0)

      if (returnDate < today) {
        return '已逾期'
      } else {
        return '借阅中'
      }
    } catch {
      return '借阅中'
    }
  }

  // 获取状态标签类型
  const getStatusTagType = (row: any) => {
    const status = formatBorrowStatus(row)
    switch (status) {
      case '已归还':
        return 'success'
      case '已逾期':
        return 'danger'
      case '借阅中':
        return 'warning'
      default:
        return 'info'
    }
  }

  // 查看借阅记录
  const showBorrowRecordsDialog = async (row: any) => {
    console.log('查看借阅记录，用户信息:', row)

    // 确保获取正确的用户ID
    const userId = row.userId || row.id || row.user_id
    if (!userId) {
      ElMessage.error('无法获取用户ID')
      return
    }

    currentViewUserId.value = userId
    currentViewUserName.value = row.name || row.username || row.profile?.realName || '未知用户'

    console.log('设置当前查看用户ID:', currentViewUserId.value)
    console.log('设置当前查看用户名称:', currentViewUserName.value)

    borrowDialogVisible.value = true
    borrowLoading.value = true

    // 重置分页和记录
    borrowPagination.currentPage = 1
    borrowPagination.total = 0
    borrowRecords.value = []

    try {
      await loadBorrowRecords()
    } catch (error) {
      console.error('获取借阅记录失败:', error)
      ElMessage.error('获取借阅记录失败')
      borrowLoading.value = false
    }
  }

  // 加载借阅记录数据
  const loadBorrowRecords = async () => {
    if (!currentViewUserId.value) {
      ElMessage.error('用户ID不存在')
      borrowLoading.value = false
      return
    }

    borrowLoading.value = true
    try {
      const params = {
        userId: currentViewUserId.value.toString(),
        current: borrowPagination.currentPage,
        size: borrowPagination.pageSize
      }

      console.log('请求借阅记录参数:', params)
      console.log('当前查看用户ID:', currentViewUserId.value)

      const res = await BorrowService.getBorrowRecords(params)
      console.log('借阅记录API响应:', res)

      if (res.code === ApiStatus.success) {
        let records: any[] = []

        // 根据实际API响应结构处理数据
        if (res.data) {
          // 如果data是数组
          if (Array.isArray(res.data)) {
            records = res.data
          }
          // 如果data是对象，包含records字段
          else if (res.data.records && Array.isArray(res.data.records)) {
            records = res.data.records
            borrowPagination.total = res.data.total || 0
          }
          // 如果data是单个对象（单条记录）
          else if (typeof res.data === 'object' && res.data.records) {
            records = [res.data]
          }
          // 其他情况
          else if (typeof res.data === 'object') {
            records = [res.data]
          }
        }

        console.log('处理后的记录数据:', records)
        console.log('记录数量:', records.length)

        // 验证返回的记录是否属于当前用户
        const filteredRecords = records.filter((record) => {
          const recordUserId =
            record.userId || record.user_id || record.borrower?.userId || record.user?.userId
          console.log('记录中的用户ID:', recordUserId, '当前查看用户ID:', currentViewUserId.value)
          return recordUserId == currentViewUserId.value // 使用宽松比较以处理字符串/数字类型差异
        })

        console.log('过滤后的记录:', filteredRecords)

        // 数据预处理和扁平化
        borrowRecords.value = filteredRecords.map((record: any) => {
          console.log('单条记录结构:', record)

          return {
            id: record.recordId || record.id || record.borrowId,
            bookTitle: getBookTitle(record),
            borrowerName: getBorrowerName(record),
            borrowDate: record.borrowDate || record.createdAt,
            returnDate: record.returnDate || record.dueDate || record.expectedReturnDate,
            actualReturnDate: record.actualReturnDate || record.returnedAt || null,
            status: record.status,
            userId: record.userId || record.user_id, // 保存用户ID用于验证
            // 保留原始数据用于调试
            _raw: record
          }
        })

        // 设置总数
        if (res.data.total !== undefined) {
          borrowPagination.total = res.data.total
        } else {
          borrowPagination.total = filteredRecords.length
        }

        console.log('最终显示的借阅记录:', borrowRecords.value)
        console.log('总记录数:', borrowPagination.total)

        if (borrowRecords.value.length === 0) {
          console.log('该用户没有借阅记录')
        }
      } else {
        console.log('API返回错误:', res)
        ElMessage.error(res.message || '获取借阅记录失败')
        borrowRecords.value = []
        borrowPagination.total = 0
      }
    } catch (error) {
      console.error('加载借阅记录出错:', error)
      ElMessage.error('获取借阅记录失败，请重试')
      borrowRecords.value = []
      borrowPagination.total = 0
    } finally {
      borrowLoading.value = false
    }
  }

  // 提取书籍标题的辅助函数
  const getBookTitle = (record: any) => {
    // 根据实际API数据结构调整
    return (
      record.bookTitle ||
      record.bookId?.title || // 匹配实际API结构
      record.book?.title ||
      record.book?.name ||
      record.bookName ||
      '未知书籍'
    )
  }

  // 提取借阅人姓名的辅助函数
  const getBorrowerName = (record: any) => {
    // 根据实际API数据结构调整
    return (
      record.borrowerName ||
      record.user?.profile?.realName || // 匹配实际API结构
      record.borrower?.name ||
      record.user?.name ||
      record.userName ||
      currentViewUserName.value ||
      '未知用户'
    )
  }

  // 借阅记录分页处理
  const handleBorrowSizeChange = (size: number) => {
    borrowPagination.pageSize = size
    borrowPagination.currentPage = 1
    loadBorrowRecords()
  }

  const handleBorrowCurrentChange = (page: number) => {
    borrowPagination.currentPage = page
    loadBorrowRecords()
  }

  const roleList = ref<any[]>([])

  // 响应式表单数据
  const formFilters = reactive({ ...initialSearchState })

  const pagination = reactive({
    currentPage: 1,
    pageSize: 20,
    total: 0
  })

  // 表格数据
  const tableData = ref<any[]>([])

  // 表格实例引用
  const tableRef = ref()

  // 选中的行数据
  const selectedRows = ref<any[]>([])

  // 当前用户ID
  //   const currentUserId = ref<number | null>(null)

  // 重置表单
  const handleReset = () => {
    Object.assign(formFilters, { ...initialSearchState })
    pagination.currentPage = 1
    getUserList()
  }

  // 搜索处理
  const handleSearch = () => {
    console.log('搜索参数:', formFilters)
    pagination.currentPage = 1
    getUserList()
  }

  // 表单项变更处理
  const handleFormChange = (params: SearchChangeParams): void => {
    console.log('表单项变更:', params)
  }

  // 表单配置项
  const formItems: SearchFormItem[] = [
    {
      label: '用户名',
      prop: 'name',
      type: 'input',
      config: {
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: '电话',
      prop: 'phone',
      type: 'input',
      config: {
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      label: '邮箱',
      prop: 'email',
      type: 'input',
      config: {
        clearable: true
      },
      onChange: handleFormChange
    },
    {
      prop: 'date',
      label: '日期',
      type: 'date',
      config: {
        type: 'date',
        placeholder: '请选择日期'
      }
    },
    {
      prop: 'daterange',
      label: '日期范围',
      type: 'daterange',
      config: {
        type: 'daterange',
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间'
      }
    }
  ]

  // 动态列配置
  const { columns } = useCheckedColumns(() => [
    {
      prop: 'avatar',
      label: '用户信息',
      minWidth: 220,
      formatter: (row: any) => {
        type RoleType = 'R_SUPER' | 'R_ADMIN' | 'R_USER'

        const roleMap: Record<RoleType, string> = {
          R_SUPER: '超级管理员',
          R_ADMIN: '管理员',
          R_USER: '普通用户'
        }

        return h('div', { class: 'user', style: 'display: flex; align-items: center' }, [
          h('img', { class: 'avatar', src: row.avatar }),
          h('div', {}, [
            h('p', { class: 'user-name' }, [
              h('span', {}, row.name),
              h('span', { class: 'user-role' }, `（${roleMap[row.role as RoleType] || row.role}）`)
            ]),
            h('p', { class: 'email' }, row.userEmail)
          ])
        ])
      }
    },
    {
      prop: 'userGender',
      label: '性别',
      sortable: true,
      formatter: (row) => (row.userGender === '1' ? '男' : '女')
    },
    { prop: 'userPhone', label: '手机号' },
    {
      prop: 'username',
      label: '账号',
      formatter: (row) => row.username
    },
    {
      prop: 'createTime',
      label: '创建日期',
      sortable: true
    },
    {
      prop: 'operation',
      label: '操作',
      width: 150,
      formatter: (row: any) => {
        return h('div', [
          h(ArtButtonTable, {
            type: 'eye',
            text: '查看借阅记录',
            onClick: () => showBorrowRecordsDialog(row)
          })
        ])
      }
    }
  ])

  onMounted(() => {
    getUserList()
    getRoleList()
  })

  // 获取用户信息
  const getUserList = async () => {
    loading.value = true
    try {
      const params: {
        current: number
        size: number
        keyword?: string
        phone?: string
        email?: string
        date?: string
        startDate?: string
        endDate?: string
      } = {
        current: pagination.currentPage,
        size: pagination.pageSize
      }

      if (formFilters.name) {
        params.keyword = formFilters.name
      }
      if (formFilters.phone) {
        params.phone = formFilters.phone
      }
      if (formFilters.email) {
        params.email = formFilters.email
      }
      if (formFilters.date) {
        params.date = formFilters.date
      }
      if (formFilters.daterange && formFilters.daterange.length === 2) {
        params.startDate = formFilters.daterange[0]
        params.endDate = formFilters.daterange[1]
      }

      const res = await UserService.getUserList(params)
      console.log('用户列表:', res)
      if (res.code === ApiStatus.success) {
        const records = res.data.map((item: any, index: number) => {
          const avatarIndex = index % ACCOUNT_TABLE_DATA.length
          return {
            ...item,
            avatar: item.profile?.avatar || ACCOUNT_TABLE_DATA[avatarIndex].avatar,
            name: item.profile?.realName || item.username,
            userEmail: item.profile?.email,
            userPhone: item.profile?.phone,
            userGender: item.profile?.sex,
            nickname: item.profile?.nickname,
            school: item.profile?.school,
            description: item.profile?.description,
            createTime: item.createdAt,
            backgroundUrl: item.profile?.backgroundUrl
          }
        })
        tableData.value = records
        pagination.total = res.data.length
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  const getRoleList = () => {
    roleList.value = ROLE_LIST_DATA
  }

  // 处理表格行选择变化
  const handleSelectionChange = (selection: any[]) => {
    selectedRows.value = selection
  }

  // 处理表格分页变化
  const handleSizeChange = (newPageSize: number) => {
    pagination.pageSize = newPageSize
    getUserList()
  }

  const handleCurrentChange = (newCurrentPage: number) => {
    pagination.currentPage = newCurrentPage
    getUserList()
  }
</script>

<style lang="scss" scoped>
  .account-page {
    :deep(.user) {
      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 6px;
      }

      > div {
        margin-left: 10px;

        .user-name {
          font-weight: 500;
          color: var(--art-text-gray-800);

          .user-role {
            margin-left: 4px;
            font-weight: normal;
            color: var(--art-text-gray-600);
          }
        }

        .email {
          margin-top: 4px;
          font-size: 13px;
          color: var(--art-text-gray-600);
        }
      }
    }
  }

  .empty-state {
    padding: 40px 0;
    color: var(--el-text-color-secondary);
    text-align: center;
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
</style>
