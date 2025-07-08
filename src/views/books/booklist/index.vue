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
      <ElCol :lg="6" :md="6" :sm="10" :xs="6" style="display: flex; justify-content: end">
        <ElButton type="primary" @click="showAddDialog">添加图书</ElButton>
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
      <!-- 添加图书详情列 -->
      <ElTableColumn label="详情" width="80">
        <template #default="scope">
          <ArtButtonTable type="eye" @click="handleDetail(scope.row)"></ArtButtonTable>
        </template>
      </ElTableColumn>
      <ElTableColumn fixed="right" label="操作" width="150">
        <template #default="scope">
          <ArtButtonTable type="edit" @click="handleEdit(scope.row)"></ArtButtonTable>
          <ArtButtonTable type="delete" @click="handleDelete(scope.row)"></ArtButtonTable>
        </template>
      </ElTableColumn>
    </ElTable>

    <!-- 分页 -->
    <div style="display: flex; justify-content: center; margin-top: 20px">
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
            <img
              v-if="formData.cover || formData.cover"
              :src="formData.cover || formData.cover"
              class="detail-cover"
              alt="封面"
            />
            <span v-else>无封面图片</span>
          </div>
        </div>
      </div>
      <div class="dialog-footer">
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </div>
    </ElDialog>

    <!-- 添加/编辑对话框 -->
    <ElDialog
      :title="dialogType === 'add' ? '添加图书' : '编辑图书'"
      v-model="dialogVisible"
      width="500px"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        style="max-height: 60vh; overflow-y: auto"
      >
        <ElFormItem label="书名" prop="title">
          <ElInput v-model="formData.title" />
        </ElFormItem>
        <ElFormItem label="作者" prop="author">
          <ElInput v-model="formData.author" />
        </ElFormItem>
        <ElFormItem label="出版社" prop="publisher">
          <ElInput v-model="formData.publisher" />
        </ElFormItem>
        <ElFormItem label="ISBN" prop="isbn">
          <ElInput v-model="formData.isbn" />
        </ElFormItem>
        <ElFormItem label="分类" prop="category">
          <ElSelect v-model="formData.category" placeholder="选择分类">
            <ElOption v-for="item in categories" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="描述" prop="description">
          <ElInput v-model="formData.description" type="textarea" :rows="3" />
        </ElFormItem>
        <ElFormItem label="封面" prop="cover">
          <ElUpload
            class="avatar-uploader"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img
              v-if="formData.cover || formData.cover"
              :src="formData.cover || formData.cover"
              class="avatar"
              alt="封面"
            />
            <ElIcon v-else class="avatar-uploader-icon"><Plus /></ElIcon>
          </ElUpload>
          <!-- 新增URL输入框 -->
          <ElInput v-model="formData.cover" placeholder="或输入图片URL" style="margin-top: 10px" />
        </ElFormItem>
      </ElForm>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { Search, Plus } from '@element-plus/icons-vue'
  import { BookService, type Book } from '@/api/bookApi'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { FormInstance } from 'element-plus'
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

  // 表单相关
  const dialogVisible = ref(false)
  const detailDialogVisible = ref(false) // 添加这行声明详情对话框可见性变量
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref<FormInstance>()
  const formData = ref<Partial<Book>>({})

  // 书籍详情方法
  const handleDetail = (row: Book) => {
    formData.value = { ...row }
    detailDialogVisible.value = true
  }
  // 新增URL变化处理
  // const handleUrlChange = () => {
  //   console.log('用户修改了 cover URL:', formData.value.cover)
  // }

  // 修改上传成功处理
  const handleAvatarSuccess = (response: any) => {
    if (response.code === 200) {
      formData.value.cover = response.data.url
    } else {
      ElMessage.error('上传失败')
    }
  }

  // 修改编辑处理
  const handleEdit = (row: Book) => {
    dialogType.value = 'edit'
    formData.value = {
      ...row,
      cover: row.cover || '' // 初始化URL字段
    }
    dialogVisible.value = true
  }

  // 修改表单提交
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          const bookData = {
            ...formData.value
          }
          console.log('提交给后端的 bookData:', bookData)
          if (dialogType.value === 'add') {
            const res = await BookService.addBook(bookData)
            if (res.code === 200) {
              ElMessage.success('添加成功')
              getBookList()
            }
          } else {
            const res = await BookService.updateBook(formData.value.bookId!, bookData)
            if (res.code === 200) {
              ElMessage.success('更新成功')
              getBookList()
            }
          }
          dialogVisible.value = false
        } catch (error) {
          console.error('操作失败:', error)
        }
      }
    })
  }

  // 获取图书列表
  const getBookList = async () => {
    loading.value = true
    try {
      const params = {
        current: currentPage.value,
        size: pageSize.value,
        title: searchVal.value, // 按书名查询
        category: categoryVal.value // 按分类查询
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

  // 表单提交
  // const handleSubmit = async () => {
  //   if (!formRef.value) return

  //   await formRef.value.validate(async (valid) => {
  //     if (valid) {
  //       try {
  //         if (dialogType.value === 'add') {
  //           const res = await BookService.addBook(formData.value)
  //           if (res.code === 200) {
  //             ElMessage.success('添加成功')
  //             getBookList()
  //           }
  //         } else {
  //           const res = await BookService.updateBook(formData.value.bookId!, formData.value)
  //           if (res.code === 200) {
  //             ElMessage.success('更新成功')
  //             getBookList()
  //           }
  //         }
  //         dialogVisible.value = false
  //       } catch (error) {
  //         console.error('操作失败:', error)
  //       }
  //     }
  //   })
  // }

  // 显示添加对话框
  const showAddDialog = () => {
    dialogType.value = 'add'
    formData.value = {}
    dialogVisible.value = true
  }

  const rules = ref<Record<string, any>>({
    title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
    author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
    publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
    isbn: [
      { required: true, message: '请输入ISBN', trigger: 'blur' },
      { pattern: /^[0-9-]{10,17}$/, message: 'ISBN格式不正确', trigger: 'blur' }
    ],
    category: [{ required: true, message: '请选择分类', trigger: 'change' }]
  })

  // 显示编辑对话框
  // const handleEdit = (row: Book) => {
  //   dialogType.value = 'edit'
  //   formData.value = { ...row }
  //   dialogVisible.value = true
  // }

  // 删除图书
  const handleDelete = async (row: Book) => {
    try {
      await ElMessageBox.confirm('确认删除该图书吗？', '提示', {
        type: 'warning'
      })
      const res = await BookService.deleteBook(row.bookId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getBookList()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  }

  onMounted(() => {
    getBookList()
  })
</script>

<style lang="scss" scoped>
  .book-list {
    padding: 20px;

    .avatar-uploader {
      :deep(.el-upload) {
        position: relative;
        overflow: hidden;
        cursor: pointer;
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        transition: var(--el-transition-duration-fast);

        &:hover {
          border-color: var(--el-color-primary);
        }
      }
    }

    .avatar-uploader-icon {
      width: 178px;
      height: 178px;
      font-size: 28px;
      line-height: 178px;
      color: #8c939d;
      text-align: center;
    }

    .avatar {
      display: block;
      width: 178px;
      height: 178px;
    }
  }

  .dialog-footer {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
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
    padding-right: 10px;
    font-weight: bold;
    color: #606266;
    text-align: right;
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
