import { create } from 'zustand'

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
  login: (user: User, token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: {
    id: 'demo-user',
    email: 'admin@nexushr.com',
    firstName: 'Admin',
    lastName: 'User',
    roles: ['ROLE_HR_ADMIN'],
  },
  accessToken: 'demo-token',
  isAuthenticated: true,
  login: (user, token) => set({ user, accessToken: token, isAuthenticated: true }),
  logout: () => set({ user: null, accessToken: null, isAuthenticated: false }),
}))
