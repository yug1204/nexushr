import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// Request interceptor — attach JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('nexushr_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor — handle 401
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('nexushr_token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ========== Auth ==========
export const authApi = {
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),
  register: (data: any) => api.post('/auth/register', data),
  refresh: (refreshToken: string) =>
    api.post('/auth/refresh', { refreshToken }),
  logout: () => api.post('/auth/logout'),
}

// ========== Employee ==========
export const employeeApi = {
  list: (page = 0, size = 20) =>
    api.get(`/employees?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/employees/${id}`),
  create: (data: any) => api.post('/employees', data),
  update: (id: string, data: any) => api.put(`/employees/${id}`, data),
  terminate: (id: string, reason: string) =>
    api.put(`/employees/${id}/terminate?reason=${reason}`),
  search: (query: string) => api.get(`/employees/search?q=${query}`),
  orgChart: (rootId: string) => api.get(`/employees/org-chart/${rootId}`),
}

// ========== Attendance ==========
export const attendanceApi = {
  clockIn: (employeeId: string) =>
    api.post(`/attendance/clock-in?employeeId=${employeeId}`),
  clockOut: (employeeId: string) =>
    api.post(`/attendance/clock-out?employeeId=${employeeId}`),
  getToday: () => api.get('/attendance/today'),
  getSummary: () => api.get('/attendance/summary'),
  getHistory: (empId: string, from: string, to: string) =>
    api.get(`/attendance/employee/${empId}?from=${from}&to=${to}`),
}

// ========== Leave ==========
export const leaveApi = {
  apply: (data: any) => api.post('/attendance/leave/apply', data),
  approve: (id: string, approverId: string) =>
    api.put(`/attendance/leave/${id}/approve?approverId=${approverId}`),
  reject: (id: string, approverId: string, remarks: string) =>
    api.put(`/attendance/leave/${id}/reject?approverId=${approverId}&remarks=${remarks}`),
  getPending: () => api.get('/attendance/leave/pending'),
  getBalance: (empId: string, year = 2026) =>
    api.get(`/attendance/leave/balance/${empId}?year=${year}`),
}

// ========== Payroll ==========
export const payrollApi = {
  initiateRun: (month: number, year: number) =>
    api.post(`/payroll/run?month=${month}&year=${year}`),
  getPayslips: (runId: string) => api.get(`/payroll/${runId}/payslips`),
  getPayslip: (empId: string, month: number, year: number) =>
    api.get(`/payroll/payslip/${empId}?month=${month}&year=${year}`),
}

// ========== Performance ==========
export const performanceApi = {
  createGoal: (data: any) => api.post('/performance/goals', data),
  getGoals: (empId: string) => api.get(`/performance/goals/employee/${empId}`),
  updateProgress: (goalId: string, progress: number) =>
    api.put(`/performance/goals/${goalId}/progress?progress=${progress}`),
  createReview: (data: any) => api.post('/performance/reviews', data),
  getReviews: (empId: string) => api.get(`/performance/reviews/employee/${empId}`),
  submitReview: (id: string) => api.put(`/performance/reviews/${id}/submit`),
  getCycles: () => api.get('/performance/cycles'),
}

// ========== AI Workforce Intelligence ==========
export const aiApi = {
  predictAttrition: (features: any) => api.post('/ai/predict/attrition', features),
  batchPredict: (featuresList: any[]) => api.post('/ai/predict/attrition/batch', featuresList),
  getDashboard: () => api.get('/ai/dashboard'),
  getHighRisk: () => api.get('/ai/attrition/high-risk'),
  getSkillGaps: (empId: string) => api.get(`/ai/skills/${empId}`),
  analyzeSkillGaps: (data: any) => api.post('/ai/skills/analyze', data),
  generateDemo: () => api.post('/ai/demo/generate'),
}

// ========== Recruitment & Onboarding ==========
export const recruitmentApi = {
  createRequisition: (data: any) => api.post('/recruitment/requisitions', data),
  getOpenRequisitions: () => api.get('/recruitment/requisitions/open'),
  approveRequisition: (id: string, approvedBy: string) => api.put(`/recruitment/requisitions/${id}/approve?approvedBy=${approvedBy}`),
  applyForJob: (reqId: string, data: any) => api.post(`/recruitment/candidates/apply/${reqId}`, data),
  advanceCandidate: (id: string, stage: string) => api.put(`/recruitment/candidates/${id}/advance?stage=${stage}`),
  getCandidates: (reqId: string) => api.get(`/recruitment/candidates/requisition/${reqId}`),
  getDashboard: () => api.get('/recruitment/dashboard'),
}

// ========== Reports & Analytics ==========
export const reportsApi = {
  headcount: () => api.get('/reports/headcount'),
  attrition: (period: string = 'MONTHLY') => api.get(`/reports/attrition?period=${period}`),
  payrollSummary: (month: number, year: number) => api.get(`/reports/payroll-summary?month=${month}&year=${year}`),
  leaveAnalytics: (year: number) => api.get(`/reports/leave-analytics?year=${year}`),
  diversity: () => api.get('/reports/diversity'),
  compensation: () => api.get('/reports/compensation-benchmarking'),
  executive: () => api.get('/reports/executive-dashboard'),
}

// ========== Compliance & GDPR ==========
export const complianceApi = {
  getAuditTrail: (entityType: string, entityId: string) => api.get(`/compliance/audit/${entityType}/${entityId}`),
  verifyChain: () => api.get('/compliance/audit/verify'),
  gdprAccess: (empId: string) => api.get(`/compliance/gdpr/access/${empId}`),
  gdprErasure: (empId: string) => api.post(`/compliance/gdpr/erasure/${empId}`),
  gdprPortability: (empId: string) => api.get(`/compliance/gdpr/portability/${empId}`),
}

export default api
