import { useState } from 'react'
import { Settings as SettingsIcon, User, Bell, Shield, Database, Globe, Palette } from 'lucide-react'

export default function SettingsPage() {
  const [activeSection, setActiveSection] = useState('profile')

  const sections = [
    { key: 'profile', label: 'Profile', icon: User },
    { key: 'notifications', label: 'Notifications', icon: Bell },
    { key: 'security', label: 'Security', icon: Shield },
    { key: 'system', label: 'System', icon: Database },
    { key: 'localization', label: 'Localization', icon: Globe },
    { key: 'appearance', label: 'Appearance', icon: Palette },
  ]

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
                background: activeSection === s.key ? '#eef2ff' : 'transparent',
                color: activeSection === s.key ? '#4f46e5' : '#4b5563',
                textAlign: 'left',
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
                  <input className="form-input" defaultValue="Admin" /></div>
                <div className="form-group"><label className="form-label">Last Name</label>
                  <input className="form-input" defaultValue="User" /></div>
                <div className="form-group"><label className="form-label">Email</label>
                  <input className="form-input" defaultValue="admin@nexushr.com" /></div>
                <div className="form-group"><label className="form-label">Phone</label>
                  <input className="form-input" defaultValue="+91-9876543210" /></div>
                <div className="form-group"><label className="form-label">Department</label>
                  <input className="form-input" defaultValue="HR" /></div>
                <div className="form-group"><label className="form-label">Designation</label>
                  <input className="form-input" defaultValue="HR Administrator" /></div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: '8px' }}>Save Changes</button>
            </div>
          )}

          {activeSection === 'notifications' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Notification Preferences</h2>
              {[
                { label: 'Leave requests', desc: 'When employees apply for leave', checked: true },
                { label: 'Payroll alerts', desc: 'Payroll run completion and errors', checked: true },
                { label: 'Performance reviews', desc: 'Review cycle updates', checked: true },
                { label: 'Attendance anomalies', desc: 'Late arrivals and absences', checked: false },
                { label: 'System updates', desc: 'Platform maintenance and releases', checked: false },
              ].map((n, i) => (
                <div key={i} style={{
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  padding: '14px 0', borderBottom: '1px solid #e5e7eb',
                }}>
                  <div>
                    <div style={{ fontWeight: 500, fontSize: '14px' }}>{n.label}</div>
                    <div style={{ fontSize: '12px', color: '#9ca3af' }}>{n.desc}</div>
                  </div>
                  <label style={{ position: 'relative', display: 'inline-block', width: '40px', height: '22px' }}>
                    <input type="checkbox" defaultChecked={n.checked}
                      style={{ opacity: 0, width: 0, height: 0 }} />
                    <span style={{
                      position: 'absolute', cursor: 'pointer', inset: 0, borderRadius: '11px',
                      background: n.checked ? '#4f46e5' : '#d1d5db', transition: '.2s',
                    }} />
                  </label>
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
              <button className="btn btn-primary">Update Password</button>

              <div style={{ marginTop: '32px', padding: '16px', background: '#f3f4f6', borderRadius: '8px' }}>
                <h3 style={{ fontSize: '14px', fontWeight: 600, marginBottom: '8px' }}>Two-Factor Authentication</h3>
                <p style={{ fontSize: '13px', color: '#6b7280', marginBottom: '12px' }}>
                  Add an extra layer of security with TOTP-based 2FA.
                </p>
                <button className="btn btn-secondary btn-sm">Enable 2FA</button>
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
                  <select className="form-input form-select"><option>English</option><option>Hindi</option></select></div>
                <div className="form-group"><label className="form-label">Timezone</label>
                  <select className="form-input form-select"><option>Asia/Kolkata (IST, +05:30)</option></select></div>
                <div className="form-group"><label className="form-label">Date Format</label>
                  <select className="form-input form-select"><option>DD/MM/YYYY</option><option>MM/DD/YYYY</option></select></div>
                <div className="form-group"><label className="form-label">Currency</label>
                  <select className="form-input form-select"><option>₹ INR (Indian Rupee)</option></select></div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: '8px' }}>Save Preferences</button>
            </div>
          )}

          {activeSection === 'appearance' && (
            <div>
              <h2 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '20px' }}>Appearance</h2>
              <div className="form-group">
                <label className="form-label">Theme</label>
                <div style={{ display: 'flex', gap: '12px', marginTop: '8px' }}>
                  {['Light', 'Dark', 'System'].map(t => (
                    <button key={t} className={`btn ${t === 'Light' ? 'btn-primary' : 'btn-secondary'} btn-sm`}>
                      {t}
                    </button>
                  ))}
                </div>
              </div>
              <div className="form-group" style={{ marginTop: '20px' }}>
                <label className="form-label">Accent Color</label>
                <div style={{ display: 'flex', gap: '8px', marginTop: '8px' }}>
                  {['#4f46e5', '#2563eb', '#059669', '#d97706', '#dc2626', '#7c3aed'].map(c => (
                    <div key={c} style={{
                      width: '32px', height: '32px', borderRadius: '8px', background: c, cursor: 'pointer',
                      border: c === '#4f46e5' ? '3px solid #111827' : '2px solid #e5e7eb',
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
