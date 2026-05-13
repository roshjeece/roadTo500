import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router'
import { useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup'
import { useSoldier } from '../context/SoldierContext'
import { getAllSoldiers, createSoldier, deleteSoldier } from '../api/api'
import { colors, fonts } from '../constants/theme'

const createSchema = yup.object({
    name: yup.string().required('Name is required'),
    dob: yup.string().required('Date of birth is required'),
    gender: yup.string().required('Gender is required'),
    mos: yup.string().required('MOS is required'),
})

export default function LoginPage() {
    const navigate = useNavigate()
    const { login } = useSoldier()
    const [soldiers, setSoldiers] = useState([])
    const [showCreate, setShowCreate] = useState(false)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(createSchema)
    })

    useEffect(() => {
        getAllSoldiers()
            .then(res => setSoldiers(res.data))
            .catch(() => setError('Could not connect to backend'))
    }, [])

    const [confirmDelete, setConfirmDelete] = useState(null) // soldier object pending deletion
    const [deleting, setDeleting] = useState(false)

    const handleSelect = (soldier) => {
        login(soldier)
        navigate('/dashboard')
    }

    const handleDeleteConfirm = async () => {
        if (!confirmDelete) return
        setDeleting(true)
        try {
            await deleteSoldier(confirmDelete.id)
            setSoldiers(prev => prev.filter(s => s.id !== confirmDelete.id))
            setConfirmDelete(null)
        } catch (e) {
            setError('Failed to delete soldier')
        } finally {
            setDeleting(false)
        }
    }

    const onCreateSubmit = async (data) => {
        setLoading(true)
        setError(null)
        try {
            const res = await createSoldier(data)
            login(res.data)
            navigate('/onboarding')
        } catch (e) {
            setError(e.response?.data?.message || 'Failed to create soldier')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div
            className="min-h-screen flex items-center justify-center"
            style={{ backgroundColor: colors.bgPrimary, fontFamily: fonts.body }}
        >
            {/* Background grid */}
            <div className="absolute inset-0 opacity-5" style={{
                backgroundImage: `linear-gradient(${colors.accentGold} 1px, transparent 1px),
                          linear-gradient(90deg, ${colors.accentGold} 1px, transparent 1px)`,
                backgroundSize: '40px 40px'
            }} />

            <div className="relative z-10 w-full max-w-2xl px-6">

                {/* Header */}
                <div className="text-center mb-12">
                    <h1 style={{
                        fontFamily: fonts.heading,
                        fontSize: '4rem',
                        color: colors.accentGold,
                        letterSpacing: '0.1em',
                        lineHeight: 1,
                    }}>
                        ROAD TO 500
                    </h1>
                    <p style={{ color: colors.textSecondary, fontFamily: fonts.condensed, letterSpacing: '0.2em', fontSize: '0.85rem' }}>
                        ARMY FITNESS TEST — PERFORMANCE TRACKING
                    </p>
                    <div style={{ width: '60px', height: '2px', backgroundColor: colors.accentGold, margin: '12px auto 0' }} />
                </div>

                {error && (
                    <div className="mb-4 px-4 py-3 rounded" style={{ backgroundColor: colors.dangerDim, border: `1px solid ${colors.danger}`, color: colors.danger }}>
                        {error}
                    </div>
                )}

                {!showCreate ? (
                    <div>
                        <p style={{ color: colors.textSecondary, fontFamily: fonts.condensed, letterSpacing: '0.15em', fontSize: '0.8rem', marginBottom: '12px' }}>
                            SELECT SOLDIER
                        </p>

                        <div className="space-y-2 mb-6">
                            {soldiers.length === 0 && (
                                <p style={{ color: colors.textSecondary }}>No soldiers found.</p>
                            )}
                            {soldiers.map(s => (
                                <div key={s.id} className="flex items-center gap-2">
                                    <button
                                        onClick={() => handleSelect(s)}
                                        className="flex-1 text-left px-5 py-4 rounded transition-all duration-150"
                                        style={{
                                            backgroundColor: colors.bgCard,
                                            border: `1px solid ${colors.border}`,
                                            color: colors.textPrimary,
                                            fontFamily: fonts.condensed,
                                            fontSize: '1rem',
                                            letterSpacing: '0.05em',
                                        }}
                                        onMouseEnter={e => {
                                            e.currentTarget.style.borderColor = colors.accentGold
                                            e.currentTarget.style.backgroundColor = colors.bgCardHover
                                        }}
                                        onMouseLeave={e => {
                                            e.currentTarget.style.borderColor = colors.border
                                            e.currentTarget.style.backgroundColor = colors.bgCard
                                        }}
                                    >
                                        <span style={{ color: colors.accentGold, fontFamily: fonts.mono, fontSize: '0.75rem', marginRight: '12px' }}>
                                            #{String(s.id).padStart(3, '0')}
                                        </span>
                                        {s.name}
                                        <span style={{ color: colors.textSecondary, fontSize: '0.8rem', marginLeft: '8px' }}>
                                            {s.mos}
                                        </span>
                                    </button>
                                    <button
                                        onClick={() => setConfirmDelete(s)}
                                        title="Delete soldier"
                                        style={{
                                            flexShrink: 0,
                                            width: '40px',
                                            height: '40px',
                                            borderRadius: '4px',
                                            backgroundColor: 'transparent',
                                            border: `1px solid ${colors.border}`,
                                            color: colors.textSecondary,
                                            cursor: 'pointer',
                                            fontSize: '0.85rem',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            transition: 'all 0.15s',
                                        }}
                                        onMouseEnter={e => {
                                            e.currentTarget.style.borderColor = colors.danger
                                            e.currentTarget.style.color = colors.danger
                                            e.currentTarget.style.backgroundColor = `${colors.danger}15`
                                        }}
                                        onMouseLeave={e => {
                                            e.currentTarget.style.borderColor = colors.border
                                            e.currentTarget.style.color = colors.textSecondary
                                            e.currentTarget.style.backgroundColor = 'transparent'
                                        }}
                                    >
                                        ✕
                                    </button>
                                </div>
                            ))}
                        </div>

                        {/* Confirm delete modal */}
                        {confirmDelete && (
                            <div
                                className="fixed inset-0 flex items-center justify-center z-50"
                                style={{ backgroundColor: 'rgba(0,0,0,0.7)' }}
                                onClick={() => !deleting && setConfirmDelete(null)}
                            >
                                <div
                                    className="p-8 rounded max-w-sm w-full mx-4"
                                    style={{
                                        backgroundColor: colors.bgSecondary,
                                        border: `1px solid ${colors.danger}`,
                                    }}
                                    onClick={e => e.stopPropagation()}
                                >
                                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.danger, marginBottom: '8px' }}>
                                        CONFIRM DELETE
                                    </p>
                                    <p style={{ fontFamily: fonts.heading, fontSize: '1.6rem', color: colors.textPrimary, marginBottom: '6px' }}>
                                        {confirmDelete.name}
                                    </p>
                                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginBottom: '24px', lineHeight: 1.5 }}>
                                        This will permanently delete the soldier and all associated data. This cannot be undone.
                                    </p>
                                    <div className="flex gap-3">
                                        <button
                                            onClick={() => setConfirmDelete(null)}
                                            disabled={deleting}
                                            style={{
                                                flex: 1,
                                                padding: '10px',
                                                borderRadius: '4px',
                                                backgroundColor: 'transparent',
                                                border: `1px solid ${colors.border}`,
                                                color: colors.textSecondary,
                                                fontFamily: fonts.condensed,
                                                letterSpacing: '0.1em',
                                                cursor: 'pointer',
                                            }}
                                        >
                                            CANCEL
                                        </button>
                                        <button
                                            onClick={handleDeleteConfirm}
                                            disabled={deleting}
                                            style={{
                                                flex: 1,
                                                padding: '10px',
                                                borderRadius: '4px',
                                                backgroundColor: colors.danger,
                                                border: 'none',
                                                color: '#fff',
                                                fontFamily: fonts.condensed,
                                                fontWeight: 700,
                                                letterSpacing: '0.1em',
                                                cursor: 'pointer',
                                                opacity: deleting ? 0.6 : 1,
                                            }}
                                        >
                                            {deleting ? 'DELETING...' : 'DELETE'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}

                        <button
                            onClick={() => setShowCreate(true)}
                            className="w-full py-3 rounded transition-all duration-150"
                            style={{
                                backgroundColor: 'transparent',
                                border: `1px dashed ${colors.accentGoldDim}`,
                                color: colors.accentGold,
                                fontFamily: fonts.condensed,
                                letterSpacing: '0.1em',
                                fontSize: '0.9rem',
                            }}
                            onMouseEnter={e => e.currentTarget.style.borderColor = colors.accentGold}
                            onMouseLeave={e => e.currentTarget.style.borderColor = colors.accentGoldDim}
                        >
                            + CREATE NEW SOLDIER
                        </button>
                    </div>
                ) : (
                    <div>
                        <p style={{ color: colors.textSecondary, fontFamily: fonts.condensed, letterSpacing: '0.15em', fontSize: '0.8rem', marginBottom: '16px' }}>
                            NEW SOLDIER REGISTRATION
                        </p>

                        <form onSubmit={handleSubmit(onCreateSubmit)} className="space-y-4">
                            {[
                                { name: 'name', label: 'FULL NAME / RANK', placeholder: '1LT Reece' },
                                { name: 'dob', label: 'DATE OF BIRTH', placeholder: 'YYYY-MM-DD', type: 'date' },
                                { name: 'mos', label: 'MOS', placeholder: '11A' },
                            ].map(field => (
                                <div key={field.name}>
                                    <label style={{ color: colors.textSecondary, fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.15em', display: 'block', marginBottom: '4px' }}>
                                        {field.label}
                                    </label>
                                    <input
                                        {...register(field.name)}
                                        type={field.type || 'text'}
                                        placeholder={field.placeholder}
                                        className="w-full px-4 py-3 rounded outline-none transition-all"
                                        style={{
                                            backgroundColor: colors.bgCard,
                                            border: `1px solid ${colors.border}`,
                                            color: colors.textPrimary,
                                            fontFamily: fonts.body,
                                            fontSize: '0.95rem',
                                        }}
                                        onFocus={e => e.currentTarget.style.borderColor = colors.accentGold}
                                        onBlur={e => e.currentTarget.style.borderColor = errors[field.name] ? colors.danger : colors.border}
                                    />
                                    {errors[field.name] && (
                                        <p style={{ color: colors.danger, fontSize: '0.75rem', marginTop: '2px' }}>{errors[field.name].message}</p>
                                    )}
                                </div>
                            ))}

                            <div>
                                <label style={{ color: colors.textSecondary, fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.15em', display: 'block', marginBottom: '4px' }}>
                                    GENDER
                                </label>
                                <select
                                    {...register('gender')}
                                    className="w-full px-4 py-3 rounded outline-none"
                                    style={{
                                        backgroundColor: colors.bgCard,
                                        border: `1px solid ${colors.border}`,
                                        color: colors.textPrimary,
                                        fontFamily: fonts.body,
                                        fontSize: '0.95rem',
                                    }}
                                >
                                    <option value="">Select...</option>
                                    <option value="Male">Male</option>
                                    <option value="Female">Female</option>
                                </select>
                                {errors.gender && (
                                    <p style={{ color: colors.danger, fontSize: '0.75rem', marginTop: '2px' }}>{errors.gender.message}</p>
                                )}
                            </div>

                            <div className="flex gap-3 pt-2">
                                <button
                                    type="button"
                                    onClick={() => setShowCreate(false)}
                                    className="flex-1 py-3 rounded"
                                    style={{
                                        backgroundColor: 'transparent',
                                        border: `1px solid ${colors.border}`,
                                        color: colors.textSecondary,
                                        fontFamily: fonts.condensed,
                                        letterSpacing: '0.1em',
                                    }}
                                >
                                    CANCEL
                                </button>
                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="flex-1 py-3 rounded transition-all"
                                    style={{
                                        backgroundColor: colors.accentGold,
                                        border: 'none',
                                        color: colors.bgPrimary,
                                        fontFamily: fonts.condensed,
                                        fontWeight: 700,
                                        letterSpacing: '0.1em',
                                        opacity: loading ? 0.6 : 1,
                                    }}
                                >
                                    {loading ? 'CREATING...' : 'CREATE & CONTINUE'}
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        </div>
    )
}