import { useState } from 'react'
import { Search, Plus, Filter, Download, MoreVertical, Mail, Phone, MapPin } from 'lucide-react'

const employees = [
  { id: 1, code: 'NHR-001001', name: 'Priya Sharma', email: 'priya.sharma@nexushr.com', phone: '+91-9876543210', dept: 'Engineering', designation: 'Senior Java Developer', grade: 'L5', status: 'ACTIVE', hireDate: '2023-03-15', ctc: '₹18,00,000', avatar: 'PS' },
  { id: 2, code: 'NHR-001002', name: 'Rahul Verma', email: 'rahul.verma@nexushr.com', phone: '+91-9876543211', dept: 'Sales', designation: 'Regional Sales Manager', grade: 'L4', status: 'ACTIVE', hireDate: '2022-07-01', ctc: '₹14,00,000', avatar: 'RV' },
  { id: 3, code: 'NHR-001003', name: 'Anita Desai', email: 'anita.desai@nexushr.com', phone: '+91-9876543212', dept: 'Marketing', designation: 'Marketing Lead', grade: 'L4', status: 'ACTIVE', hireDate: '2024-01-10', ctc: '₹12,50,000', avatar: 'AD' },
  { id: 4, code: 'NHR-001004', name: 'Vikram Singh', email: 'vikram.singh@nexushr.com', phone: '+91-9876543213', dept: 'Operations', designation: 'Operations Analyst', grade: 'L3', status: 'ON_NOTICE', hireDate: '2021-11-20', ctc: '₹10,00,000', avatar: 'VS' },
  { id: 5, code: 'NHR-001005', name: 'Meera Patel', email: 'meera.patel@nexushr.com', phone: '+91-9876543214', dept: 'Finance', designation: 'Senior Accountant', grade: 'L4', status: 'ACTIVE', hireDate: '2020-06-15', ctc: '₹13,00,000', avatar: 'MP' },
  { id: 6, code: 'NHR-001006', name: 'Arjun Nair', email: 'arjun.nair@nexushr.com', phone: '+91-9876543215', dept: 'Engineering', designation: 'Full Stack Developer', grade: 'L4', status: 'ACTIVE', hireDate: '2023-09-01', ctc: '₹16,00,000', avatar: 'AN' },
  { id: 7, code: 'NHR-001007', name: 'Sneha Gupta', email: 'sneha.gupta@nexushr.com', phone: '+91-9876543216', dept: 'HR', designation: 'HR Business Partner', grade: 'L4', status: 'ON_LEAVE', hireDate: '2022-04-12', ctc: '₹11,50,000', avatar: 'SG' },
  { id: 8, code: 'NHR-001008', name: 'Karthik Iyer', email: 'karthik.iyer@nexushr.com', phone: '+91-9876543217', dept: 'Engineering', designation: 'DevOps Engineer', grade: 'L5', status: 'ACTIVE', hireDate: '2021-08-23', ctc: '₹19,00,000', avatar: 'KI' },
]

const avatarColors = ['#6366f1', '#06b6d4', '#8b5cf6', '#10b981', '#f59e0b', '#ec4899', '#3b82f6', '#ef4444']

export default function Employees() {
  const [searchQuery, setSearchQuery] = useState('')
  const [showForm, setShowForm] = useState(false)

  const filtered = employees.filter(e =>
    e.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    e.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
    e.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
    e.dept.toLowerCase().includes(searchQuery.toLowerCase())
  )

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
          <button className="btn btn-secondary btn-sm"><Download size={14} /> Export</button>
          <button className="btn btn-primary btn-sm" onClick={() => setShowForm(!showForm)}>
            <Plus size={14} /> Add Employee
          </button>
        </div>
      </div>

      {/* Search & Filter Bar */}
      <div className="card animate-in" style={{ marginBottom: '20px', padding: '16px 20px' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <div className="header-search" style={{ flex: 1 }}>
            <Search className="search-icon" size={14} />
            <input
              type="text"
              placeholder="Search by name, email, code, department..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              style={{ width: '100%' }}
            />
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
              { label: 'First Name', placeholder: 'Enter first name' },
              { label: 'Last Name', placeholder: 'Enter last name' },
              { label: 'Email', placeholder: 'email@company.com' },
              { label: 'Phone', placeholder: '+91-XXXXXXXXXX' },
              { label: 'Department', placeholder: 'Select department' },
              { label: 'Designation', placeholder: 'Job title' },
              { label: 'Grade', placeholder: 'L1-L8' },
              { label: 'Hire Date', placeholder: 'YYYY-MM-DD' },
              { label: 'CTC (Annual)', placeholder: '₹ Amount' },
            ].map(f => (
              <div className="form-group" key={f.label} style={{ marginBottom: '0' }}>
                <label className="form-label">{f.label}</label>
                <input className="form-input" placeholder={f.placeholder} />
              </div>
            ))}
          </div>
          <div style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
            <button className="btn btn-secondary btn-sm" onClick={() => setShowForm(false)}>Cancel</button>
            <button className="btn btn-primary btn-sm">Save Employee</button>
          </div>
        </div>
      )}

      {/* Employee Table */}
      <div className="card animate-in-delay-1">
        <div className="card-header">
          <div>
            <div className="card-title">All Employees ({filtered.length})</div>
            <div className="card-subtitle">Complete employee directory with lifecycle status</div>
          </div>
        </div>
        <div style={{ overflowX: 'auto' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Code</th>
                <th>Department</th>
                <th>Designation</th>
                <th>Grade</th>
                <th>CTC</th>
                <th>Status</th>
                <th>Hire Date</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((emp, i) => (
                <tr key={emp.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                      <div style={{
                        width: '36px', height: '36px', borderRadius: 'var(--radius-full)',
                        background: avatarColors[i % avatarColors.length],
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '12px', fontWeight: 700, color: 'white', flexShrink: 0,
                      }}>
                        {emp.avatar}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '13px' }}>{emp.name}</div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{emp.email}</div>
                      </div>
                    </div>
                  </td>
                  <td><span style={{ fontFamily: 'monospace', fontSize: '12px' }}>{emp.code}</span></td>
                  <td>{emp.dept}</td>
                  <td>{emp.designation}</td>
                  <td><span className="badge badge-purple">{emp.grade}</span></td>
                  <td style={{ fontWeight: 600 }}>{emp.ctc}</td>
                  <td>
                    <span className={`badge ${
                      emp.status === 'ACTIVE' ? 'badge-success' :
                      emp.status === 'ON_NOTICE' ? 'badge-warning' :
                      emp.status === 'ON_LEAVE' ? 'badge-info' :
                      'badge-danger'
                    }`}>
                      {emp.status.replace('_', ' ')}
                    </span>
                  </td>
                  <td style={{ color: 'var(--text-muted)' }}>{emp.hireDate}</td>
                  <td>
                    <button className="icon-btn" style={{ width: '28px', height: '28px', border: 'none' }}>
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
