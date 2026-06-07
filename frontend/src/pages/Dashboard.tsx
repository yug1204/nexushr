import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { reportsApi } from '../api/client'
import {
  Users, Clock, Wallet, Target, TrendingUp, TrendingDown,
  UserPlus, UserMinus, CalendarDays, DollarSign, ArrowUpRight
} from 'lucide-react'
import {
  AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from 'recharts'

const headcountData = [
  { month: 'Jan', count: 4820, hires: 45, exits: 12 },
  { month: 'Feb', count: 4853, hires: 52, exits: 19 },
  { month: 'Mar', count: 4886, hires: 60, exits: 27 },
  { month: 'Apr', count: 4919, hires: 48, exits: 15 },
  { month: 'May', count: 4952, hires: 55, exits: 22 },
  { month: 'Jun', count: 4985, hires: 62, exits: 29 },
  { month: 'Jul', count: 5018, hires: 58, exits: 25 },
  { month: 'Aug', count: 5051, hires: 47, exits: 14 },
  { month: 'Sep', count: 5084, hires: 53, exits: 20 },
  { month: 'Oct', count: 5117, hires: 65, exits: 32 },
  { month: 'Nov', count: 5150, hires: 50, exits: 17 },
  { month: 'Dec', count: 5183, hires: 42, exits: 9 },
]

const deptData = [
  { name: 'Engineering', value: 1850, color: '#6366f1' },
  { name: 'Sales', value: 980, color: '#06b6d4' },
  { name: 'Marketing', value: 620, color: '#8b5cf6' },
  { name: 'Operations', value: 750, color: '#10b981' },
  { name: 'Finance', value: 420, color: '#f59e0b' },
  { name: 'HR', value: 180, color: '#ec4899' },
  { name: 'Support', value: 383, color: '#3b82f6' },
]

const payrollTrend = [
  { month: 'Jan', gross: 42.5, net: 34.2, deductions: 8.3 },
  { month: 'Feb', gross: 43.1, net: 34.8, deductions: 8.3 },
  { month: 'Mar', gross: 44.2, net: 35.6, deductions: 8.6 },
  { month: 'Apr', gross: 43.8, net: 35.3, deductions: 8.5 },
  { month: 'May', gross: 45.1, net: 36.4, deductions: 8.7 },
  { month: 'Jun', gross: 46.3, net: 37.2, deductions: 9.1 },
]

const attritionData = [
  { month: 'Jan', rate: 2.1 }, { month: 'Feb', rate: 2.3 },
  { month: 'Mar', rate: 1.8 }, { month: 'Apr', rate: 2.0 },
  { month: 'May', rate: 1.6 }, { month: 'Jun', rate: 1.4 },
]

const recentActivities = [
  { action: 'New employee onboarded', name: 'Priya Sharma', dept: 'Engineering', time: '2 min ago', type: 'hire' },
  { action: 'Leave approved', name: 'Rahul Verma', dept: 'Sales', time: '15 min ago', type: 'leave' },
  { action: 'Payroll run completed', name: 'March 2026', dept: 'Finance', time: '1 hour ago', type: 'payroll' },
  { action: 'Performance review submitted', name: 'Anita Desai', dept: 'Marketing', time: '2 hours ago', type: 'review' },
  { action: 'Employee terminated', name: 'Vikram Singh', dept: 'Support', time: '3 hours ago', type: 'exit' },
]

const tooltipStyle = {
  backgroundColor: '#18181b',
  border: '1px solid rgba(255,255,255,0.1)',
  borderRadius: '8px',
  color: '#fafafa',
  fontSize: '12px',
  boxShadow: '0 4px 24px -4px rgba(0,0,0,0.5)',
}

export default function Dashboard() {
  const { data: reportData, isLoading } = useQuery({
    queryKey: ['executiveReport'],
    queryFn: async () => {
      const res = await reportsApi.executive();
      return res.data.data; // ApiResponse.success returns { data: ... }
    }
  });

  if (isLoading) {
    return <div className="page-container">Loading Dashboard Data...</div>;
  }

  // Use real data if available, fallback to dummy data for chart structure
  const totalEmployees = reportData?.totalHeadcount || 5183;
  const attritionRate = reportData?.attritionRate || 1.4;
  const payrollCost = reportData?.payrollCost || "46.3L";
  return (
    <div className="page-container">
      <div style={{ marginBottom: '28px' }} className="animate-in">
        <h1 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '4px' }}>
          Workforce Dashboard
        </h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
          Real-time workforce intelligence · {new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </p>
      </div>

      {/* KPI Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon purple"><Users size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Total Employees</div>
            <div className="stat-value">{totalEmployees.toLocaleString()}</div>
            <div className="stat-change positive">
              <TrendingUp size={12} /> +3.2% this month
            </div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-1">
          <div className="stat-icon blue"><Clock size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Present Today</div>
            <div className="stat-value">4,891</div>
            <div className="stat-change positive">
              <TrendingUp size={12} /> 94.4% attendance
            </div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-2">
          <div className="stat-icon green"><Wallet size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Payroll Cost (Monthly)</div>
            <div className="stat-value">₹{payrollCost}</div>
            <div className="stat-change negative">
              <TrendingDown size={12} /> +2.6% vs last month
            </div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-3">
          <div className="stat-icon amber"><Target size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Attrition Rate</div>
            <div className="stat-value">{attritionRate}%</div>
            <div className="stat-change positive">
              <TrendingDown size={12} /> -0.6% vs last quarter
            </div>
          </div>
        </div>
      </div>

      {/* Charts Row 1 */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Headcount Trend</div>
              <div className="card-subtitle">Monthly employee count over 12 months</div>
            </div>
            <span className="badge badge-success">+7.5% YoY</span>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={headcountData}>
              <defs>
                <linearGradient id="gradientCount" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#6366f1" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
              <XAxis dataKey="month" tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} domain={[4700, 5300]} />
              <Tooltip contentStyle={tooltipStyle} />
              <Area type="monotone" dataKey="count" stroke="#6366f1" strokeWidth={2.5} fill="url(#gradientCount)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Department Distribution</div>
              <div className="card-subtitle">Employees by department</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={deptData} cx="50%" cy="50%" innerRadius={65} outerRadius={95}
                   dataKey="value" stroke="none" paddingAngle={3}>
                {deptData.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={tooltipStyle} />
              <Legend
                iconType="circle"
                iconSize={8}
                wrapperStyle={{ fontSize: '11px', color: '#a1a1aa' }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2 */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Payroll Cost Trend</div>
              <div className="card-subtitle">Monthly gross vs net (₹ Lakhs)</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={payrollTrend}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
              <XAxis dataKey="month" tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="gross" fill="#6366f1" radius={[4, 4, 0, 0]} name="Gross" />
              <Bar dataKey="net" fill="#06b6d4" radius={[4, 4, 0, 0]} name="Net" />
              <Bar dataKey="deductions" fill="#f59e0b" radius={[4, 4, 0, 0]} name="Deductions" />
              <Legend iconType="circle" iconSize={8} wrapperStyle={{ fontSize: '11px' }} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Attrition Trend</div>
              <div className="card-subtitle">Monthly attrition rate (%)</div>
            </div>
            <span className="badge badge-success">Below 2% target</span>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={attritionData}>
              <defs>
                <linearGradient id="gradientAttrition" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#ef4444" stopOpacity={0.2}/>
                  <stop offset="95%" stopColor="#ef4444" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
              <XAxis dataKey="month" tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} domain={[0, 3]} />
              <Tooltip contentStyle={tooltipStyle} />
              <Area type="monotone" dataKey="rate" stroke="#ef4444" strokeWidth={2.5} fill="url(#gradientAttrition)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="card animate-in" style={{ marginBottom: '28px' }}>
        <div className="card-header">
          <div>
            <div className="card-title">Recent Activity</div>
            <div className="card-subtitle">Latest HR operations across the platform</div>
          </div>
          <button className="btn btn-secondary btn-sm">View All</button>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Action</th>
              <th>Name / Reference</th>
              <th>Department</th>
              <th>Time</th>
              <th>Type</th>
            </tr>
          </thead>
          <tbody>
            {recentActivities.map((a, i) => (
              <tr key={i}>
                <td style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{a.action}</td>
                <td>{a.name}</td>
                <td>{a.dept}</td>
                <td style={{ color: 'var(--text-muted)' }}>{a.time}</td>
                <td>
                  <span className={`badge ${
                    a.type === 'hire' ? 'badge-success' :
                    a.type === 'leave' ? 'badge-info' :
                    a.type === 'payroll' ? 'badge-purple' :
                    a.type === 'review' ? 'badge-warning' :
                    'badge-danger'
                  }`}>
                    {a.type}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
