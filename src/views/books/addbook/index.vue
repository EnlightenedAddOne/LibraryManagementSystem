<template>
  <div class="page-content add-book">
    <h2 class="page-title">添加图书</h2>

    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="120px" class="book-form">
      <!-- 左侧表单 -->
      <div class="form-container">
        <div class="left-form">
          <ElFormItem label="书名" prop="title">
            <ElInput v-model="formData.title" placeholder="请输入书名" />
          </ElFormItem>

          <ElFormItem label="作者" prop="author">
            <ElInput v-model="formData.author" placeholder="请输入作者" />
          </ElFormItem>

          <ElFormItem label="出版社" prop="publisher">
            <ElInput v-model="formData.publisher" placeholder="请输入出版社" />
          </ElFormItem>

          <ElFormItem label="ISBN" prop="isbn">
            <ElInput v-model="formData.isbn" placeholder="请输入ISBN" />
          </ElFormItem>

          <ElFormItem label="分类" prop="category">
            <ElSelect v-model="formData.category" placeholder="请选择分类">
              <ElOption v-for="item in categories" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>

          <ElFormItem label="出版日期" prop="publishDate">
            <ElDatePicker
              v-model="formData.publishDate"
              type="date"
              placeholder="选择出版日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </ElFormItem>

          <ElFormItem label="状态" prop="status">
            <ElSelect v-model="formData.status" placeholder="请选择状态">
              <ElOption :value="0" label="可借阅" />
              <ElOption :value="1" label="已借出" />
            </ElSelect>
          </ElFormItem>
        </div>

        <!-- 右侧封面上传 -->
        <div class="right-form">
          <ElFormItem label="图书封面" prop="cover">
            <div class="upload-container">
              <!-- 现有上传组件 -->
              <ElUpload
                class="cover-uploader"
                :action="uploadUrl"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :before-upload="beforeCoverUpload"
              >
                <img
                  v-if="formData.cover || formData.cover"
                  :src="formData.cover || formData.cover"
                  class="cover"
                />
                <div v-else class="upload-placeholder">
                  <ElIcon><Plus /></ElIcon>
                  <div class="upload-text">点击上传封面</div>
                </div>
              </ElUpload>
              <div class="upload-tip">建议尺寸: 300x400px, 支持 jpg/png 格式</div>

              <!-- 新增URL输入框 -->
              <ElInput
                v-model="formData.cover"
                placeholder="或输入图片URL"
                style="margin-top: 10px"
                @change="handleUrlChange"
              />
            </div>
          </ElFormItem>

          <ElFormItem label="图书描述" prop="description">
            <ElInput
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入图书描述"
            />
          </ElFormItem>
        </div>
      </div>

      <!-- 表单按钮 -->
      <div class="form-buttons">
        <ElButton @click="goBack">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">保存</ElButton>
      </div>
    </ElForm>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import type { FormInstance, UploadProps } from 'element-plus'
  import { BookService, type Book } from '@/api/bookApi'

  const router = useRouter()
  const formRef = ref<FormInstance>()
  const categories = ['文学', '科技', '历史', '艺术', '经济', '教育', '其他']
  const uploadUrl = '/api/upload' // 上传接口地址

  // 表单数据
  const formData = ref<Partial<Book>>({
    title: '',
    author: '',
    publisher: '',
    isbn: '',
    category: '',
    publishDate: '',
    description: '',
    cover: '',
    status: 0
  })

  // 新增URL变化处理
  const handleUrlChange = () => {
    if (formData.value.cover) {
      // 如果输入了URL，清空上传的封面
      formData.value.cover = ''
    }
  }

  // 修改上传成功处理
  const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
    if (response.code === 200) {
      formData.value.cover = response.data.url
      formData.value.cover = '' // 清空URL
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error('封面上传失败')
    }
  }

  // 修改表单提交
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          // 合并封面信息
          const bookData = {
            ...formData.value,
            cover: formData.value.cover || formData.value.cover
          }
          // 删除临时字段
          delete bookData.cover

          const res = await BookService.addBook(bookData)
          if (res.code === 200) {
            ElMessage.success('添加成功')
            goBack()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        } catch (error) {
          console.error('添加图书失败:', error)
          ElMessage.error('添加失败，请重试')
        }
      }
    })
  }

  // 上传前校验
  const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => {
    const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPGOrPNG) {
      ElMessage.error('封面图片只能是 JPG 或 PNG 格式!')
    }
    if (!isLt2M) {
      ElMessage.error('封面图片大小不能超过 2MB!')
    }
    return isJPGOrPNG && isLt2M
  }

  // 添加表单验证规则定义
  const rules = {
    title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
    author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
    publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
    isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
    category: [{ required: true, message: '请选择分类', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }

  // 返回列表页
  const goBack = () => {
    router.back()
  }
</script>

<style lang="scss" scoped>
  .add-book {
    padding: 20px;

    .page-title {
      margin-bottom: 30px;
      font-size: 24px;
      font-weight: 500;
    }

    .book-form {
      max-width: 1200px;
      margin: 0 auto;

      .form-container {
        display: flex;
        gap: 40px;

        .left-form {
          flex: 1;
        }

        .right-form {
          width: 400px;
        }
      }
    }

    .upload-container {
      text-align: center;

      .cover-uploader {
        :deep(.el-upload) {
          width: 300px;
          height: 400px;
          overflow: hidden;
          cursor: pointer;
          border: 1px dashed var(--el-border-color);
          border-radius: 6px;

          &:hover {
            border-color: var(--el-color-primary);
          }
        }

        .cover {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .upload-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 100%;

          .el-icon {
            font-size: 48px;
            color: #8c939d;
          }

          .upload-text {
            margin-top: 16px;
            color: #8c939d;
          }
        }
      }

      .upload-tip {
        margin-top: 8px;
        font-size: 12px;
        color: #606266;
      }
    }

    .form-buttons {
      margin-top: 40px;
      text-align: center;

      .el-button {
        min-width: 120px;
        margin: 0 10px;
      }
    }
  }

  // 响应式布局
  @media (width <= 1200px) {
    .add-book {
      .book-form {
        .form-container {
          flex-direction: column;

          .right-form {
            width: 100%;
          }
        }
      }
    }
  }
</style>
