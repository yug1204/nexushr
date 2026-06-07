import { useState } from 'react'
import { User, Bell, Shield, Database, Globe, Palette } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'

export default function SettingsPage() {
  const [activeSection, setActiveSection] = useState('profile')
  const { user } = useAuthStore()
  const [profile, setProfile] = useState({
    firstName: user?.firstName || '', lastName: user?.lastName || '',
    email: user?.email || '', phone: '+91-9876543210',
    department: 'HR', designation: 'HR Administrator',
  })
  const [notifications, setNotifications] = useState([
    { label: 'Leave requests', desc: 'When employees apply for leave', checked: true },
    { label: 'Payroll alerts', desc: 'Payroll run completion and errors', checked: true },
    { label: 'Performance reviews', desc: 'Review cycle updates', checked: true },
    { label: 'Attendance anomalies', desc: 'Late arrivals and absences', checked: false },
    { label: 'System updates', desc: 'Platform maintenance and releases', checked: false },
  ])
  const [theme, setTheme] = useState('Dark')

  const sections = [
    { key: 'profile', label: 'Profile', icon: User },
    { key: 'notifications', label: 'Notifications', icon: Bell },
    { key: 'security', label: 'Security', icon: Shield },
    { key: 'system', label: 'System', icon: Database },
    { key: 'localization', label: 'Localization', icon: Globe },
    { key: 'appearance', label: 'Appearance', icon: Palette },
  ]

  const handleSaveProfile = () => {
    toast.success('Profile saved successfully')
  }

  const handleUpdatePassword = () => {
    toast.success('Password updated')
  }

  const toggleNotification = (idx: number) => {
    setNotifications(prev => prev.map((n, i) => i === idx ? { ...n, checked: !n.checked } : n))
    toast.success('Notification preference updated')
  }

  return (
    <div className="page-container">
      <div style={{ marginBottom: '24px' }} className="animate-in">
        <h1 style={{ fontSize: '24px', fontWeight: 700 }}>Settings</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
          Manage platform configuration and preferences
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '220px 1fr', gap: '20px' }}>
        {/* Settings Nav */}
        <div className="card" style={{ padding: '8px', height: 'fit-content' }}>
          {sections.map(s => (
            <button key={s.key}
              onClick={() => setActiveSection(s.key)}
              style={{
                display: 'flex', alignItems: 'center', gap: '8px',
                width: '100%', padding: '10px 12px', borderRadius: '6px',
                border: 'none', cursor: 'pointer', fontSize: '13px', fontWeight: 500,
                background: activeSection === s.key ? 'var(--accent-light)' : 'transparent',
                color: activeSection === s.key ? 'var(--accent)' : 'var(--text-secondary)',
                textAlign: 'left' as const,
              }}>
              <s.icon size={16} />
              {s.label}
            </button>
          ))}
        </div>

        {/* Settings Content */}
        <div className="card animate-in">
          {activeSection === 'profile' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Profile Settings</h2>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group"><label className="form-label">First Name</label>
                  <input className="form-input" value={profile.firstName} onChange={e => setProfile({ ...profile, firstName: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Last Name</label>
                  <input className="form-input" value={profile.lastName} onChange={e => setProfile({ ...profile, lastName: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Email</label>
                  <input className="form-input" value={profile.email} onChange={e => setProfile({ ...profile, email: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Phone</label>
                  <input className="form-input" value={profile.phone} onChange={e => setProfile({ ...profile, phone: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Department</label>
                  <input className="form-input" value={profile.department} onChange={e => setProfile({ ...profile, department: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Designation</label>
                  <input className="form-input" value={profile.designation} onChange={e => setProfile({ ...profile, designation: e.target.value })} /></div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: '8px' }} onClick={handleSaveProfile}>Save Changes</button>
            </div>
          )}

          {activeSection === 'notifications' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Notification Preferences</h2>
              {notifications.map((n, i) => (
                <div key={i} style={{
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  padding: '14px 0', borderBottom: '1px solid var(--border)',
                }}>
                  <div>
                    <div style={{ fontWeight: 500, fontSize: '14px' }}>{n.label}</div>
                    <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{n.desc}</div>
                  </div>
                  <button onClick={() => toggleNotification(i)} style={{
                    width: '44px', height: '24px', borderRadius: '12px', border: 'none', cursor: 'pointer',
                    background: n.checked ? 'var(--accent)' : 'rgba(255,255,255,0.1)',
                    position: 'relative' as const, transition: 'background 0.2s',
                  }}>
                    <div style={{
                      width: '18px', height: '18px', borderRadius: '50%', background: '#fff',
                      position: 'absolute' as const, top: '3px', transition: 'left 0.2s',
                      left: n.checked ? '23px' : '3px',
                    }} />
                  </button>
                </div>
              ))}
            </div>
          )}

          {activeSection === 'security' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Security Settings</h2>
              <div className="form-group"><label className="form-label">Current Password</label>
                <input className="form-input" type="password" placeholder="Enter current password" /></div>
              <div className="form-group"><label className="form-label">New Password</label>
                <input className="form-input" type="password" placeholder="Enter new password" /></div>
              <div className="form-group"><label className="form-label">Confirm Password</label>
                <input className="form-input" type="password" placeholder="Confirm new password" /></div>
              <button className="btn btn-primary" onClick={handleUpdatePassword}>Update Password</button>

              <div style={{ marginTop: '32px', padding: '16px', background: 'var(--bg-input)', borderRadius: '8px', border: '1px solid var(--border)' }}>
                <h3 style={{ fontSize: '14px', fontWeight: 600, marginBottom: '8px' }}>Two-Factor Authentication</h3>
                <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginBottom: '12px' }}>
                  Add an extra layer of security with TOTP-based 2FA.
                </p>
                <button className="btn btn-secondary btn-sm" onClick={() => toast.success('2FA setup initiated')}>Enable 2FA</button>
              </div>
            </div>
          )}

          {activeSection === 'system' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>System Configuration</h2>
              <table className="data-table">
                <thead><tr><th>Property</th><th>Value</th><th>Status</th></tr></thead>
                <tbody>
                  {[
                    { prop: 'Database', value: 'PostgreSQL 17', status: 'Connected' },
                    { prop: 'Cache', value: 'Redis 7.2', status: 'Connected' },
                    { prop: 'Message Broker', value: 'Apache Kafka 3.7', status: 'Connected' },
                    { prop: 'Search Engine', value: 'Elasticsearch 8.15', status: 'Connected' },
                    { prop: 'Identity Provider', value: 'Keycloak 25', status: 'Connected' },
                    { prop: 'Java Runtime', value: 'OpenJDK 21 (Temurin)', status: 'Active' },
                    { prop: 'Spring Boot', value: '3.3.x', status: 'Active' },
                  ].map((r, i) => (
                    <tr key={i}>
                      <td style={{ fontWeight: 500 }}>{r.prop}</td>
                      <td>{r.value}</td>
                      <td><span className="badge badge-success">{r.status}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {activeSection === 'localization' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Localization</h2>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group"><label className="form-label">Language</label>
                  <select className="form-input"><option>English</option><option>Hindi</option></select></div>
                <div className="form-group"><label className="form-label">Timezone</label>
                  <select className="form-input"><option>Asia/Kolkata (IST, +05:30)</option></select></div>
                <div className="form-group"><label className="form-label">Date Format</label>
                  <select className="form-input"><option>DD/MM/YYYY</option><option>MM/DD/YYYY</option></select></div>
                <div className="form-group"><label className="form-label">Currency</label>
                  <select className="form-input"><option>₹ INR (Indian Rupee)</option></select></div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: '8px' }} onClick={() => toast.success('Preferences saved')}>Save Preferences</button>
            </div>
          )}

          {activeSection === 'appearance' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Appearance</h2>
              <div className="form-group">
                <label className="form-label">Theme</label>
                <div style={{ display: 'flex', gap: '12px', marginTop: '8px' }}>
                  {['Light', 'Dark', 'System'].map(t => (
                    <button key={t} className={`btn ${theme === t ? 'btn-primary' : 'btn-secondary'} btn-sm`}
                      onClick={() => { setTheme(t); toast.success(`Theme set to ${t}`) }}>
                      {t}
                    </button>
                  ))}
                </div>
              </div>
              <div className="form-group" style={{ marginTop: '20px' }}>
                <label className="form-label">Accent Color</label>
                <div style={{ display: 'flex', gap: '8px', marginTop: '8px' }}>
                  {['#4f46e5', '#2563eb', '#059669', '#d97706', '#dc2626', '#7c3aed'].map(c => (
                    <div key={c} onClick={() => toast.success('Accent color updated')} style={{
                      width: '32px', height: '32px', borderRadius: '8px', background: c, cursor: 'pointer',
                      border: c === '#4f46e5' ? '3px solid var(--text-primary)' : '2px solid var(--border)',
                    }} />
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
