import { useState } from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { colors, fonts } from '../constants/theme'

const navItems = [
    { path: '/dashboard', label: 'DASHBOARD', icon: '◈' },
    { path: '/plan', label: 'WEEKLY PLAN', icon: '◫' },
    { path: '/scores', label: 'SCORES', icon: '◉' },
    { path: '/profile', label: 'PROFILE', icon: '◎' },
]

export default function Layout() {
    const { soldier, logout } = useSoldier()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    return (
        <div className="flex min-h-screen" style={{ backgroundColor: colors.bgPrimary }}>

            {/* Sidebar */}
            <div
                className="flex flex-col w-56 shrink-0"
                style={{
                    backgroundColor: colors.bgSecondary,
                    borderRight: `1px solid ${colors.border}`,
                }}
            >
                {/* Logo */}
                <div className="px-6 pt-8 pb-6" style={{ borderBottom: `1px solid ${colors.border}` }}>
                    <h1 style={{
                        fontFamily: fonts.heading,
                        fontSize: '1.6rem',
                        color: colors.accentGold,
                        letterSpacing: '0.08em',
                        lineHeight: 1.1,
                    }}>
                        ROAD<br />TO 500
                    </h1>
                    <div style={{ width: '24px', height: '2px', backgroundColor: colors.accentGold, marginTop: '6px' }} />
                </div>

                {/* Soldier info */}
                <div className="px-6 py-4" style={{ borderBottom: `1px solid ${colors.border}` }}>
                    <p style={{ color: colors.textSecondary, fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.15em' }}>
                        LOGGED IN AS
                    </p>
                    <p style={{ color: colors.textPrimary, fontFamily: fonts.condensed, fontSize: '0.95rem', fontWeight: 600, marginTop: '2px' }}>
                        {soldier?.name || '—'}
                    </p>
                </div>

                {/* Nav */}
                <nav className="flex-1 px-3 py-4">
                    {navItems.map(item => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            style={({ isActive }) => ({
                                display: 'flex',
                                alignItems: 'center',
                                gap: '10px',
                                padding: '10px 12px',
                                borderRadius: '4px',
                                marginBottom: '2px',
                                textDecoration: 'none',
                                fontFamily: fonts.condensed,
                                fontSize: '0.85rem',
                                letterSpacing: '0.1em',
                                fontWeight: 600,
                                color: isActive ? colors.accentGold : colors.textSecondary,
                                backgroundColor: isActive ? `${colors.accentGold}15` : 'transparent',
                                borderLeft: isActive ? `2px solid ${colors.accentGold}` : '2px solid transparent',
                                transition: 'all 0.15s',
                            })}
                        >
                            <span style={{ fontSize: '0.9rem' }}>{item.icon}</span>
                            {item.label}
                        </NavLink>
                    ))}
                </nav>

                {/* Logout */}
                <div className="px-3 pb-6">
                    <button
                        onClick={handleLogout}
                        className="w-full py-2 rounded text-left px-3"
                        style={{
                            backgroundColor: 'transparent',
                            border: `1px solid ${colors.border}`,
                            color: colors.textSecondary,
                            fontFamily: fonts.condensed,
                            fontSize: '0.8rem',
                            letterSpacing: '0.1em',
                            cursor: 'pointer',
                        }}
                        onMouseEnter={e => e.currentTarget.style.borderColor = colors.danger}
                        onMouseLeave={e => e.currentTarget.style.borderColor = colors.border}
                    >
                        ← SIGN OUT
                    </button>
                </div>
            </div>

            {/* Main content */}
            <div className="flex-1 overflow-auto" style={{ backgroundColor: colors.bgPrimary }}>
                <Outlet />
            </div>

        </div>
    )
}