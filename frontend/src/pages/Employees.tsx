import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { employeeApi } from '../api/client'
import { Search, Plus, Filter, Download, MoreVertical } from 'lucide-react'
import toast from 'react-hot-toast'

const avatarColors = ['#6366f1', '#06b6d4', '#8b5cf6', '#10b981', '#f59e0b', '#ec4899', '#3b82f6', '#ef4444']

interface EmployeeForm {
  firstName: string; lastName: string; email: string; phone: string
  department: string; jobTitle: string; grade: string; hireDate: string; baseSalary: string
}

const emptyForm: EmployeeForm = {
  firstName: '', lastName: '', email: '', phone: '',
  department: '', jobTitle: '', grade: '', hireDate: '', baseSalary: '',
}

export default function Employees() {
  const [searchQuery, setSearchQuery] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState<EmployeeForm>(emptyForm)
  const queryClient = useQueryClient()

  const { data: employeePage, isLoading } = useQuery({
    queryKey: ['employees'],
    queryFn: async () => {
      const res = await employeeApi.list(0, 100)
      return res.data.data
    },
    retry: 1,
  })

  const createMutation = useMutation({
    mutationFn: (data: any) => employeeApi.create(data),
    onSuccess: () => {
      toast.success('Employee onboarded successfully')
      queryClient.invalidateQueries({ queryKey: ['employees'] })
      setShowForm(false)
      setForm(emptyForm)
    },
    onError: () => toast.error('Failed to create employee'),
  })

  const terminateMutation = useMutation({
    mutationFn: (id: string) => employeeApi.terminate(id, 'Voluntary resignation'),
    onSuccess: () => {
      toast.success('Employee terminated')
      queryClient.invalidateQueries({ queryKey: ['employees'] })
    },
    onError: () => toast.error('Failed to terminate employee'),
  })

  const employees = employeePage?.content || []

  const filtered = employees.filter((e: any) => {
    const q = searchQuery.toLowerCase()
    const fullName = `${e.firstName || ''} ${e.lastName || ''}`.toLowerCase()
    return fullName.includes(q) || e.email?.toLowerCase().includes(q) ||
      e.employeeCode?.toLowerCase().includes(q) || e.department?.toLowerCase().includes(q)
  })

  const handleSave = () => {
    if (!form.firstName || !form.lastName || !form.email) {
      toast.error('First name, last name, and email are required')
      return
    }
    createMutation.mutate({
      firstName: form.firstName, lastName: form.lastName, email: form.email,
      phone: form.phone, department: form.department, jobTitle: form.jobTitle,
      grade: form.grade, hireDate: form.hireDate || new Date().toISOString().split('T')[0],
      baseSalary: parseFloat(form.baseSalary) || 0,
    })
  }

  const handleExport = () => {
    const csv = ['Name,Email,Department,Grade,Status']
    filtered.forEach((e: any) => {
      csv.push(`${e.firstName} ${e.lastName},${e.email},${e.department},${e.grade},${e.lifecycleStatus}`)
    })
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url; a.download = 'employees.csv'; a.click()
    toast.success('Exported to CSV')
  }

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }} className="animate-in">
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 800 }}>Employee Management</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
            Manage employee lifecycle from onboarding to offboarding
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="btn btn-secondary btn-sm" onClick={handleExport}><Download size={14} /> Export</button>
          <button className="btn btn-primary btn-sm" onClick={() => setShowForm(!showForm)}>
            <Plus size={14} /> Add Employee
          </button>
        </div>
      </div>

      {/* Search */}
      <div className="card animate-in" style={{ marginBottom: '20px', padding: '16px 20px' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <div className="header-search" style={{ flex: 1 }}>
            <Search className="search-icon" size={14} />
            <input type="text" placeholder="Search by name, email, code, department..."
              value={searchQuery} onChange={e => setSearchQuery(e.target.value)} style={{ width: '100%' }} />
          </div>
          <button className="btn btn-secondary btn-sm"><Filter size={14} /> Filters</button>
        </div>
      </div>

      {/* Add Employee Form */}
      {showForm && (
        <div className="card animate-in" style={{ marginBottom: '20px' }}>
          <div className="card-header">
            <div className="card-title">New Employee Onboarding</div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px' }}>
            {[
              { key: 'firstName', label: 'First Name', placeholder: 'Enter first name' },
              { key: 'lastName', label: 'Last Name', placeholder: 'Enter last name' },
              { key: 'email', label: 'Email', placeholder: 'email@company.com' },
              { key: 'phone', label: 'Phone', placeholder: '+91-XXXXXXXXXX' },
              { key: 'department', label: 'Department', placeholder: 'Engineering, Sales...' },
              { key: 'jobTitle', label: 'Designation', placeholder: 'Job title' },
              { key: 'grade', label: 'Grade', placeholder: 'L1-L8' },
              { key: 'hireDate', label: 'Hire Date', placeholder: 'YYYY-MM-DD' },
              { key: 'baseSalary', label: 'CTC (Annual)', placeholder: '₹ Amount' },
            ].map(f => (
              <div className="form-group" key={f.key} style={{ marginBottom: '0' }}>
                <label className="form-label">{f.label}</label>
                <input className="form-input" placeholder={f.placeholder}
                  value={(form as any)[f.key]} onChange={e => setForm({ ...form, [f.key]: e.target.value })} />
              </div>
            ))}
          </div>
          <div style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
            <button className="btn btn-secondary btn-sm" onClick={() => { setShowForm(false); setForm(emptyForm) }}>Cancel</button>
            <button className="btn btn-primary btn-sm" onClick={handleSave}
              disabled={createMutation.isPending}>
              {createMutation.isPending ? 'Saving...' : 'Save Employee'}
            </button>
          </div>
        </div>
      )}

      {/* Employee Table */}
      <div className="card animate-in-delay-1">
        <div className="card-header">
          <div>
            <div className="card-title">All Employees ({filtered.length})</div>
            <div className="card-subtitle">{isLoading ? 'Loading from database...' : 'Complete employee directory with lifecycle status'}</div>
          </div>
        </div>
        <div style={{ overflowX: 'auto' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee</th><th>Code</th><th>Department</th><th>Designation</th>
                <th>Grade</th><th>CTC</th><th>Status</th><th>Hire Date</th><th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 && (
                <tr><td colSpan={9} style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                  {isLoading ? 'Loading employees...' : 'No employees found. Add one to get started.'}
                </td></tr>
              )}
              {filtered.map((emp: any, i: number) => (
                <tr key={emp.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div style={{
                        width: '36px', height: '36px', borderRadius: '9999px',
                        background: avatarColors[i % avatarColors.length],
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '12px', fontWeight: 700, color: 'white', flexShrink: 0,
                      }}>
                        {(emp.firstName?.[0] || '') + (emp.lastName?.[0] || '')}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '13px' }}>{emp.firstName} {emp.lastName}</div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{emp.email}</div>
                      </div>
                    </div>
                  </td>
                  <td><span style={{ fontFamily: 'monospace', fontSize: '12px' }}>{emp.employeeCode}</span></td>
                  <td>{emp.department}</td>
                  <td>{emp.jobTitle}</td>
                  <td><span className="badge badge-purple">{emp.grade}</span></td>
                  <td style={{ fontWeight: 600 }}>₹{Number(emp.baseSalary || 0).toLocaleString('en-IN')}</td>
                  <td>
                    <span className={`badge ${
                      emp.lifecycleStatus === 'ACTIVE' ? 'badge-success' :
                      emp.lifecycleStatus === 'ON_NOTICE' ? 'badge-warning' :
                      emp.lifecycleStatus === 'TERMINATED' ? 'badge-danger' :
                      'badge-info'
                    }`}>
                      {(emp.lifecycleStatus || 'UNKNOWN').replace('_', ' ')}
                    </span>
                  </td>
                  <td style={{ color: 'var(--text-muted)' }}>{emp.hireDate}</td>
                  <td>
                    <button className="icon-btn" style={{ width: '28px', height: '28px', border: 'none' }}
                      onClick={() => { if (confirm(`Terminate ${emp.firstName}?`)) terminateMutation.mutate(emp.id) }}>
                      <MoreVertical size={14} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
