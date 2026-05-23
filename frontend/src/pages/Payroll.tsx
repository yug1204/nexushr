import { useState } from 'react'
import { Wallet, FileText, Download, CheckCircle, PlayCircle, DollarSign, TrendingUp } from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, PieChart, Pie, Cell } from 'recharts'

const payrollRuns = [
  { id: 'PR-2026-04', period: 'April 2026', status: 'FINALIZED', employees: 5183, gross: '₹46.3L', net: '₹37.2L', deductions: '₹9.1L', date: '2026-04-28' },
  { id: 'PR-2026-03', period: 'March 2026', status: 'FINALIZED', employees: 5150, gross: '₹45.1L', net: '₹36.4L', deductions: '₹8.7L', date: '2026-03-28' },
  { id: 'PR-2026-02', period: 'February 2026', status: 'FINALIZED', employees: 5117, gross: '₹44.2L', net: '₹35.6L', deductions: '₹8.6L', date: '2026-02-27' },
  { id: 'PR-2026-05', period: 'May 2026', status: 'DRAFT', employees: 5183, gross: '-', net: '-', deductions: '-', date: '-' },
]

const salaryBreakdown = [
  { component: 'Basic Salary', amount: 18.5, color: '#6366f1' },
  { component: 'HRA', amount: 7.4, color: '#06b6d4' },
  { component: 'Special Allowance', amount: 5.2, color: '#8b5cf6' },
  { component: 'Transport', amount: 1.6, color: '#10b981' },
  { component: 'PF (Employer)', amount: 2.2, color: '#f59e0b' },
  { component: 'Others', amount: 2.4, color: '#ec4899' },
]

const deductionBreakdown = [
  { type: 'PF (Employee)', amount: '₹2.2L', pct: '12%' },
  { type: 'TDS', amount: '₹4.1L', pct: 'Slab-based' },
  { type: 'ESI (Employee)', amount: '₹0.8L', pct: '0.75%' },
  { type: 'Professional Tax', amount: '₹1.0L', pct: '₹200/m' },
  { type: 'Other Deductions', amount: '₹1.0L', pct: 'Variable' },
]

const payslipSample = {
  empName: 'Priya Sharma', empCode: 'NHR-001001', designation: 'Senior Java Developer',
  department: 'Engineering', period: 'April 2026',
  earnings: [
    { component: 'Basic Salary', amount: '₹75,000' },
    { component: 'House Rent Allowance', amount: '₹30,000' },
    { component: 'Special Allowance', amount: '₹20,000' },
    { component: 'Transport Allowance', amount: '₹1,600' },
  ],
  deductions: [
    { component: 'PF (Employee 12%)', amount: '₹1,800' },
    { component: 'TDS', amount: '₹12,500' },
    { component: 'Professional Tax', amount: '₹200' },
  ],
  grossSalary: '₹1,26,600',
  totalDeductions: '₹14,500',
  netSalary: '₹1,12,100',
}

const tooltipStyle = {
  backgroundColor: '#fff', border: '1px solid #e5e7eb',
  borderRadius: '8px', color: '#111827', fontSize: '12px',
  boxShadow: '0 4px 12px rgba(0,0,0,.08)',
}

export default function Payroll() {
  const [showPayslip, setShowPayslip] = useState(false)

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }} className="animate-in">
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 800 }}>Payroll Management</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
            Gross-to-net payroll processing with Indian tax compliance
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="btn btn-secondary btn-sm"><Download size={14} /> Export Journal</button>
          <button className="btn btn-primary btn-sm"><PlayCircle size={14} /> Run Payroll</button>
        </div>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon purple"><DollarSign size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Total Gross (Apr)</div>
            <div className="stat-value">₹46.3L</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-1">
          <div className="stat-icon green"><Wallet size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Total Net Pay</div>
            <div className="stat-value">₹37.2L</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-2">
          <div className="stat-icon amber"><FileText size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Total Deductions</div>
            <div className="stat-value">₹9.1L</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-3">
          <div className="stat-icon blue"><CheckCircle size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Employees Processed</div>
            <div className="stat-value">5,183</div>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Salary Component Distribution</div>
              <div className="card-subtitle">Breakdown of total payroll cost (₹ Lakhs)</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={salaryBreakdown} cx="50%" cy="50%" innerRadius={60} outerRadius={90}
                   dataKey="amount" nameKey="component" stroke="none" paddingAngle={3}>
                {salaryBreakdown.map((e, i) => <Cell key={i} fill={e.color} />)}
              </Pie>
              <Tooltip contentStyle={tooltipStyle} />
              <Legend iconType="circle" iconSize={8} wrapperStyle={{ fontSize: '11px', color: '#94a3b8' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Deduction Summary</div>
              <div className="card-subtitle">Statutory and voluntary deductions</div>
            </div>
          </div>
          <div style={{ padding: '8px 0' }}>
            {deductionBreakdown.map((d, i) => (
              <div key={i} style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '14px 16px', borderBottom: i < deductionBreakdown.length - 1 ? '1px solid var(--border)' : 'none',
              }}>
                <div>
                  <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '13px' }}>{d.type}</div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Rate: {d.pct}</div>
                </div>
                <div style={{ fontWeight: 700, color: '#d97706', fontSize: '14px' }}>{d.amount}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Payroll Runs Table */}
      <div className="card animate-in" style={{ marginBottom: '20px' }}>
        <div className="card-header">
          <div>
            <div className="card-title">Payroll Runs</div>
            <div className="card-subtitle">Monthly payroll processing history</div>
          </div>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Run ID</th><th>Period</th><th>Employees</th><th>Gross</th>
              <th>Net</th><th>Deductions</th><th>Status</th><th>Date</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {payrollRuns.map((r, i) => (
              <tr key={i}>
                <td style={{ fontFamily: 'monospace', fontSize: '12px', fontWeight: 600, color: 'var(--text-primary)' }}>{r.id}</td>
                <td style={{ fontWeight: 500 }}>{r.period}</td>
                <td>{r.employees.toLocaleString()}</td>
                <td style={{ fontWeight: 600 }}>{r.gross}</td>
                <td style={{ fontWeight: 600, color: '#059669' }}>{r.net}</td>
                <td style={{ color: '#d97706' }}>{r.deductions}</td>
                <td>
                  <span className={`badge ${r.status === 'FINALIZED' ? 'badge-success' : 'badge-warning'}`}>{r.status}</span>
                </td>
                <td style={{ color: 'var(--text-muted)' }}>{r.date}</td>
                <td>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    {r.status === 'FINALIZED' && (
                      <button className="btn btn-secondary btn-sm" style={{ padding: '4px 10px' }}
                              onClick={() => setShowPayslip(!showPayslip)}>
                        <FileText size={12} /> Payslips
                      </button>
                    )}
                    {r.status === 'DRAFT' && (
                      <button className="btn btn-primary btn-sm" style={{ padding: '4px 10px' }}>
                        <PlayCircle size={12} /> Process
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Sample Payslip */}
      {showPayslip && (
        <div className="card animate-in" style={{ marginBottom: '20px' }}>
          <div className="card-header">
            <div>
              <div className="card-title">Payslip Preview - {payslipSample.empName}</div>
              <div className="card-subtitle">{payslipSample.period} | {payslipSample.empCode} | {payslipSample.department}</div>
            </div>
            <button className="btn btn-primary btn-sm"><Download size={14} /> Download PDF</button>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
            {/* Earnings */}
            <div>
              <h3 style={{ fontSize: '14px', fontWeight: 700, color: '#059669', marginBottom: '12px' }}>Earnings</h3>
              {payslipSample.earnings.map((e, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--border)' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>{e.component}</span>
                  <span style={{ fontWeight: 600 }}>{e.amount}</span>
                </div>
              ))}
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 700, color: 'var(--text-primary)', fontSize: '15px' }}>
                <span>Gross Salary</span>
                <span style={{ color: '#059669' }}>{payslipSample.grossSalary}</span>
              </div>
            </div>

            {/* Deductions */}
            <div>
              <h3 style={{ fontSize: '14px', fontWeight: 700, color: '#dc2626', marginBottom: '12px' }}>Deductions</h3>
              {payslipSample.deductions.map((d, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 0', borderBottom: '1px solid var(--border-primary)' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>{d.component}</span>
                  <span style={{ fontWeight: 600, color: '#dc2626' }}>{d.amount}</span>
                </div>
              ))}
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', fontWeight: 700, fontSize: '15px' }}>
                <span>Total Deductions</span>
                <span style={{ color: '#dc2626' }}>{payslipSample.totalDeductions}</span>
              </div>
            </div>
          </div>

          {/* Net */}
          <div style={{
            marginTop: '20px', padding: '20px', borderRadius: '8px',
            background: '#eef2ff', border: '1px solid #c7d2fe',
            display: 'flex', justifyContent: 'space-between', alignItems: 'center'
          }}>
            <span style={{ fontSize: '16px', fontWeight: 700 }}>Net Salary (Take Home)</span>
            <span style={{ fontSize: '24px', fontWeight: 800, color: '#4f46e5' }}>
              {payslipSample.netSalary}
            </span>
          </div>
        </div>
      )}
    </div>
  )
}
