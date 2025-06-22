import request from '@/utils/http'
import { BaseResponse } from '@/types/api'

interface LoginParams {
  username: string
  password: string
}

interface UserListParams {
  current?: number
  size?: number
}

interface AddUserParams {
  username: string
  password: string
  role: string
  profile: {
    realName: string
  }
}

// 将 UpdateUserParams 接口添加到类型定义中
export interface UpdateUserParams {
  username?: string
  password?: string
  role?: string
  profile: {
    realName: string
    nickname: string
    email: string
    phone: string
    school: string
    sex: string
    backgroundUrl: string
    description: string | null
    avatar: string
  }
}

export class UserService {
  // 登录
  static login(params: LoginParams) {
    return request.post<BaseResponse>({
      url: '/users/login',
      data: params // 使用 `data` 而不是 `params`，因为登录通常是 POST 请求的请求体
    })
  }

  // 获取用户信息
  static getUserInfo() {
    return request.get<BaseResponse>({
      url: '/users/info'
    })
  }

  // 获取用户列表
  static getUserList(params?: UserListParams) {
    return request.get<BaseResponse>({
      url: '/users',
      params
    })
  }

  // 删除用户
  static deleteUser(userId: number) {
    return request.del<BaseResponse>({
      url: `/users/${userId}`
    })
  }

  // 添加用户
  static addUser(data: AddUserParams) {
    return request.post<BaseResponse>({
      url: '/users',
      data
    })
  }

  // 修改用户信息
  static updateUserInfo(userId: number, data: UpdateUserParams) {
    return request.put<BaseResponse>({
      url: `/users/${userId}`,
      data
    })
  }
}
