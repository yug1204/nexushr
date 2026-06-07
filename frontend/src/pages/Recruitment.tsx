import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Briefcase, Users, Plus, CheckCircle, Clock, XCircle, Search, UserPlus
} from 'lucide-react'
import { recruitmentApi } from '../api/client'
import toast from 'react-hot-toast'

export default function Recruitment() {
  const [activeTab, setActiveTab] = useState<'requisitions' | 'pipeline'>('requisitions')
  const [selectedReq, setSelectedReq] = useState<string | null>(null)
  const queryClient = useQueryClient()

  // Fetch Requisitions
  const { data: requisitions = [] } = useQuery({
    queryKey: ['requisitions'],
    queryFn: async () => {
      try {
        const res = await recruitmentApi.getOpenRequisitions()
        return res.data.data || []
      } catch (e) { return [] }
    }
  })

  // Fetch Candidates for selected requisition
  const { data: candidates = [] } = useQuery({
    queryKey: ['candidates', selectedReq],
    queryFn: async () => {
      if (!selectedReq) return []
      try {
        const res = await recruitmentApi.getCandidates(selectedReq)
        return res.data.data || []
      } catch (e) { return [] }
    },
    enabled: !!selectedReq
  })

  const advanceCandidate = useMutation({
    mutationFn: ({ id, stage }: { id: string, stage: string }) => recruitmentApi.advanceCandidate(id, stage),
    onSuccess: () => {
      toast.success('Candidate advanced successfully')
      queryClient.invalidateQueries({ queryKey: ['candidates', selectedReq] })
    }
  })

  const handleAdvance = (id: string, currentStage: string) => {
    const stages = ['APPLIED', 'SCREENING', 'PHONE_SCREEN', 'TECHNICAL_ROUND', 'HR_ROUND', 'OFFER_PENDING', 'OFFER_SENT', 'OFFER_ACCEPTED']
    const idx = stages.indexOf(currentStage)
    if (idx < stages.length - 1) {
      advanceCandidate.mutate({ id, stage: stages[idx + 1] })
    }
  }

  // Group candidates by stage for Kanban board
  const kanbanStages = ['APPLIED', 'SCREENING', 'TECHNICAL_ROUND', 'HR_ROUND', 'OFFER_SENT']
  const groupedCandidates = kanbanStages.reduce((acc, stage) => {
    acc[stage] = candidates.filter((c: any) => c.pipelineStage === stage)
    return acc
  }, {} as Record<string, any[]>)

  return (
    <div className="page-container">
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }} className="animate-in">
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 800 }}>Recruitment & ATS</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
            Manage job requisitions, candidate pipelines, and offers
          </p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={() => toast.success('New requisition form opening...')}>
          <Plus size={16} /> New Requisition
        </button>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '4px', marginBottom: '20px', background: 'var(--bg-input)', borderRadius: '8px', padding: '4px', width: 'fit-content', border: '1px solid var(--border)' }}>
        <button onClick={() => setActiveTab('requisitions')}
          style={{
            padding: '8px 20px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '13px', fontWeight: 600, transition: 'all 0.2s',
            background: activeTab === 'requisitions' ? 'var(--accent)' : 'transparent', color: activeTab === 'requisitions' ? 'white' : 'var(--text-secondary)'
          }}>
          Job Requisitions
        </button>
        <button onClick={() => { setActiveTab('pipeline'); if (!selectedReq && requisitions.length > 0) setSelectedReq(requisitions[0].id) }}
          style={{
            padding: '8px 20px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '13px', fontWeight: 600, transition: 'all 0.2s',
            background: activeTab === 'pipeline' ? 'var(--accent)' : 'transparent', color: activeTab === 'pipeline' ? 'white' : 'var(--text-secondary)'
          }}>
          Candidate Pipeline
        </button>
      </div>

      {/* Requisitions View */}
      {activeTab === 'requisitions' && (
        <div className="card animate-in">
          <table className="data-table">
            <thead>
              <tr>
                <th>Job Title</th>
                <th>Department</th>
                <th>Type</th>
                <th>Experience</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requisitions.length === 0 ? (
                <tr><td colSpan={6} style={{ textAlign: 'center', padding: '20px', color: 'var(--text-muted)' }}>No open requisitions found.</td></tr>
              ) : (
                requisitions.map((req: any) => (
                  <tr key={req.id}>
                    <td style={{ fontWeight: 600 }}>{req.title}</td>
                    <td>{req.department}</td>
                    <td>{req.employmentType}</td>
                    <td>{req.minExperience}+ yrs</td>
                    <td><span className="badge badge-success">{req.status}</span></td>
                    <td>
                      <button className="btn btn-secondary btn-sm" onClick={() => { setSelectedReq(req.id); setActiveTab('pipeline') }}>
                        View Pipeline
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Pipeline Kanban View */}
      {activeTab === 'pipeline' && (
        <div className="animate-in">
          <div style={{ marginBottom: '16px', display: 'flex', gap: '12px', alignItems: 'center' }}>
            <label style={{ fontSize: '13px', fontWeight: 600 }}>Select Requisition:</label>
            <select className="form-input" style={{ width: '300px' }} value={selectedReq || ''} onChange={(e) => setSelectedReq(e.target.value)}>
              {requisitions.map((req: any) => <option key={req.id} value={req.id}>{req.title} ({req.department})</option>)}
            </select>
          </div>

          <div style={{ display: 'flex', gap: '16px', overflowX: 'auto', paddingBottom: '16px' }}>
            {kanbanStages.map(stage => (
              <div key={stage} style={{
                flex: '0 0 280px', background: 'var(--bg-card)', borderRadius: '10px',
                border: '1px solid var(--border)', padding: '12px', minHeight: '400px'
              }}>
                <div style={{
                  fontSize: '12px', fontWeight: 700, color: 'var(--text-secondary)',
                  marginBottom: '12px', display: 'flex', justifyContent: 'space-between'
                }}>
                  {stage.replace('_', ' ')}
                  <span className="badge" style={{ background: 'var(--bg-input)' }}>{groupedCandidates[stage]?.length || 0}</span>
                </div>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                  {groupedCandidates[stage]?.map((candidate: any) => (
                    <div key={candidate.id} style={{
                      background: 'var(--bg-input)', padding: '12px', borderRadius: '8px',
                      border: '1px solid rgba(255,255,255,0.05)'
                    }}>
                      <div style={{ fontWeight: 600, fontSize: '13px', color: 'var(--text-primary)', marginBottom: '4px' }}>
                        {candidate.firstName} {candidate.lastName}
                      </div>
                      <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '8px' }}>
                        Exp: {candidate.totalExperience} yrs · AI Score: {candidate.aiMatchScore}/100
                      </div>
                      {stage !== 'OFFER_SENT' && (
                        <button className="btn btn-secondary btn-sm" style={{ width: '100%', fontSize: '11px', padding: '4px' }}
                          onClick={() => handleAdvance(candidate.id, candidate.pipelineStage)}>
                          Advance →
                        </button>
                      )}
                    </div>
                  ))}
                  {(!groupedCandidates[stage] || groupedCandidates[stage].length === 0) && (
                    <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '12px', padding: '20px 0' }}>
                      No candidates
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
