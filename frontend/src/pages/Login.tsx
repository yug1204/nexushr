import { useState } from 'react'
import { Mail, Lock, Eye, EyeOff } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'

export default function Login() {
  const [email, setEmail] = useState('admin@nexushr.com')
  const [password, setPassword] = useState('demo1234')
  const [showPass, setShowPass] = useState(false)
  const navigate = useNavigate()
  const login = useAuthStore(s => s.login)

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault()
    login(
      { id: 'demo', email, firstName: 'Admin', lastName: 'User', roles: ['ROLE_HR_ADMIN'] },
      'demo-token'
    )
    navigate('/dashboard')
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: '#f8f9fb',
    }}>
      <div style={{
        width: '100%', maxWidth: '400px', padding: '36px',
        background: '#fff', borderRadius: '12px',
        border: '1px solid #e5e7eb', boxShadow: '0 4px 12px rgba(0,0,0,.08)',
      }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            width: '48px', height: '48px', borderRadius: '10px',
            background: '#4f46e5', display: 'flex', alignItems: 'center',
            justifyContent: 'center', margin: '0 auto 14px', fontSize: '20px', fontWeight: 700, color: '#fff',
          }}>N</div>
          <h1 style={{ fontSize: '22px', fontWeight: 700, color: '#111827', marginBottom: '4px' }}>Welcome to NexusHR</h1>
          <p style={{ color: '#9ca3af', fontSize: '13px' }}>
            AI-Enabled Enterprise HR Platform
          </p>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#9ca3af' }} />
              <input className="form-input" style={{ paddingLeft: '40px' }}
                type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="admin@nexushr.com" />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#9ca3af' }} />
              <input className="form-input" style={{ paddingLeft: '40px', paddingRight: '40px' }}
                type={showPass ? 'text' : 'password'} value={password} onChange={e => setPassword(e.target.value)} />
              <button type="button" onClick={() => setShowPass(!showPass)}
                style={{ position: 'absolute', right: '12px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', color: '#9ca3af', cursor: 'pointer' }}>
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>
          <button type="submit" className="btn btn-primary btn-lg" style={{ width: '100%', marginTop: '8px' }}>
            Sign In
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '16px', fontSize: '12px', color: '#9ca3af' }}>
          Demo credentials: admin@nexushr.com / demo1234
        </p>
      </div>
    </div>
  )
}
