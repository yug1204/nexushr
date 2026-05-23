import { useState } from 'react'
import {
  Brain, AlertTriangle, TrendingUp, TrendingDown, Shield, Target,
  Users, Zap, BarChart3, ArrowUpRight, Sparkles, RefreshCw
} from 'lucide-react'
import {
  BarChart, Bar, PieChart, Pie, Cell, RadarChart, Radar, PolarGrid,
  PolarAngleAxis, PolarRadiusAxis, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Legend, AreaChart, Area
} from 'recharts'

const atRiskEmployees = [
  { id: 'EMP008', name: 'Suresh Kumar', dept: 'Support', role: 'Support Lead', score: 0.89, risk: 'CRITICAL', tenure: 72, perf: 2.5, raise: '1%', topFactor: 'Low engagement (2.0/10), 60 months without promotion' },
  { id: 'EMP002', name: 'Rahul Verma', dept: 'Sales', role: 'Account Manager', score: 0.82, risk: 'CRITICAL', tenure: 36, perf: 2.0, raise: '0%', topFactor: 'No salary change, 42 months without promotion' },
  { id: 'EMP005', name: 'Deepa Nair', dept: 'Finance', role: 'Analyst', score: 0.71, risk: 'HIGH', tenure: 4, perf: 3.0, raise: '0%', topFactor: 'New hire flight risk (4 months), no salary change' },
  { id: 'EMP004', name: 'Vikram Singh', dept: 'Engineering', role: 'Tech Lead', score: 0.62, risk: 'HIGH', tenure: 60, perf: 4.8, raise: '3%', topFactor: 'Top talent at risk — 48 months without promotion' },
  { id: 'EMP009', name: 'Neha Gupta', dept: 'Engineering', role: 'Junior Dev', score: 0.58, risk: 'MEDIUM', tenure: 3, perf: 3.2, raise: '0%', topFactor: 'New hire, engagement score 5.5/10' },
  { id: 'EMP001', name: 'Priya Sharma', dept: 'Engineering', role: 'Senior Dev', score: 0.45, risk: 'MEDIUM', tenure: 8, perf: 4.5, raise: '2%', topFactor: 'Below-market salary adjustment' },
  { id: 'EMP006', name: 'Arjun Patel', dept: 'Operations', role: 'Ops Manager', score: 0.22, risk: 'LOW', tenure: 48, perf: 3.5, raise: '8%', topFactor: 'Stable — good raise, engaged' },
  { id: 'EMP007', name: 'Kavita Menon', dept: 'HR', role: 'HRBP', score: 0.08, risk: 'LOW', tenure: 18, perf: 4.2, raise: '15%', topFactor: 'Highly engaged, recent promotion' },
]

const riskDistribution = [
  { name: 'Low Risk', value: 3420, color: '#10b981' },
  { name: 'Medium Risk', value: 980, color: '#f59e0b' },
  { name: 'High Risk', value: 520, color: '#f97316' },
  { name: 'Critical', value: 263, color: '#ef4444' },
]

const deptAttrition = [
  { dept: 'Support', avgScore: 0.72 },
  { dept: 'Sales', avgScore: 0.58 },
  { dept: 'Finance', avgScore: 0.45 },
  { dept: 'Engineering', avgScore: 0.38 },
  { dept: 'Marketing', avgScore: 0.25 },
  { dept: 'Operations', avgScore: 0.22 },
  { dept: 'HR', avgScore: 0.15 },
]

const skillGapData = [
  { skill: 'Cloud Architecture', avgGap: 35, count: 420 },
  { skill: 'Machine Learning', avgGap: 42, count: 280 },
  { skill: 'DevOps/K8s', avgGap: 28, count: 350 },
  { skill: 'System Design', avgGap: 25, count: 310 },
  { skill: 'Data Engineering', avgGap: 32, count: 190 },
  { skill: 'Cybersecurity', avgGap: 38, count: 150 },
]

const engagementTrend = [
  { month: 'Jan', score: 6.8 }, { month: 'Feb', score: 6.9 },
  { month: 'Mar', score: 7.1 }, { month: 'Apr', score: 6.7 },
  { month: 'May', score: 7.3 }, { month: 'Jun', score: 7.5 },
]

const featureImportance = [
  { feature: 'Salary Change', importance: 0.20 },
  { feature: 'Engagement', importance: 0.20 },
  { feature: 'Tenure', importance: 0.18 },
  { feature: 'Performance', importance: 0.15 },
  { feature: 'Promotion Lag', importance: 0.15 },
  { feature: 'Absence', importance: 0.12 },
]

const tooltipStyle = {
  backgroundColor: '#fff',
  border: '1px solid #e5e7eb',
  borderRadius: '8px',
  color: '#111827',
  fontSize: '12px',
  boxShadow: '0 4px 12px rgba(0,0,0,.08)',
}

export default function AiInsights() {
  const [selectedEmployee, setSelectedEmployee] = useState<string | null>(null)

  const getRiskColor = (risk: string) => {
    switch (risk) {
      case 'CRITICAL': return '#ef4444'
      case 'HIGH': return '#f97316'
      case 'MEDIUM': return '#f59e0b'
      case 'LOW': return '#10b981'
      default: return '#94a3b8'
    }
  }

  return (
    <div className="page-container">
      <div style={{ marginBottom: '28px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }} className="animate-in">
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '4px' }}>
            <Sparkles size={24} style={{ color: '#8b5cf6' }} />
            <h1 style={{ fontSize: '24px', fontWeight: 800 }}>AI Workforce Intelligence</h1>
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
            Predictive attrition · Skill gap analysis · Engagement scoring · Model v1.2.0 (AUC: 0.84)
          </p>
        </div>
        <button className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <RefreshCw size={14} /> Retrain Model
        </button>
      </div>

      {/* AI KPI Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon" style={{ background: 'rgba(239,68,68,0.1)' }}><AlertTriangle size={22} style={{ color: '#ef4444' }} /></div>
          <div className="stat-content">
            <div className="stat-label">At-Risk Employees</div>
            <div className="stat-value">783</div>
            <div className="stat-change negative"><TrendingUp size={12} /> +12% vs last quarter</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-1">
          <div className="stat-icon" style={{ background: 'rgba(139,92,246,0.1)' }}><Brain size={22} style={{ color: '#8b5cf6' }} /></div>
          <div className="stat-content">
            <div className="stat-label">Model Accuracy (AUC)</div>
            <div className="stat-value">0.84</div>
            <div className="stat-change positive"><TrendingUp size={12} /> +0.02 vs previous</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-2">
          <div className="stat-icon" style={{ background: 'rgba(16,185,129,0.1)' }}><Shield size={22} style={{ color: '#10b981' }} /></div>
          <div className="stat-content">
            <div className="stat-label">Retention Actions Taken</div>
            <div className="stat-value">156</div>
            <div className="stat-change positive"><TrendingUp size={12} /> 78% success rate</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-3">
          <div className="stat-icon" style={{ background: 'rgba(245,158,11,0.1)' }}><Target size={22} style={{ color: '#f59e0b' }} /></div>
          <div className="stat-content">
            <div className="stat-label">Avg Engagement Score</div>
            <div className="stat-value">7.5/10</div>
            <div className="stat-change positive"><TrendingUp size={12} /> +0.4 this quarter</div>
          </div>
        </div>
      </div>

      {/* Charts Row 1: Risk Distribution + Dept Attrition */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Risk Distribution</div>
              <div className="card-subtitle">Employee count by attrition risk level</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={riskDistribution} cx="50%" cy="50%" innerRadius={65} outerRadius={95}
                   dataKey="value" stroke="none" paddingAngle={3}>
                {riskDistribution.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={tooltipStyle} />
              <Legend iconType="circle" iconSize={8} wrapperStyle={{ fontSize: '11px' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Attrition Risk by Department</div>
              <div className="card-subtitle">Average attrition score per department</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={deptAttrition} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
              <XAxis type="number" domain={[0, 1]} tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis type="category" dataKey="dept" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} width={80} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="avgScore" radius={[0, 4, 4, 0]} name="Avg Risk Score">
                {deptAttrition.map((entry, i) => (
                  <Cell key={i} fill={entry.avgScore > 0.5 ? '#ef4444' : entry.avgScore > 0.3 ? '#f59e0b' : '#10b981'} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row 2: Skill Gaps + Engagement */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Top Skill Gaps</div>
              <div className="card-subtitle">Organizational skill deficits requiring training</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={skillGapData}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
              <XAxis dataKey="skill" tick={{ fill: '#64748b', fontSize: 10 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="avgGap" fill="#8b5cf6" radius={[4, 4, 0, 0]} name="Gap Score" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Engagement Trend</div>
              <div className="card-subtitle">Monthly organization-wide engagement score</div>
            </div>
            <span className="badge badge-success">↑ Improving</span>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={engagementTrend}>
              <defs>
                <linearGradient id="gradientEng" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#10b981" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
              <XAxis dataKey="month" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} domain={[5, 10]} />
              <Tooltip contentStyle={tooltipStyle} />
              <Area type="monotone" dataKey="score" stroke="#10b981" strokeWidth={2.5} fill="url(#gradientEng)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* At-Risk Employees Table */}
      <div className="card animate-in" style={{ marginBottom: '28px' }}>
        <div className="card-header">
          <div>
            <div className="card-title">🔍 At-Risk Employees — AI Predictions</div>
            <div className="card-subtitle">SHAP-explained attrition risk with actionable recommendations</div>
          </div>
          <span className="badge badge-purple">Model rf-v1.2.0</span>
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee</th>
              <th>Department</th>
              <th>Risk Score</th>
              <th>Level</th>
              <th>Tenure</th>
              <th>Rating</th>
              <th>Top Factor (SHAP)</th>
            </tr>
          </thead>
          <tbody>
            {atRiskEmployees.map((emp) => (
              <tr key={emp.id}>
                <td>
                  <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{emp.name}</div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>{emp.role}</div>
                </td>
                <td>{emp.dept}</td>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <div style={{
                      width: '60px', height: '6px', borderRadius: '3px',
                      background: 'rgba(0,0,0,0.06)', overflow: 'hidden'
                    }}>
                      <div style={{
                        width: `${emp.score * 100}%`, height: '100%', borderRadius: '3px',
                        background: getRiskColor(emp.risk)
                      }} />
                    </div>
                    <span style={{ fontWeight: 600, fontSize: '13px' }}>{(emp.score * 100).toFixed(0)}%</span>
                  </div>
                </td>
                <td>
                  <span className={`badge ${
                    emp.risk === 'CRITICAL' ? 'badge-danger' :
                    emp.risk === 'HIGH' ? 'badge-warning' :
                    emp.risk === 'MEDIUM' ? 'badge-info' : 'badge-success'
                  }`}>
                    {emp.risk}
                  </span>
                </td>
                <td>{emp.tenure}m</td>
                <td>{emp.perf}</td>
                <td style={{ fontSize: '12px', color: 'var(--text-muted)', maxWidth: '280px' }}>{emp.topFactor}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Feature Importance */}
      <div className="card animate-in" style={{ marginBottom: '28px' }}>
        <div className="card-header">
          <div>
            <div className="card-title">📊 Model Feature Importance</div>
            <div className="card-subtitle">Random Forest feature weights — top drivers of attrition prediction</div>
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '16px', padding: '20px' }}>
          {featureImportance.map((f) => (
            <div key={f.feature} style={{ padding: '16px', borderRadius: '10px', border: '1px solid var(--border-light)' }}>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '8px' }}>{f.feature}</div>
              <div style={{ fontSize: '20px', fontWeight: 700 }}>{(f.importance * 100).toFixed(0)}%</div>
              <div style={{
                width: '100%', height: '4px', borderRadius: '2px',
                background: 'rgba(0,0,0,0.06)', marginTop: '8px'
              }}>
                <div style={{
                  width: `${f.importance * 500}%`, height: '100%', borderRadius: '2px',
                  background: '#8b5cf6'
                }} />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
