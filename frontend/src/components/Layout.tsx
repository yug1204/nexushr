import { NavLink, Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useEffect, useState, useRef } from 'react'
import {
  LayoutDashboard, Users, Clock, Wallet, Target, Brain,
  Bell, Settings, Search, ChevronRight, LogOut, Briefcase, User as UserIcon
} from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'

const navItems = [
  { section: 'Overview', items: [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  ]},
  { section: 'HR Management', items: [
    { to: '/employees', icon: Users, label: 'Employees' },
    { to: '/attendance', icon: Clock, label: 'Attendance & Leave' },
    { to: '/payroll', icon: Wallet, label: 'Payroll' },
    { to: '/performance', icon: Target, label: 'Performance' },
    { to: '/recruitment', icon: Briefcase, label: 'Recruitment & ATS' },
  ]},
  { section: 'Intelligence', items: [
    { to: '/ai-insights', icon: Brain, label: 'AI Insights' },
  ]},
  { section: 'System', items: [
    { to: '/settings', icon: Settings, label: 'Settings' },
  ]},
]

export default function Layout() {
  const { user, logout, isAuthenticated } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [notifications, setNotifications] = useState(3)
  const [showProfileMenu, setShowProfileMenu] = useState(false)
  const profileMenuRef = useRef<HTMLDivElement>(null)

  // Close profile dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (profileMenuRef.current && !profileMenuRef.current.contains(e.target as Node)) {
        setShowProfileMenu(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  useEffect(() => {
    if (!isAuthenticated) return

    // Attempt real WebSocket connection or fallback to simulated events
    let socket: WebSocket | null = null
    try {
      socket = new WebSocket('ws://localhost:8083/ws/notifications')
      socket.onmessage = (event) => {
        const msg = JSON.parse(event.data)
        toast(msg.content, { icon: '🔔' })
        setNotifications(prev => prev + 1)
      }
    } catch (e) {
      console.log('Falling back to simulated WebSockets for demo')
    }

    // Simulation loop for Demo purposes
    const interval = setInterval(() => {
      const messages = [
        "Leave request approved by HR",
        "New candidate applied for Tech Lead",
        "Payroll processing completed",
        "Suresh Kumar's engagement dropped"
      ]
      const randMsg = messages[Math.floor(Math.random() * messages.length)]
      toast(randMsg, { icon: '🔔' })
      setNotifications(prev => prev + 1)
    }, 45000) // Emit event every 45s

    return () => {
      clearInterval(interval)
      socket?.close()
    }
  }, [isAuthenticated])

  const handleLogout = () => {
    setShowProfileMenu(false)
    logout()
    toast.success('Signed out')
    navigate('/login')
  }

  // Get current page name for breadcrumb
  const currentPage = navItems
    .flatMap(s => s.items)
    .find(i => location.pathname.includes(i.to.replace('/', '')))?.label || 'Dashboard'

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-icon">N</div>
          <span className="logo-text">NexusHR</span>
        </div>
        <nav className="sidebar-nav">
          {navItems.map(section => (
            <div className="nav-section" key={section.section}>
              <div className="nav-section-title">{section.section}</div>
              {section.items.map(item => (
                <NavLink key={item.to} to={item.to}
                  className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                  <item.icon className="icon" size={18} />
                  <span>{item.label}</span>
                </NavLink>
              ))}
            </div>
          ))}
        </nav>
        <div style={{ padding: '12px 16px', borderTop: '1px solid var(--border)' }}>
          {user && (
            <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '8px', padding: '0 12px' }}>
              {user.email}
            </div>
          )}
          <div className="nav-item" style={{ color: '#ef4444' }} onClick={handleLogout}>
            <LogOut className="icon" size={18} />
            <span>Sign Out</span>
          </div>
        </div>
      </aside>

      <header className="header">
        <div className="header-left">
          <div className="header-breadcrumb">
            <a href="#/dashboard">NexusHR</a>
            <ChevronRight size={12} />
            <span>{currentPage}</span>
          </div>
        </div>
        <div className="header-right">
          <div className="header-search">
            <Search className="search-icon" size={14} />
            <input type="text" placeholder="Search employees, payroll..." />
          </div>
          <button className="icon-btn" onClick={() => {toast(`${notifications} new notifications`); setNotifications(0)}}>
            <Bell size={16} />
            {notifications > 0 && (
              <span className="badge" style={{
                position: 'absolute', top: '-3px', right: '-3px',
                width: '16px', height: '16px', background: 'var(--danger)', borderRadius: '50%',
                fontSize: '9px', fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff', border: '2px solid var(--bg-body)',
              }}>{notifications}</span>
            )}
          </button>

          {/* Profile Avatar with Dropdown */}
          <div ref={profileMenuRef} style={{ position: 'relative' }}>
            <div
              className="avatar-btn"
              id="profile-avatar-btn"
              onClick={() => setShowProfileMenu(prev => !prev)}
              style={{ outline: showProfileMenu ? '2px solid var(--accent)' : undefined, outlineOffset: '2px' }}
            >
              {user?.firstName?.[0] || 'A'}{user?.lastName?.[0] || 'U'}
            </div>

            {showProfileMenu && (
              <div style={{
                position: 'absolute', top: 'calc(100% + 8px)', right: 0,
                width: '260px',
                background: 'var(--bg-card)',
                border: '1px solid var(--border)',
                borderRadius: 'var(--radius-lg)',
                boxShadow: '0 12px 40px -8px rgba(0,0,0,0.6)',
                zIndex: 200,
                animation: 'fadeSlideUp 0.2s cubic-bezier(0.16, 1, 0.3, 1) both',
                overflow: 'hidden',
              }}>
                {/* User info header */}
                <div style={{
                  padding: '16px 20px',
                  borderBottom: '1px solid var(--border)',
                  background: 'rgba(79, 70, 229, 0.05)',
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{
                      width: '40px', height: '40px', borderRadius: '50%',
                      background: 'linear-gradient(135deg, #3b82f6, #8b5cf6)',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      fontWeight: 700, fontSize: '14px', color: '#fff',
                      flexShrink: 0,
                    }}>
                      {user?.firstName?.[0] || 'A'}{user?.lastName?.[0] || 'U'}
                    </div>
                    <div style={{ minWidth: 0 }}>
                      <div style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-primary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                        {user?.firstName || 'Admin'} {user?.lastName || 'User'}
                      </div>
                      <div style={{ fontSize: '12px', color: 'var(--text-muted)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                        {user?.email || 'admin@nexushr.com'}
                      </div>
                    </div>
                  </div>
                  {user?.roles && user.roles.length > 0 && (
                    <div style={{ marginTop: '10px' }}>
                      <span className="badge badge-purple" style={{ fontSize: '10px', padding: '2px 8px' }}>
                        {user.roles[0]?.replace('ROLE_', '').replace(/_/g, ' ')}
                      </span>
                    </div>
                  )}
                </div>

                {/* Menu items */}
                <div style={{ padding: '8px' }}>
                  <button
                    id="profile-settings-btn"
                    onClick={() => { setShowProfileMenu(false); navigate('/settings'); }}
                    style={{
                      display: 'flex', alignItems: 'center', gap: '10px',
                      width: '100%', padding: '10px 12px', borderRadius: 'var(--radius-sm)',
                      background: 'transparent', border: 'none', color: 'var(--text-secondary)',
                      fontSize: '13px', fontWeight: 500, cursor: 'pointer',
                      transition: 'all 0.15s ease', textAlign: 'left',
                    }}
                    onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-hover)'; e.currentTarget.style.color = 'var(--text-primary)'; }}
                    onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-secondary)'; }}
                  >
                    <UserIcon size={16} style={{ opacity: 0.7 }} />
                    Profile & Settings
                  </button>
                  <button
                    id="profile-logout-btn"
                    onClick={handleLogout}
                    style={{
                      display: 'flex', alignItems: 'center', gap: '10px',
                      width: '100%', padding: '10px 12px', borderRadius: 'var(--radius-sm)',
                      background: 'transparent', border: 'none', color: '#ef4444',
                      fontSize: '13px', fontWeight: 500, cursor: 'pointer',
                      transition: 'all 0.15s ease', textAlign: 'left',
                    }}
                    onMouseEnter={e => { e.currentTarget.style.background = 'var(--danger-bg)'; }}
                    onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; }}
                  >
                    <LogOut size={16} style={{ opacity: 0.7 }} />
                    Sign Out
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}
