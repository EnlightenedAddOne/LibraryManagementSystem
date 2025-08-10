<!-- LibraryNotices.vue -->
<template>
  <div class="card art-custom-card library-notices-card">
    <div class="card-header">
      <div class="title">
        <h3 class="box-title">图书馆公告</h3>
      </div>
      <el-button
        v-if="canEdit"
        class="edit-btn"
        type="primary"
        size="small"
        link
        @click="openEdit()"
        >编辑</el-button
      >
    </div>

    <div class="notice-wrapper" v-loading="loading">
      <h4 class="notice-title">{{ displayTitle }}</h4>
      <p class="notice-content" v-if="displayContent">{{ displayContent }}</p>
      <p class="notice-empty" v-else> </p>
    </div>

    <el-dialog v-model="dialogVisible" title="编辑公告" width="420px" :close-on-click-modal="false">
      <el-form :model="form" label-width="56px">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="40" show-word-limit />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :disabled="saving || deleting">取消</el-button>
          <el-button
            v-if="notice"
            type="danger"
            :loading="deleting"
            :disabled="saving"
            @click="onDelete"
            >删除</el-button
          >
          <el-button type="primary" :loading="saving" :disabled="deleting" @click="saveNotice"
            >保存</el-button
          >
        </div>
      </template>
    </el-dialog>

    <!-- 装饰图片 -->
    <div class="image-container">
      <img src="@/assets/img/home_card_bg/ll.png" alt="ll" class="notice-bg-image" loading="lazy" />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from 'vue'
  import { useUserStore } from '@/store/modules/user'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    getNotices, // 新增：获取全部公告
    // getNoticeById, // 若后续需要单条可保留
    createNotice,
    updateNotice,
    deleteNotice,
    type Notice
  } from '@/api/LibraryNoticeApi'

  // 默认文本
  const DEFAULT_TITLE = '暂无公告'
  const DEFAULT_CONTENT = ''

  const userStore = useUserStore()
  const role = userStore.info?.role || ''
  const canEdit = computed(() => role === 'R_ADMIN' || role === 'R_SUPER')

  // 只操作“第一条”公告
  const notice = ref<Notice | null>(null)
  const loading = ref(false)
  const saving = ref(false)
  const deleting = ref(false)

  const displayTitle = computed(() => notice.value?.title || DEFAULT_TITLE)
  const displayContent = computed(() => notice.value?.content || DEFAULT_CONTENT)

  const dialogVisible = ref(false)
  const form = reactive({
    title: '',
    content: ''
  })

  // 获取所有公告，仅保留第一条
  const fetchNotices = async () => {
    loading.value = true
    try {
      const res = await getNotices()
      if (res.code === 200 && Array.isArray(res.data) && res.data.length > 0) {
        notice.value = res.data[0] // 第一条
      } else {
        notice.value = null
      }
    } finally {
      loading.value = false
    }
  }

  const openEdit = () => {
    if (!canEdit.value) return
    form.title = notice.value?.title || ''
    form.content = notice.value?.content || ''
    dialogVisible.value = true
  }

  // 保存：有第一条则更新，没有则创建新公告
  const saveNotice = async () => {
    if (saving.value || deleting.value) return
    if (!form.title.trim() && !form.content.trim()) {
      ElMessage.warning('请输入内容')
      return
    }
    if (!form.title.trim()) form.title = DEFAULT_TITLE
    saving.value = true
    const payload = { title: form.title.trim(), content: form.content.trim() }
    try {
      if (notice.value) {
        const upd = await updateNotice(notice.value.id, payload)
        if (upd.code === 200 && upd.data) ElMessage.success('更新成功')
        else ElMessage.error('更新失败')
      } else {
        const crt = await createNotice(payload)
        if (crt.code === 200 && crt.data) ElMessage.success('创建成功')
        else ElMessage.error('创建失败')
      }
      await fetchNotices()
      dialogVisible.value = false
    } catch {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  }

  // 删除：删除当前第一条
  const onDelete = async () => {
    if (!notice.value) return
    try {
      await ElMessageBox.confirm('确定删除该公告吗？删除后将显示“暂无公告”。', '提示', {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消'
      })
    } catch {
      return
    }
    deleting.value = true
    try {
      const delRes = await deleteNotice(notice.value.id)
      if (delRes.code === 200) {
        ElMessage.success('已删除')
        notice.value = null
        dialogVisible.value = false
      } else {
        ElMessage.error('删除失败')
      }
      await fetchNotices()
    } catch {
      ElMessage.error('删除失败')
    } finally {
      deleting.value = false
    }
  }

  onMounted(fetchNotices)
</script>

<style lang="scss" scoped>
  /* 与“图书分类统计”统一：标题结构一致（.card-header > .title > .box-title + .subtitle） */
  .library-notices-card {
    position: relative;
    min-height: 180px;
    overflow: hidden;
  }

  .card-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    padding: 8px 12px 0;

    .title {
      display: flex;
      flex-direction: column;
    }

    .box-title {
      margin: 0 0 4px;
      font-size: 16px;
      font-weight: 600;
      line-height: 1.2;
      color: var(--el-text-color-primary);
    }

    .subtitle {
      margin: 0;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }

    .edit-btn {
      margin-top: -2px;
      font-size: 13px;
    }
  }

  .notice-wrapper {
    min-height: 120px;
    padding: 8px 14px 18px;
  }

  .notice-title {
    margin: -35px 0 10px;
    font-size: 15px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    text-align: center;
  }

  .notice-content {
    margin: 0;
    font-size: 14px;
    line-height: 1.55;
    color: var(--el-text-color-regular);
    text-indent: 2em;
    white-space: pre-line;
  }

  .notice-empty {
    font-size: 13px;
    color: var(--el-text-color-secondary);
    text-align: center;
  }

  /* 可选：让 footer 横向间距更合理 */
  .dialog-footer {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
  }

  .image-container {
    position: absolute;
    right: 0;
    bottom: 0;
    z-index: 1;
    display: flex;
    align-items: flex-end;
    justify-content: flex-end;
    width: 38%;
    height: 100%;
    pointer-events: none;
    opacity: 0.5;
  }

  .notice-bg-image {
    display: block;
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
</style>
