import { useState } from 'react'
import { Target, Award, TrendingUp, Users, Star, ChevronRight } from 'lucide-react'
import { RadarChart, Radar, PolarGrid, PolarAngleAxis, PolarRadiusAxis,
         ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Cell } from 'recharts'

const reviewCycles = [
  { id: 'RC-2026-H1', name: 'H1 2026 Annual Review', type: 'ANNUAL', status: 'SELF_REVIEW', year: 2026, progress: 65 },
  { id: 'RC-2025-H2', name: 'H2 2025 Mid-Year', type: 'MID_YEAR', status: 'PUBLISHED', year: 2025, progress: 100 },
]

const goals = [
  { title: 'Deliver NexusHR v1.0', employee: 'Priya Sharma', progress: 85, status: 'IN_PROGRESS', weight: 30 },
  { title: 'Reduce API latency below 200ms', employee: 'Karthik Iyer', progress: 100, status: 'COMPLETED', weight: 25 },
  { title: 'Onboard 50 new enterprise clients', employee: 'Rahul Verma', progress: 60, status: 'IN_PROGRESS', weight: 30 },
  { title: 'Launch employee self-service portal', employee: 'Arjun Nair', progress: 45, status: 'IN_PROGRESS', weight: 20 },
  { title: 'Implement AI attrition model', employee: 'Priya Sharma', progress: 70, status: 'IN_PROGRESS', weight: 25 },
  { title: 'Achieve 95% payroll accuracy', employee: 'Meera Patel', progress: 92, status: 'IN_PROGRESS', weight: 20 },
]

const radarData = [
  { metric: 'Technical Skills', value: 88 },
  { metric: 'Leadership', value: 75 },
  { metric: 'Communication', value: 82 },
  { metric: 'Problem Solving', value: 91 },
  { metric: 'Teamwork', value: 85 },
  { metric: 'Innovation', value: 78 },
]

const bellCurveData = [
  { rating: 'Needs Improvement', count: 52, color: '#ef4444' },
  { rating: 'Meets Expectations', count: 312, color: '#f59e0b' },
  { rating: 'Exceeds Expectations', count: 890, color: '#06b6d4' },
  { rating: 'Outstanding', count: 1250, color: '#10b981' },
  { rating: 'Exceptional', count: 180, color: '#6366f1' },
]

const reviews360 = [
  { reviewer: 'Manager - Arun Kumar', type: 'MANAGER', rating: 4.2, status: 'SUBMITTED', comments: 'Strong technical leadership, needs more delegation skills.' },
  { reviewer: 'Peer - Rahul Verma', type: 'PEER', rating: 4.5, status: 'SUBMITTED', comments: 'Excellent collaborator, always willing to help team members.' },
  { reviewer: 'Peer - Anita Desai', type: 'PEER', rating: 4.0, status: 'SUBMITTED', comments: 'Great attention to detail, could improve communication.' },
  { reviewer: 'Self - Priya Sharma', type: 'SELF', rating: 4.1, status: 'SUBMITTED', comments: 'Focused on delivery, looking to develop leadership skills.' },
]

const tooltipStyle = {
  backgroundColor: '#fff', border: '1px solid #e5e7eb',
  borderRadius: '8px', color: '#111827', fontSize: '12px',
  boxShadow: '0 4px 12px rgba(0,0,0,.08)',
}

export default function Performance() {
  const [activeTab, setActiveTab] = useState<'goals' | 'reviews' | '360'>('goals')

  return (
    <div className="page-container">
      <div style={{ marginBottom: '24px' }} className="animate-in">
        <h1 style={{ fontSize: '24px', fontWeight: 800 }}>Performance Management</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
          OKR framework · 360° feedback · Bell curve calibration · Succession planning
        </p>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon purple"><Target size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Active Goals</div>
            <div className="stat-value">1,284</div>
            <div className="stat-change positive">72% on track</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-1">
          <div className="stat-icon blue"><Award size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Reviews Completed</div>
            <div className="stat-value">3,890</div>
            <div className="stat-change positive">65% of cycle</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-2">
          <div className="stat-icon green"><Star size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">Avg Rating</div>
            <div className="stat-value">4.1</div>
            <div className="stat-change positive">+0.2 vs H2</div>
          </div>
        </div>
        <div className="stat-card animate-in-delay-3">
          <div className="stat-icon amber"><Users size={22} /></div>
          <div className="stat-content">
            <div className="stat-label">High Potentials</div>
            <div className="stat-value">186</div>
            <div className="stat-change positive">3.6% of workforce</div>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="charts-grid">
        <div className="chart-card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">Competency Radar (Org Average)</div>
              <div className="card-subtitle">360° assessment across 6 competencies</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={280}>
            <RadarChart data={radarData} cx="50%" cy="50%" outerRadius="70%">
              <PolarGrid stroke="rgba(0,0,0,0.08)" />
              <PolarAngleAxis dataKey="metric" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <PolarRadiusAxis tick={{ fill: '#64748b', fontSize: 10 }} domain={[0, 100]} />
              <Radar name="Score" dataKey="value" stroke="#6366f1" fill="#6366f1" fillOpacity={0.25} strokeWidth={2} />
            </RadarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card animate-in-delay-1">
          <div className="card-header">
            <div>
              <div className="card-title">Rating Distribution (Bell Curve)</div>
              <div className="card-subtitle">Forced distribution across rating bands</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={bellCurveData}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
              <XAxis dataKey="rating" tick={{ fill: '#64748b', fontSize: 10 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                {bellCurveData.map((e, i) => <Cell key={i} fill={e.color} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '4px', marginBottom: '20px', background: '#f3f4f6', borderRadius: '8px', padding: '4px', width: 'fit-content', border: '1px solid #e5e7eb' }}>
        {[
          { key: 'goals', label: 'OKR Goals' },
          { key: 'reviews', label: 'Review Cycles' },
          { key: '360', label: '360° Feedback' },
        ].map(tab => (
          <button key={tab.key} onClick={() => setActiveTab(tab.key as any)}
            style={{
              padding: '8px 20px', borderRadius: '6px', border: 'none', cursor: 'pointer',
              fontSize: '13px', fontWeight: 600, transition: 'all 0.2s',
              background: activeTab === tab.key ? '#4f46e5' : 'transparent',
              color: activeTab === tab.key ? 'white' : 'var(--text-secondary)',
            }}>
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'goals' && (
        <div className="card animate-in">
          <div className="card-header">
            <div className="card-title">Active Goals & OKRs</div>
            <button className="btn btn-primary btn-sm"><Target size={14} /> Add Goal</button>
          </div>
          {goals.map((g, i) => (
            <div key={i} style={{
              padding: '16px', borderBottom: i < goals.length - 1 ? '1px solid #e5e7eb' : 'none',
              display: 'flex', alignItems: 'center', gap: '16px',
            }}>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '14px', marginBottom: '4px' }}>{g.title}</div>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{g.employee} · Weight: {g.weight}%</div>
              </div>
              <div style={{ width: '200px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '4px' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Progress</span>
                  <span style={{ fontWeight: 700, color: g.progress >= 80 ? '#059669' : '#d97706' }}>{g.progress}%</span>
                </div>
                <div style={{ height: '6px', background: '#f3f4f6', borderRadius: '3px', overflow: 'hidden' }}>
                  <div style={{
                    height: '100%', borderRadius: '3px', transition: 'width 0.5s ease',
                    width: `${g.progress}%`,
                    background: g.progress >= 80 ? '#059669' : g.progress >= 50 ? '#2563eb' : '#d97706',
                  }} />
                </div>
              </div>
              <span className={`badge ${g.status === 'COMPLETED' ? 'badge-success' : 'badge-info'}`}>{g.status.replace('_', ' ')}</span>
            </div>
          ))}
        </div>
      )}

      {activeTab === 'reviews' && (
        <div className="card animate-in">
          <div className="card-header">
            <div className="card-title">Review Cycles</div>
            <button className="btn btn-primary btn-sm">Create Cycle</button>
          </div>
          {reviewCycles.map((c, i) => (
            <div key={i} style={{
              padding: '20px', borderBottom: i < reviewCycles.length - 1 ? '1px solid #e5e7eb' : 'none',
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            }}>
              <div>
                <div style={{ fontWeight: 700, color: 'var(--text-primary)', fontSize: '15px' }}>{c.name}</div>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '4px' }}>
                  {c.id} · {c.type} · {c.year}
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                <div style={{ width: '150px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '4px' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Progress</span>
                    <span style={{ fontWeight: 700 }}>{c.progress}%</span>
                  </div>
                  <div style={{ height: '6px', background: '#f3f4f6', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', borderRadius: '3px', width: `${c.progress}%`, background: '#4f46e5' }} />
                  </div>
                </div>
                <span className={`badge ${c.status === 'PUBLISHED' ? 'badge-success' : 'badge-warning'}`}>{c.status.replace('_', ' ')}</span>
                <ChevronRight size={16} style={{ color: 'var(--text-muted)' }} />
              </div>
            </div>
          ))}
        </div>
      )}

      {activeTab === '360' && (
        <div className="card animate-in">
          <div className="card-header">
            <div>
              <div className="card-title">360° Feedback - Priya Sharma</div>
              <div className="card-subtitle">Annual Review · H1 2026</div>
            </div>
          </div>
          {reviews360.map((r, i) => (
            <div key={i} style={{
              padding: '20px', borderBottom: i < reviews360.length - 1 ? '1px solid #e5e7eb' : 'none',
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{r.reviewer}</span>
                  <span className={`badge ${r.type === 'MANAGER' ? 'badge-purple' : r.type === 'PEER' ? 'badge-info' : 'badge-warning'}`}>{r.type}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  {Array.from({ length: 5 }).map((_, si) => (
                    <Star key={si} size={14} fill={si < Math.round(r.rating) ? '#f59e0b' : 'transparent'}
                          color={si < Math.round(r.rating) ? '#f59e0b' : 'var(--text-muted)'} />
                  ))}
                  <span style={{ fontWeight: 700, marginLeft: '6px' }}>{r.rating}</span>
                </div>
              </div>
              <p style={{ fontSize: '13px', color: 'var(--text-secondary)', fontStyle: 'italic' }}>"{r.comments}"</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
