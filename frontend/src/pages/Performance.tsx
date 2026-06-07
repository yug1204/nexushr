import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { performanceApi } from '../api/client'
import { Target, Award, Star, Users, ChevronRight } from 'lucide-react'
import { RadarChart, Radar, PolarGrid, PolarAngleAxis, PolarRadiusAxis,
         ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Cell } from 'recharts'
import toast from 'react-hot-toast'

const reviewCycles = [
  { id: 'RC-2026-H1', name: 'H1 2026 Annual Review', type: 'ANNUAL', status: 'SELF_REVIEW', year: 2026, progress: 65 },
  { id: 'RC-2025-H2', name: 'H2 2025 Mid-Year', type: 'MID_YEAR', status: 'PUBLISHED', year: 2025, progress: 100 },
]

const initialGoals = [
  { id: 'g1', title: 'Deliver NexusHR v1.0', employee: 'Priya Sharma', progress: 85, status: 'IN_PROGRESS', weight: 30 },
  { id: 'g2', title: 'Reduce API latency below 200ms', employee: 'Karthik Iyer', progress: 100, status: 'COMPLETED', weight: 25 },
  { id: 'g3', title: 'Onboard 50 new enterprise clients', employee: 'Rahul Verma', progress: 60, status: 'IN_PROGRESS', weight: 30 },
  { id: 'g4', title: 'Launch employee self-service portal', employee: 'Arjun Nair', progress: 45, status: 'IN_PROGRESS', weight: 20 },
  { id: 'g5', title: 'Implement AI attrition model', employee: 'Priya Sharma', progress: 70, status: 'IN_PROGRESS', weight: 25 },
  { id: 'g6', title: 'Achieve 95% payroll accuracy', employee: 'Meera Patel', progress: 92, status: 'IN_PROGRESS', weight: 20 },
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
  backgroundColor: '#18181b', border: '1px solid rgba(255,255,255,0.1)',
  borderRadius: '8px', color: '#fafafa', fontSize: '12px',
  boxShadow: '0 4px 24px -4px rgba(0,0,0,0.5)',
}

export default function Performance() {
  const [activeTab, setActiveTab] = useState<'goals' | 'reviews' | '360'>('goals')
  const [goals, setGoals] = useState(initialGoals)
  const [showAddGoal, setShowAddGoal] = useState(false)
  const [newGoal, setNewGoal] = useState({ title: '', employee: '', weight: 20 })
  const queryClient = useQueryClient()

  const createGoalMutation = useMutation({
    mutationFn: (data: any) => performanceApi.createGoal(data),
    onSuccess: () => {
      toast.success('Goal created')
      queryClient.invalidateQueries({ queryKey: ['goals'] })
    },
    onError: () => toast.error('Failed to create goal — saved locally'),
  })

  const handleAddGoal = () => {
    if (!newGoal.title) { toast.error('Goal title is required'); return }
    const goal = {
      id: `g${Date.now()}`, title: newGoal.title, employee: newGoal.employee || 'Unassigned',
      progress: 0, status: 'IN_PROGRESS', weight: newGoal.weight,
    }
    setGoals(prev => [goal, ...prev])
    createGoalMutation.mutate({ title: newGoal.title, employeeId: 'demo', weight: newGoal.weight, description: newGoal.title })
    setShowAddGoal(false)
    setNewGoal({ title: '', employee: '', weight: 20 })
  }

  const handleUpdateProgress = (id: string, delta: number) => {
    setGoals(prev => prev.map(g => {
      if (g.id === id) {
        const np = Math.min(100, Math.max(0, g.progress + delta))
        return { ...g, progress: np, status: np >= 100 ? 'COMPLETED' : 'IN_PROGRESS' }
      }
      return g
    }))
    toast.success(`Progress updated`)
  }

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
            <div className="stat-value">{goals.filter(g => g.status === 'IN_PROGRESS').length.toLocaleString()}</div>
            <div className="stat-change positive">{Math.round(goals.filter(g => g.progress >= 50).length / goals.length * 100)}% on track</div>
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
              <PolarGrid stroke="rgba(255,255,255,0.08)" />
              <PolarAngleAxis dataKey="metric" tick={{ fill: '#a1a1aa', fontSize: 11 }} />
              <PolarRadiusAxis tick={{ fill: '#71717a', fontSize: 10 }} domain={[0, 100]} />
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
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
              <XAxis dataKey="rating" tick={{ fill: '#a1a1aa', fontSize: 10 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#a1a1aa', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={tooltipStyle} />
              <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                {bellCurveData.map((e, i) => <Cell key={i} fill={e.color} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '4px', marginBottom: '20px', background: 'var(--bg-input)', borderRadius: '8px', padding: '4px', width: 'fit-content', border: '1px solid var(--border)' }}>
        {[
          { key: 'goals', label: 'OKR Goals' },
          { key: 'reviews', label: 'Review Cycles' },
          { key: '360', label: '360° Feedback' },
        ].map(tab => (
          <button key={tab.key} onClick={() => setActiveTab(tab.key as any)}
            style={{
              padding: '8px 20px', borderRadius: '6px', border: 'none', cursor: 'pointer',
              fontSize: '13px', fontWeight: 600, transition: 'all 0.2s',
              background: activeTab === tab.key ? 'var(--accent)' : 'transparent',
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
            <button className="btn btn-primary btn-sm" onClick={() => setShowAddGoal(!showAddGoal)}>
              <Target size={14} /> Add Goal
            </button>
          </div>
          {showAddGoal && (
            <div style={{ padding: '16px', marginBottom: '16px', background: 'var(--bg-input)', borderRadius: '8px', border: '1px solid var(--border)' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '12px', alignItems: 'end' }}>
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Goal Title</label>
                  <input className="form-input" placeholder="e.g., Increase team velocity by 20%"
                    value={newGoal.title} onChange={e => setNewGoal({ ...newGoal, title: e.target.value })} />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Assignee</label>
                  <input className="form-input" placeholder="Employee name"
                    value={newGoal.employee} onChange={e => setNewGoal({ ...newGoal, employee: e.target.value })} />
                </div>
                <button className="btn btn-primary btn-sm" onClick={handleAddGoal}>Save</button>
              </div>
            </div>
          )}
          {goals.map((g, i) => (
            <div key={g.id} style={{
              padding: '16px', borderBottom: i < goals.length - 1 ? '1px solid var(--border)' : 'none',
              display: 'flex', alignItems: 'center', gap: '16px',
            }}>
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '14px', marginBottom: '4px' }}>{g.title}</div>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{g.employee} · Weight: {g.weight}%</div>
              </div>
              <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                <button className="btn btn-secondary btn-sm" style={{ padding: '2px 8px', fontSize: '11px' }}
                  onClick={() => handleUpdateProgress(g.id, -10)}>-10</button>
                <button className="btn btn-primary btn-sm" style={{ padding: '2px 8px', fontSize: '11px' }}
                  onClick={() => handleUpdateProgress(g.id, 10)}>+10</button>
              </div>
              <div style={{ width: '200px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', marginBottom: '4px' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Progress</span>
                  <span style={{ fontWeight: 700, color: g.progress >= 80 ? '#10b981' : '#f59e0b' }}>{g.progress}%</span>
                </div>
                <div style={{ height: '6px', background: 'var(--bg-input)', borderRadius: '3px', overflow: 'hidden' }}>
                  <div style={{
                    height: '100%', borderRadius: '3px', transition: 'width 0.5s ease',
                    width: `${g.progress}%`,
                    background: g.progress >= 80 ? '#10b981' : g.progress >= 50 ? '#3b82f6' : '#f59e0b',
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
            <button className="btn btn-primary btn-sm" onClick={() => toast.success('Create cycle — coming in Phase 2')}>Create Cycle</button>
          </div>
          {reviewCycles.map((c, i) => (
            <div key={i} style={{
              padding: '20px', borderBottom: i < reviewCycles.length - 1 ? '1px solid var(--border)' : 'none',
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
                  <div style={{ height: '6px', background: 'var(--bg-input)', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', borderRadius: '3px', width: `${c.progress}%`, background: 'var(--accent)' }} />
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
              padding: '20px', borderBottom: i < reviews360.length - 1 ? '1px solid var(--border)' : 'none',
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
