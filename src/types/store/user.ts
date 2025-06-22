export interface UserProfile {
  profileId: number
  realName: string
  nickname: string
  email: string
  phone: string
  school: string
  sex: string
  description: string | null
  avatar: string
  backgroundUrl: string
  createdAt: string
  updatedAt: string
}

export interface UserInfo {
  userId: number
  username: string
  password: string
  role: string
  createdAt: string
  updatedAt: string
  profile: UserProfile
}
