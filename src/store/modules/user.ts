import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { LanguageEnum } from '@/enums/appEnum'
import { router } from '@/router'
import type { UserInfo } from '@/types/store/user'
import { UserService, type UpdateUserParams } from '@/api/usersApi'
import { useSettingStore } from './setting'
import { useWorktabStore } from './worktab'
import { AppRouteRecord } from '@/types/router'
import { setPageTitle } from '@/router/utils/utils'
import { resetRouterState } from '@/router/guards/beforeEach'
import { RoutesAlias } from '@/router/routesAlias'

// 用户
export const useUserStore = defineStore(
  'userStore',
  () => {
    const language = ref(LanguageEnum.ZH)
    const isLogin = ref(false)
    const isLock = ref(false)
    const lockPassword = ref('')
    const info = ref<UserInfo>({} as UserInfo)
    const searchHistory = ref<AppRouteRecord[]>([])
    const accessToken = ref('')
    const refreshToken = ref('')

    const initialUserInfo: UserInfo = {
      userId: 0,
      username: '',
      password: '',
      role: '',
      createdAt: '',
      updatedAt: '',
      profile: {
        profileId: 0,
        realName: '',
        nickname: '',
        email: '',
        phone: '',
        school: '',
        sex: '',
        description: null,
        avatar: '',
        backgroundUrl: '',
        createdAt: '',
        updatedAt: ''
      }
    }

    const getUserInfo = computed(() => info.value)
    const getSettingState = computed(() => useSettingStore().$state)
    const getWorktabState = computed(() => useWorktabStore().$state)

    const setUserInfo = (newInfo: UserInfo) => {
      info.value = newInfo
    }

    const setLoginStatus = (status: boolean) => {
      isLogin.value = status
    }

    const setLanguage = (lang: LanguageEnum) => {
      setPageTitle(router.currentRoute.value)
      language.value = lang
    }

    const setSearchHistory = (list: AppRouteRecord[]) => {
      searchHistory.value = list
    }

    const setLockStatus = (status: boolean) => {
      isLock.value = status
    }

    const setLockPassword = (password: string) => {
      lockPassword.value = password
    }

    const setToken = (newAccessToken: string, newRefreshToken?: string) => {
      accessToken.value = newAccessToken
      if (newRefreshToken) {
        refreshToken.value = newRefreshToken
      }
    }

    const logOut = () => {
      info.value = { ...initialUserInfo }
      isLogin.value = false
      isLock.value = false
      lockPassword.value = ''
      accessToken.value = ''
      refreshToken.value = ''
      useWorktabStore().opened = []
      sessionStorage.removeItem('iframeRoutes')
      resetRouterState(router)
      router.push(RoutesAlias.Login)
    }

    // 添加获取用户信息的方法
    const fetchUserInfo = async () => {
      try {
        const res = await UserService.getUserInfo()
        if (res.code === 200) {
          info.value = res.data
        }
        return res
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    }

    // 修改更新用户信息方法
    const updateUserInfo = async ({ userId, data }: { userId: number; data: UpdateUserParams }) => {
      try {
        const res = await UserService.updateUserInfo(userId, data)
        if (res.code === 200) {
          // 更新成功后重新获取用户信息
          await fetchUserInfo()
        }
        return res
      } catch (error) {
        console.error('更新用户信息失败:', error)
        throw error
      }
    }

    return {
      language,
      isLogin,
      isLock,
      lockPassword,
      info,
      searchHistory,
      accessToken,
      refreshToken,
      getUserInfo,
      getSettingState,
      getWorktabState,
      setUserInfo,
      setLoginStatus,
      setLanguage,
      setSearchHistory,
      setLockStatus,
      setLockPassword,
      setToken,
      logOut,
      fetchUserInfo, // 将新方法添加到返回对象
      updateUserInfo
    }
  },
  {
    persist: {
      key: 'user',
      storage: localStorage
    }
  }
)
