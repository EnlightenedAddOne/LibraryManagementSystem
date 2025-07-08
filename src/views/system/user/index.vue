<template>
  <ArtTableFullScreen>
    <div class="account-page" id="table-full-screen">
      <!-- 搜索栏 -->
      <ArtSearchBar
        v-model:filter="formFilters"
        :items="formItems"
        @reset="handleReset"
        @search="handleSearch"
      ></ArtSearchBar>

      <ElCard shadow="never" class="art-table-card">
        <!-- 表格头部 -->
        <ArtTableHeader
          :columnList="columnOptions"
          v-model:columns="columnChecks"
          @refresh="handleRefresh"
        >
          <template #left>
            <ElButton @click="showDialog('add')">新增用户</ElButton>
          </template>
        </ArtTableHeader>

        <!-- 表格 -->
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

        <ElDialog
          v-model="dialogVisible"
          :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
          width="30%"
          align-center
        >
          <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
            <ElFormItem label="账号" prop="username">
              <ElInput v-model="formData.username" placeholder="请输入账号" />
            </ElFormItem>

            <ElFormItem label="密码" prop="password">
              <ElInput
                v-model="formData.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </ElFormItem>

            <ElFormItem label="真实姓名" prop="profile.realName">
              <ElInput v-model="formData.profile.realName" placeholder="请输入真实姓名" />
            </ElFormItem>

            <ElFormItem label="角色" prop="role">
              <ElSelect v-model="formData.role" placeholder="请选择角色">
                <ElOption
                  v-for="role in roleList"
                  :key="role.roleCode"
                  :value="role.roleCode"
                  :label="role.roleName"
                />
              </ElSelect>
            </ElFormItem>
          </ElForm>
          <template #footer>
            <div class="dialog-footer">
              <ElButton @click="dialogVisible = false">取消</ElButton>
              <ElButton type="primary" @click="handleSubmit">提交</ElButton>
            </div>
          </template>
        </ElDialog>
      </ElCard>
    </div>
  </ArtTableFullScreen>
</template>

<script setup lang="ts">
  import { h } from 'vue'
  import { ROLE_LIST_DATA, ACCOUNT_TABLE_DATA } from '@/mock/temp/formData'

  import { ElDialog, FormInstance } from 'element-plus'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import type { FormRules } from 'element-plus'
  import { useCheckedColumns } from '@/composables/useCheckedColumns'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { UserService } from '@/api/usersApi'
  import { ApiStatus } from '@/utils/http/status'
  import { SearchChangeParams, SearchFormItem } from '@/types'

  defineOptions({ name: 'User' }) // 定义组件名称，用于 KeepAlive 缓存控制

  const dialogType = ref('add')
  const dialogVisible = ref(false)
  const loading = ref(false)

  // 定义表单搜索初始值
  const initialSearchState = {
    name: '',
    phone: '',
    // address: '',
    // level: '',
    email: '',
    date: '',
    daterange: ''
    // status: '1'
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
  const currentUserId = ref<number | null>(null)

  // 重置表单
  const handleReset = () => {
    Object.assign(formFilters, { ...initialSearchState })
    pagination.currentPage = 1 // 重置到第一页
    getUserList()
  }

  // 搜索处理
  const handleSearch = () => {
    console.log('搜索参数:', formFilters)
    pagination.currentPage = 1 // 搜索时重置到第一页
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
    // {
    //   label: '地址',
    //   prop: 'address',
    //   type: 'input',
    //   config: {
    //     clearable: true
    //   },
    //   onChange: handleFormChange
    // },
    {
      label: '邮箱',
      prop: 'email',
      type: 'input',
      config: {
        clearable: true
      },
      onChange: handleFormChange
    },
    // 支持 9 种日期类型定义
    // 具体可参考 src/components/core/forms/art-search-bar/widget/art-search-date/README.md
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
    // {
    //   label: '状态',
    //   prop: 'status',
    //   type: 'radio',
    //   options: [
    //     { label: '在线', value: '1' },
    //     { label: '离线', value: '2' }
    //   ],
    //   onChange: handleFormChange
    // }
  ]

  // 列配置
  const columnOptions = [
    // { label: '勾选', type: 'selection' },
    { label: '用户名', prop: 'name' },
    { label: '手机号', prop: 'phone' },
    { label: '性别', prop: 'gender' },
    { label: '角色', prop: 'role' },
    // { label: '状态', prop: 'status' },
    { label: '账号', prop: 'username' },
    { label: '创建日期', prop: 'createTime' },
    { label: '操作', prop: 'operation' }
  ]

  // 获取标签类型
  // 1: 在线 2: 离线 3: 异常 4: 注销
  // const getTagType = (status: string) => {
  //   switch (status) {
  //     case '1':
  //       return 'success'
  //     case '2':
  //       return 'info'
  //     case '3':
  //       return 'warning'
  //     case '4':
  //       return 'danger'
  //     default:
  //       return 'info'
  //   }
  // }

  // // 构建标签文本
  // const buildTagText = (status: string) => {
  //   let text = ''
  //   if (status === '1') {
  //     text = '在线'
  //   } else if (status === '2') {
  //     text = '离线'
  //   } else if (status === '3') {
  //     text = '异常'
  //   } else if (status === '4') {
  //     text = '注销'
  //   }
  //   return text
  // }

  // 显示对话框
  const showDialog = (type: string, row?: any) => {
    dialogVisible.value = true
    dialogType.value = type

    // 重置表单验证状态
    if (formRef.value) {
      formRef.value.resetFields()
    }

    // 重置表单数据
    formData.username = ''
    formData.password = ''
    formData.role = ''
    formData.profile = {
      realName: '',
      nickname: '',
      email: '',
      phone: '',
      school: '',
      sex: '',
      description: '',
      avatar: '',
      backgroundUrl: ''
    }

    if (type === 'edit' && row) {
      // 保存当前编辑的用户ID
      currentUserId.value = row.userId
      // 填充表单数据
      formData.username = row.username
      formData.role = row.userRoles?.[0] || row.role // 获取角色
      formData.profile.realName = row.profile?.realName || row.name
      // 编辑时不显示密码
      formData.password = ''
    }
  }

  // 删除用户
  const deleteUser = async (userId: number) => {
    try {
      await ElMessageBox.confirm('确定要注销该用户吗？', '注销用户', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      loading.value = true
      const res = await UserService.deleteUser(userId)

      if (res.code === ApiStatus.success) {
        ElMessage.success('注销成功')
        // 刷新用户列表
        await getUserList()
      } else {
        ElMessage.error(res.message || '注销失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('注销用户失败:', error)
        ElMessage.error('注销失败，请重试')
      }
    } finally {
      loading.value = false
    }
  }

  // 动态列配置
  const { columnChecks, columns } = useCheckedColumns(() => [
    {
      prop: 'avatar',
      label: '用户信息',
      minWidth: 220,
      formatter: (row: any) => {
        // 角色映射
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
      formatter: (row) => row.username // 直接显示用户名
    },
    // {
    //   prop: 'status',
    //   label: '状态',
    //   formatter: (row) => {
    //     return h(ElTag, { type: getTagType(row.status) }, () => buildTagText(row.status))
    //   }
    // },
    {
      prop: 'createTime',
      label: '创建日期',
      sortable: true
    },
    {
      prop: 'operation',
      label: '操作',
      width: 150,
      // fixed: 'right', // 固定在右侧
      // disabled: true,
      formatter: (row: any) => {
        return h('div', [
          h(ArtButtonTable, {
            type: 'edit',
            onClick: () => showDialog('edit', row)
          }),
          h(ArtButtonTable, {
            type: 'delete',
            onClick: () => deleteUser(row.userId)
          })
        ])
      }
    }
  ])

  // 表单实例
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive({
    username: '',
    password: '',
    role: '',
    profile: {
      realName: '',
      nickname: '',
      email: '',
      phone: '',
      school: '',
      sex: '',
      description: '',
      avatar: '',
      backgroundUrl: ''
    }
  })

  onMounted(() => {
    getUserList()
    getRoleList()
  })

  // 获取用户信息
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

      // 根据搜索过滤器添加参数
      if (formFilters.name) {
        params.keyword = formFilters.name // 用户名映射为 keyword
      }
      if (formFilters.phone) {
        params.phone = formFilters.phone // 电话映射为 phone
      }
      if (formFilters.email) {
        params.email = formFilters.email // 邮箱映射为 email
      }
      if (formFilters.date) {
        // 假设 formFilters.date 已经是后端期望的 'YYYY-MM-DDTHH:mm:ss' 格式
        params.date = formFilters.date // 单个日期映射为 date
      }
      if (formFilters.daterange && formFilters.daterange.length === 2) {
        // 假设 daterange 返回的是两个日期字符串，需要转换为后端期望的格式
        params.startDate = formFilters.daterange[0] // 日期范围开始
        params.endDate = formFilters.daterange[1] // 日期范围结束
      }

      const res = await UserService.getUserList(params)
      console.log('用户列表:', res) // 打印用户列表到控制台
      if (res.code === ApiStatus.success) {
        // 使用本地头像替换接口返回的头像
        console.log('成功获取用户列表:', res.data)
        const records = res.data.map((item: any, index: number) => {
          const avatarIndex = index % ACCOUNT_TABLE_DATA.length
          return {
            ...item,
            avatar: item.profile?.avatar || ACCOUNT_TABLE_DATA[avatarIndex].avatar,
            // 从 profile 中提取信息
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
        // 更新分页总数。由于后端返回的数据格式中没有总数，这里暂时使用当前返回数据的长度
        // 如果后端在实际中会返回总数，您需要根据实际字段进行调整
        pagination.total = res.data.length
        loading.value = false
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
      loading.value = false
    }
  }

  const getRoleList = () => {
    roleList.value = ROLE_LIST_DATA
  }

  const handleRefresh = () => {
    getUserList()
  }

  // 处理表格行选择变化
  const handleSelectionChange = (selection: any[]) => {
    selectedRows.value = selection
  }

  // 表单验证规则
  const rules = reactive<FormRules>({
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
      { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    'profile.realName': [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    role: [{ required: true, message: '请选择角色', trigger: 'change' }]
  })

  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          loading.value = true
          const data = {
            username: formData.username,
            password: formData.password,
            role: Array.isArray(formData.role) ? formData.role[0] : formData.role,
            profile: {
              realName: formData.profile.realName,
              nickname: formData.profile.nickname,
              email: formData.profile.email,
              phone: formData.profile.phone,
              school: formData.profile.school,
              sex: formData.profile.sex,
              description: formData.profile.description,
              avatar: formData.profile.avatar,
              backgroundUrl: formData.profile.backgroundUrl
            }
          }

          if (dialogType.value === 'add') {
            const res = await UserService.addUser(data)
            if (res.code === ApiStatus.success) {
              ElMessage.success('添加用户成功')
              dialogVisible.value = false
              getUserList() // 刷新用户列表
            } else {
              ElMessage.error(res.message || '添加用户失败')
            }
          } else if (dialogType.value === 'edit' && currentUserId.value) {
            const res = await UserService.updateUserInfo(currentUserId.value, data)
            if (res.code === ApiStatus.success) {
              ElMessage.success('更新用户成功')
              dialogVisible.value = false
              getUserList()
            } else {
              ElMessage.error(res.message || '更新用户失败')
            }
          }
        } catch (error) {
          console.error('操作失败:', error)
          ElMessage.error('操作失败，请重试')
        } finally {
          loading.value = false
        }
      }
    })
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
</style>
