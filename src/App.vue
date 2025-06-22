<template>
  <ElConfigProvider size="default" :locale="locales[language]" :z-index="3000">
    <RouterView></RouterView>
  </ElConfigProvider>
</template>

<script setup lang="ts">
  import { useUserStore } from './store/modules/user'
  import zh from 'element-plus/es/locale/lang/zh-cn'
  import en from 'element-plus/es/locale/lang/en'
  import { systemUpgrade } from './utils/sys'
  import { UserService } from './api/usersApi'
  import { ApiStatus } from './utils/http/status'
  import { setThemeTransitionClass } from './utils/theme/animation'
  import { checkStorageCompatibility } from './utils/storage'

  const userStore = useUserStore()
  const { language } = storeToRefs(userStore)

  const locales = {
    zh: zh,
    en: en
  }

  onBeforeMount(() => {
    setThemeTransitionClass(true)
  })

  onMounted(() => {
    // 检查存储兼容性
    checkStorageCompatibility()
    // 提升暗黑主题下页面刷新视觉体验
    setThemeTransitionClass(false)
    // 系统升级
    systemUpgrade()
    // 获取用户信息
    getUserInfo()
    // 测试获取用户列表
    // fetchAndLogUserList()
  })

  // 获取用户信息
  const getUserInfo = async () => {
    if (userStore.isLogin) {
      const res = await UserService.getUserInfo()
      if (res.code === ApiStatus.success) {
        userStore.setUserInfo(res.data)
      }
    }
  }

  // // 测试获取用户列表并打印到控制台
  // const fetchAndLogUserList = async () => {
  //   try {
  //     const res = await UserService.getUserList() // 调用获取用户列表的 API
  //     console.log('用户列表:', res) // 打印用户列表到控制台
  //   } catch (error) {
  //     console.error('请求用户列表时发生错误:', error)
  //   }
  // }
</script>
