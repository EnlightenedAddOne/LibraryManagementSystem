<template>
  <div class="page-content book-list">
    <!-- 搜索栏 -->
    <ElRow justify="space-between" :gutter="10">
      <ElCol :lg="6" :md="6" :sm="14" :xs="16">
        <ElInput
          v-model="searchVal"
          :prefix-icon="Search"
          clearable
          placeholder="输入书名或作者查询"
          @keyup.enter="searchBook"
        />
      </ElCol>
      <ElCol :lg="12" :md="12" :sm="0" :xs="0">
        <div class="custom-segmented">
          <ElSelect v-model="categoryVal" placeholder="选择分类" clearable @change="searchBook">
            <ElOption v-for="item in categories" :key="item" :label="item" :value="item" />
          </ElSelect>
        </div>
      </ElCol>
    </ElRow>

    <!-- 图书列表 -->
    <ElTable :data="bookList" style="width: 100%; margin-top: 20px" v-loading="loading">
      <ElTableColumn prop="title" label="书名" min-width="180" />
      <ElTableColumn prop="author" label="作者" width="120" />
      <ElTableColumn prop="publisher" label="出版社" width="180" />
      <ElTableColumn prop="category" label="分类" width="100" />
      <ElTableColumn prop="status" label="状态" width="100">
        <template #default="scope">
          <ElTag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '可借阅' : '已借出' }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="createTime" label="创建时间" width="180" />
      <!-- 图书详情列 -->
      <ElTableColumn label="详情" width="80">
        <template #default="scope">
          <ArtButtonTable type="eye" @click="handleDetail(scope.row)"></ArtButtonTable>
        </template>
      </ElTableColumn>
      <ElTableColumn fixed="right" label="操作" width="150">
        <template #default="scope">
          <ArtButtonTable
            style="background-color: #eef3ff; border-color: #ffffff; color: #329ff5"
            type="success"
            :disabled="scope.row.status !== 1"
            @click="handleBorrow(scope.row)"
            v-if="scope.row.status === 1"
            text="借阅"
          >
            借阅
          </ArtButtonTable>
          <ElTag v-else type="info">已借出</ElTag>
        </template>
      </ElTableColumn>
    </ElTable>

    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: center">
      <ElPagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 详情对话框 -->
    <ElDialog title="图书详情" v-model="detailDialogVisible" width="600px">
      <div class="book-detail">
        <div class="detail-row">
          <span class="detail-label">书名:</span>
          <span class="detail-value">{{ formData.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">作者:</span>
          <span class="detail-value">{{ formData.author }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">出版社:</span>
          <span class="detail-value">{{ formData.publisher }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">ISBN:</span>
          <span class="detail-value">{{ formData.isbn }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">分类:</span>
          <span class="detail-value">{{ formData.category }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态:</span>
          <span class="detail-value">
            <ElTag :type="formData.status === 1 ? 'success' : 'danger'">{{
              formData.status === 1 ? '可借阅' : '已借出'
            }}</ElTag>
          </span>
        </div>
        <div class="detail-row">
          <span class="detail-label">创建时间:</span>
          <span class="detail-value">{{ formData.createTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">描述:</span>
          <span class="detail-value">{{ formData.description || '无' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">封面:</span>
          <div class="cover-container">
            <img v-if="formData.cover" :src="formData.cover" class="detail-cover" alt="封面" />
            <span v-else>无封面图片</span>
          </div>
        </div>
      </div>
      <div class="dialog-footer">
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </div>
    </ElDialog>

    <!-- 借阅对话框 -->
    <ElDialog title="图书借阅" v-model="borrowDialogVisible" width="400px">
      <ElForm ref="borrowFormRef" :model="borrowForm" :rules="borrowRules" label-width="100px">
        <ElFormItem label="图书名称" prop="bookTitle">
          <ElInput v-model="borrowForm.bookTitle" disabled />
        </ElFormItem>

        <ElFormItem label="借阅日期" prop="borrowDate">
          <ElDatePicker
            v-model="borrowForm.borrowDate"
            type="date"
            placeholder="选择借阅日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="(date: Date) => date < new Date()"
          />
        </ElFormItem>
        <ElFormItem label="归还日期" prop="dueDate">
          <ElDatePicker
            v-model="borrowForm.dueDate"
            type="date"
            placeholder="选择归还日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </ElFormItem>
      </ElForm>
      <div class="dialog-footer">
        <ElButton @click="borrowDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitBorrow">确认借阅</ElButton>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue'
  import { Search } from '@element-plus/icons-vue'
  import { BookService, type Book } from '@/api/bookApi'
  import { BorrowService } from '@/api/borrowApi'
  import { useUserStore } from '@/store/modules/user'
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'

  defineOptions({ name: 'BookList' })

  const categories = ['文学', '科技', '历史', '艺术', '经济', '教育', '其他']
  const searchVal = ref('')
  const categoryVal = ref('')
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const loading = ref(false)
  const bookList = ref<Book[]>([])

  const userStore = useUserStore()

  // 详情对话框相关
  const detailDialogVisible = ref(false)
  const formData = ref<Partial<Book>>({})

  // 书籍详情方法
  const handleDetail = (row: Book) => {
    formData.value = { ...row }
    detailDialogVisible.value = true
  }

  // 获取图书列表
  const getBookList = async () => {
    loading.value = true
    try {
      const params = {
        current: currentPage.value,
        size: pageSize.value,
        title: searchVal.value,
        category: categoryVal.value
      }
      const res = await BookService.getBookList(params)
      console.log('获取图书列表:', res)
      if (res.code === 200) {
        bookList.value = res.data.records
        total.value = res.data.total
      }
    } catch (error) {
      console.error('获取图书列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 搜索图书
  const searchBook = () => {
    currentPage.value = 1
    getBookList()
  }

  // 处理分页
  const handleSizeChange = (val: number) => {
    pageSize.value = val
    getBookList()
  }

  const handleCurrentChange = (val: number) => {
    currentPage.value = val
    getBookList()
  }

  // 借阅相关状态
  const borrowDialogVisible = ref(false)
  const borrowFormRef = ref<FormInstance>()
  const borrowForm = reactive({
    bookId: 0,
    bookTitle: '',
    userId: userStore.info.userId,
    borrowDate: new Date().toISOString().split('T')[0],
    dueDate: ''
  })

  // 借阅表单验证规则 - 修复验证器类型错误
  const borrowRules: FormRules = {
    borrowDate: [{ required: true, message: '请选择借阅日期', trigger: 'change' }],
    dueDate: [
      {
        validator: (_: any, value: string, callback: (error?: Error) => void) => {
          if (!value) {
            callback(new Error('请选择归还日期'))
            return
          }
          if (new Date(value) <= new Date(borrowForm.borrowDate)) {
            callback(new Error('归还日期必须晚于借阅日期'))
            return
          }
          callback()
        },
        trigger: 'change'
      }
    ]
  }

  // 借阅处理函数 - 修复图书ID获取问题
  const handleBorrow = (row: Book) => {
    console.log('借阅图书数据:', row) // 添加调试日志

    // 确保使用正确的字段名
    borrowForm.bookId = row.bookId
    borrowForm.bookTitle = row.title
    borrowForm.borrowDate = new Date().toISOString().split('T')[0]

    // 默认设置30天后归还
    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 30)
    borrowForm.dueDate = dueDate.toISOString().split('T')[0]

    console.log('借阅表单数据:', borrowForm) // 添加调试日志
    console.log('bookId', borrowForm.bookId)
    borrowDialogVisible.value = true
  }

  // 提交借阅
  const submitBorrow = async () => {
    if (!borrowFormRef.value) return

    await borrowFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          // 使用嵌套对象格式，与后端Entity匹配
          const borrowData = {
            bookId: {
              bookId: borrowForm.bookId
            },
            user: {
              userId: borrowForm.userId
            },
            borrowDate: borrowForm.borrowDate,
            dueDate: borrowForm.dueDate
          }

          console.log('提交借阅数据:', borrowData)
          const res = await BorrowService.createBorrowRecord(borrowData)

          if (res.code === 200) {
            ElMessage.success('借阅成功')
            borrowDialogVisible.value = false
            getBookList() // 刷新图书列表
          } else {
            ElMessage.error(res.message || '借阅失败')
          }
        } catch (error: any) {
          console.error('借阅失败:', error)
          const errorMessage = error.response?.data?.message || error.message || '借阅失败，请重试'
          ElMessage.error(errorMessage)
        }
      }
    })
  }

  onMounted(() => {
    getBookList()
  })
</script>

<style lang="scss" scoped>
  .book-list {
    padding: 20px;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }

  .book-detail {
    padding: 10px 0;
  }

  .detail-row {
    display: flex;
    margin-bottom: 16px;
    line-height: 24px;
  }

  .detail-label {
    width: 100px;
    text-align: right;
    padding-right: 10px;
    font-weight: bold;
    color: #606266;
  }

  .detail-value {
    flex: 1;
    word-break: break-word;
  }

  .cover-container {
    flex: 1;
  }

  .detail-cover {
    width: 120px;
    height: 160px;
    object-fit: cover;
    border-radius: 4px;
  }
</style>
