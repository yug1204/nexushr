import { useState } from 'react'
import { Clock, CheckCircle, XCircle, Calendar, TrendingUp } from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

const weeklyAttendance = [
  { day: 'Mon', present: 4850, absent: 120, leave: 213 },
  { day: 'Tue', present: 4910, absent: 95, leave: 178 },
  { day: 'Wed', present: 4780, absent: 150, leave: 253 },
  { day: 'Thu', present: 4891, absent: 110, leave: 182 },
  { day: 'Fri', present: 4720, absent: 180, leave: 283 },
]

const attendanceRecords = [
  { emp: 'Priya Sharma', code: 'NHR-001001', clockIn: '09:02 AM', clockOut: '06:15 PM', hours: '9h 13m', status: 'Present', overtime: '1h 13m' },
  { emp: 'Rahul Verma', code: 'NHR-001002', clockIn: '09:30 AM', clockOut: '06:00 PM', hours: '8h 30m', status: 'Present', overtime: '0h 30m' },
  { emp: 'Anita Desai', code: 'NHR-001003', clockIn: '-', clockOut: '-', hours: '-', status: 'On Leave', overtime: '-' },
  { emp: 'Vikram Singh', code: 'NHR-001004', clockIn: '08:45 AM', clockOut: '05:30 PM', hours: '8h 45m', status: 'Present', overtime: '0h 45m' },
  { emp: 'Meera Patel', code: 'NHR-001005', clockIn: '10:15 AM', clockOut: '-', hours: 'In Progress', status: 'Present', overtime: '-' },
  { emp: 'Arjun Nair', code: 'NHR-001006', clockIn: '-', clockOut: '-', hours: '-', status: 'Absent', overtime: '-' },
  { emp: 'Sneha Gupta', code: 'NHR-001007', clockIn: '-', clockOut: '-', hours: '-', status: 'On Leave', overtime: '-' },
  { emp: 'Karthik Iyer', code: 'NHR-001008', clockIn: '08:30 AM', clockOut: '07:00 PM', hours: '10h 30m', status: 'Present', overtime: '2h 30m' },
]

const leaveRequests = [
  { emp: 'Anita Desai', type: 'Casual Leave', from: '2026-05-10', to: '2026-05-12', days: 3, status: 'PENDING', reason: 'Family function' },
  { emp: 'Sneha Gupta', type: 'Sick Leave', from: '2026-05-09', to: '2026-05-11', days: 3, status: 'APPROVED', reason: 'Medical appointment' },
  { emp: 'Rahul Verma', type: 'Earned Leave', from: '2026-05-20', to: '2026-05-25', days: 6, status: 'PENDING', reason: 'Vacation' },
]

const tooltipStyle = {
  backgroundColor: '#fff', border: '1px solid #e5e7eb',
  borderRadius: '8px', color: '#111827', fontSize: '12px',
  boxShadow: '0 4px 12px rgba(0,0,0,.08)',
}

export default function Attendance() {
  const [activeTab, setActiveTab] = useState<'attendance' | 'leave'>('attendance')

  return (
    <div className="page-container">
      <div style={{ marginBottom: '24px' }} className="animate-in">
        <h1 style={{ fontSize: '24px', fontWeight: 800 }}>Attendance & Leave Management</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
          Real-time attendance tracking with leave approval workflows
        </p>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon green"><CheckCircle size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Present Today</div>
            <div className="stat-value">4,891</div>
            <div className="stat-change positive">94.4%</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-1">
          <div className="stat-icon red"><XCircle size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Absent</div>
            <div className="stat-value">110</div>
            <div className="stat-change negative">2.1%</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-2">
          <div className="stat-icon blue"><Calendar size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">On Leave</div>
            <div className="stat-value">182</div>
            <div className="stat-change">3.5%</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-3">
          <div className="stat-icon amber"><Clock size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Pending Approvals</div>
            <div className="stat-value">12</div>
          </div>
        </div>
      </div>

      {/* Weekly Chart */}
      <div className="chart-card animate-in" style={{ marginBottom: '20px' }}>
        <div className="card-header">
          <div>
            <div className="card-title">Weekly Attendance Overview</div>
            <div className="card-subtitle">Present vs Absent vs On Leave</div>
          </div>
        </div>
        <ResponsiveContainer width="100%" height={240}>
          <BarChart data={weeklyAttendance}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
            <XAxis dataKey="day" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
            <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
            <Tooltip contentStyle={tooltipStyle} />
            <Bar dataKey="present" fill="#10b981" radius={[4, 4, 0, 0]} name="Present" />
            <Bar dataKey="absent" fill="#ef4444" radius={[4, 4, 0, 0]} name="Absent" />
            <Bar dataKey="leave" fill="#f59e0b" radius={[4, 4, 0, 0]} name="On Leave" />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '4px', marginBottom: '20px', background: '#f3f4f6', borderRadius: '8px', padding: '4px', width: 'fit-content', border: '1px solid #e5e7eb' }}>
        {['attendance', 'leave'].map(tab => (
          <button key={tab} onClick={() => setActiveTab(tab as any)}
            style={{
              padding: '8px 20px', borderRadius: '6px', border: 'none', cursor: 'pointer',
              fontSize: '13px', fontWeight: 600, transition: 'all 0.2s',
              background: activeTab === tab ? '#4f46e5' : 'transparent',
              color: activeTab === tab ? 'white' : 'var(--text-secondary)',
            }}>
            {tab === 'attendance' ? 'Today\'s Attendance' : 'Leave Requests'}
          </button>
        ))}
      </div>

      {activeTab === 'attendance' ? (
        <div className="card animate-in">
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee</th><th>Code</th><th>Clock In</th><th>Clock Out</th>
                <th>Work Hours</th><th>Overtime</th><th>Status</th>
              </tr>
            </thead>
            <tbody>
              {attendanceRecords.map((r, i) => (
                <tr key={i}>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{r.emp}</td>
                  <td style={{ fontFamily: 'monospace', fontSize: '12px' }}>{r.code}</td>
                  <td>{r.clockIn}</td><td>{r.clockOut}</td>
                  <td style={{ fontWeight: 500 }}>{r.hours}</td>
                  <td>{r.overtime !== '-' && r.overtime !== '0h 0m' ?
                    <span style={{ color: '#d97706' }}>{r.overtime}</span> : r.overtime}</td>
                  <td><span className={`badge ${r.status === 'Present' ? 'badge-success' : r.status === 'On Leave' ? 'badge-info' : 'badge-danger'}`}>{r.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="card animate-in">
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee</th><th>Leave Type</th><th>From</th><th>To</th>
                <th>Days</th><th>Reason</th><th>Status</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {leaveRequests.map((l, i) => (
                <tr key={i}>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{l.emp}</td>
                  <td><span className="badge badge-purple">{l.type}</span></td>
                  <td>{l.from}</td><td>{l.to}</td>
                  <td style={{ fontWeight: 600 }}>{l.days}</td>
                  <td>{l.reason}</td>
                  <td><span className={`badge ${l.status === 'APPROVED' ? 'badge-success' : 'badge-warning'}`}>{l.status}</span></td>
                  <td>
                    {l.status === 'PENDING' && (
                      <div style={{ display: 'flex', gap: '6px' }}>
                        <button className="btn btn-primary btn-sm" style={{ padding: '4px 12px' }}>Approve</button>
                        <button className="btn btn-secondary btn-sm" style={{ padding: '4px 12px' }}>Reject</button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
