import { NavLink, Outlet } from 'react-router-dom'
import {
  LayoutDashboard, Users, Clock, Wallet, Target, Brain,
  Bell, Settings, Search, Moon, ChevronRight, LogOut
} from 'lucide-react'
import { useAuthStore } from '../store/authStore'

const navItems = [
  { section: 'Overview', items: [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  ]},
  { section: 'HR Management', items: [
    { to: '/employees', icon: Users, label: 'Employees' },
    { to: '/attendance', icon: Clock, label: 'Attendance & Leave' },
    { to: '/payroll', icon: Wallet, label: 'Payroll' },
    { to: '/performance', icon: Target, label: 'Performance' },
  ]},
  { section: 'Intelligence', items: [
    { to: '/ai-insights', icon: Brain, label: 'AI Insights' },
  ]},
  { section: 'System', items: [
    { to: '/settings', icon: Settings, label: 'Settings' },
  ]},
]

export default function Layout() {
  const { user } = useAuthStore()

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
        <div style={{ padding: '12px 8px', borderTop: '1px solid var(--border)' }}>
          <div className="nav-item" style={{ color: '#dc2626' }}>
            <LogOut className="icon" size={18} />
            <span>Sign Out</span>
          </div>
        </div>
      </aside>

      <header className="header">
        <div className="header-left">
          <div className="header-breadcrumb">
            <a href="/">NexusHR</a>
            <ChevronRight size={12} />
            <span>Dashboard</span>
          </div>
        </div>
        <div className="header-right">
          <div className="header-search">
            <Search className="search-icon" size={14} />
            <input type="text" placeholder="Search employees, payroll..." />
          </div>
          <button className="icon-btn"><Bell size={16} /><span className="badge">3</span></button>
          <div className="avatar-btn">{user?.firstName?.[0]}{user?.lastName?.[0]}</div>
        </div>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}
