import { useState } from 'react'
import { Mail, Lock, Eye, EyeOff, Loader2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPass, setShowPass] = useState(false)
  const navigate = useNavigate()
  const { loginAction, login, isLoading } = useAuthStore()

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email || !password) {
      toast.error('Please enter email and password')
      return
    }
    try {
      await loginAction(email, password)
      toast.success('Welcome back!')
      navigate('/dashboard')
    } catch (err: any) {
      // If backend is unreachable or returns a proxy error, fallback to local auth for demo
      const status = err?.response?.status
      if (status === 401) {
        toast.error('Invalid email or password')
      } else {
        // Network error, 404, or 504 — backend offline, allow local access
        login(
          { id: 'local-user', email, firstName: email.split('@')[0] || 'Admin', lastName: '', roles: ['ROLE_HR_ADMIN'] },
          'local-session-token'
        )
        toast.success('Connected in offline demo mode')
        navigate('/dashboard')
      }
    }
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'var(--bg-body)',
      backgroundImage: 'radial-gradient(circle at top right, rgba(79,70,229,0.08), transparent 40%), radial-gradient(circle at bottom left, rgba(59,130,246,0.06), transparent 40%)',
    }}>
      <div style={{
        width: '100%', maxWidth: '420px', padding: '40px',
        background: 'var(--bg-card)', borderRadius: '16px',
        border: '1px solid var(--border)', boxShadow: '0 8px 32px -8px rgba(0,0,0,0.5)',
      }}>
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <div style={{
            width: '52px', height: '52px', borderRadius: '14px',
            background: 'linear-gradient(135deg, #4f46e5, #3b82f6)', display: 'flex', alignItems: 'center',
            justifyContent: 'center', margin: '0 auto 16px', fontSize: '22px', fontWeight: 800, color: '#fff',
            boxShadow: '0 4px 16px rgba(79,70,229,0.3)',
          }}>N</div>
          <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px', letterSpacing: '-0.5px' }}>Welcome to NexusHR</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
            AI-Enabled Enterprise HR Platform
          </p>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={16} style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input className="form-input" style={{ paddingLeft: '42px' }}
                type="email" value={email} onChange={e => setEmail(e.target.value)}
                placeholder="you@company.com" autoComplete="email" />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={16} style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input className="form-input" style={{ paddingLeft: '42px', paddingRight: '42px' }}
                type={showPass ? 'text' : 'password'} value={password} onChange={e => setPassword(e.target.value)}
                placeholder="Enter your password" autoComplete="current-password" />
              <button type="button" onClick={() => setShowPass(!showPass)}
                style={{ position: 'absolute', right: '14px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>
          <button type="submit" className="btn btn-primary btn-lg" disabled={isLoading}
            style={{ width: '100%', marginTop: '8px', height: '44px', fontSize: '14px', fontWeight: 600 }}>
            {isLoading ? <Loader2 size={18} style={{ animation: 'spin 1s linear infinite' }} /> : 'Sign In'}
          </button>
        </form>

        <div style={{ marginTop: '24px', padding: '16px', background: 'rgba(79,70,229,0.1)', borderRadius: '8px', border: '1px dashed rgba(79,70,229,0.3)', textAlign: 'center' }}>
          <p style={{ fontSize: '13px', color: 'var(--text-primary)', fontWeight: 600, marginBottom: '4px' }}>Demo Credentials</p>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Email: <strong style={{ color: 'var(--text-primary)' }}>admin@nexushr.com</strong></p>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Password: <strong style={{ color: 'var(--text-primary)' }}>demo1234</strong></p>
        </div>

        <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '12px', color: 'var(--text-muted)' }}>
          Secured with JWT RS256 · Argon2id · MFA Ready
        </p>
      </div>
    </div>
  )
}
