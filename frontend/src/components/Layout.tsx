import { NavLink, Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'
import {
  LayoutDashboard, Users, Clock, Wallet, Target, Brain,
  Bell, Settings, Search, ChevronRight, LogOut, Briefcase
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
          <div className="avatar-btn">{user?.firstName?.[0] || 'A'}{user?.lastName?.[0] || 'U'}</div>
        </div>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}
