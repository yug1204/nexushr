import { create } from 'zustand'
import { authApi } from '../api/client'

interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  roles: string[]
}

interface AuthState {
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  loginAction: (email: string, password: string) => Promise<void>
  login: (user: User, token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  isAuthenticated: false,
  isLoading: false,

  loginAction: async (email: string, password: string) => {
    set({ isLoading: true })
    try {
      const res = await authApi.login(email, password)
      const { user, accessToken } = res.data.data || res.data
      localStorage.setItem('nexushr_token', accessToken)
      set({ user, accessToken, isAuthenticated: true, isLoading: false })
    } catch (err: any) {
      set({ isLoading: false })
      throw err
    }
  },

  login: (user, token) => {
    localStorage.setItem('nexushr_token', token)
    set({ user, accessToken: token, isAuthenticated: true })
  },

  logout: () => {
    localStorage.removeItem('nexushr_token')
    set({ user: null, accessToken: null, isAuthenticated: false })
  },
}))
