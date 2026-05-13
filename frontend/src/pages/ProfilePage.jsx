import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { getProfile, updateProfile, deleteSoldier } from '../api/api'
import { colors, fonts } from '../constants/theme'

// ── Helpers ──────────────────────────────────────────────────────────────────

function inchesToDisplay(inches) {
    if (!inches) return '—'
    const ft = Math.floor(inches / 12)
    const i = inches % 12
    return `${ft}'${String(i).padStart(2, '0')}"`
}

function secondsToDisplay(secs) {
    if (!secs) return '—'
    const m = Math.floor(secs / 60)
    const s = secs % 60
    return `${m}:${String(s).padStart(2, '0')}`
}

function feetInchesToInches(feet, inches) {
    return (parseInt(feet) || 0) * 12 + (parseInt(inches) || 0)
}

function mmssToSeconds(minutes, seconds) {
    return (parseInt(minutes) || 0) * 60 + (parseInt(seconds) || 0)
}

// ── Sub-components ────────────────────────────────────────────────────────────

function DataRow({ label, value, mono = false }) {
    return (
        <div
            className="flex justify-between items-center py-3"
            style={{ borderBottom: `1px solid ${colors.border}` }}
        >
            <span style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, letterSpacing: '0.05em' }}>
                {label}
            </span>
            <span style={{ fontFamily: mono ? fonts.mono : fonts.condensed, fontSize: '0.9rem', color: colors.textPrimary }}>
                {value ?? '—'}
            </span>
        </div>
    )
}

function EditInput({ label, value, onChange, suffix, type = 'number', error }) {
    return (
        <div className="mb-4">
            <label style={{
                fontFamily: fonts.condensed,
                fontSize: '0.75rem',
                letterSpacing: '0.15em',
                color: colors.textSecondary,
                display: 'block',
                marginBottom: '4px',
            }}>
                {label}
            </label>
            <div className="flex items-center gap-2">
                <input
                    type={type}
                    value={value}
                    onChange={e => onChange(e.target.value)}
                    style={{
                        flex: 1,
                        padding: '10px 14px',
                        borderRadius: '4px',
                        backgroundColor: colors.bgPrimary,
                        border: `1px solid ${error ? colors.danger : colors.border}`,
                        color: colors.textPrimary,
                        fontFamily: fonts.mono,
                        fontSize: '0.95rem',
                        outline: 'none',
                    }}
                    onFocus={e => e.currentTarget.style.borderColor = colors.accentGold}
                    onBlur={e => e.currentTarget.style.borderColor = error ? colors.danger : colors.border}
                />
                {suffix && (
                    <span style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary }}>
                        {suffix}
                    </span>
                )}
            </div>
            {error && (
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.72rem', color: colors.danger, marginTop: '3px' }}>{error}</p>
            )}
        </div>
    )
}

// ── Edit mode form ────────────────────────────────────────────────────────────

function EditForm({ profileData, soldierId, onSaved, onCancel }) {
    const [weightLbs, setWeightLbs] = useState(String(profileData.bodyWeightLbs ?? ''))
    const [heightFt, setHeightFt] = useState(String(Math.floor((profileData.heightInches ?? 0) / 12)))
    const [heightIn, setHeightIn] = useState(String((profileData.heightInches ?? 0) % 12))
    const [trapBar3RM, setTrapBar3RM] = useState(String(profileData.trapBar3RM ?? ''))
    const [lastHrpCount, setLastHrpCount] = useState(String(profileData.lastHrpCount ?? ''))
    const [twoMileMin, setTwoMileMin] = useState(String(Math.floor((profileData.twoMileTimeSeconds ?? 0) / 60)))
    const [twoMileSec, setTwoMileSec] = useState(String((profileData.twoMileTimeSeconds ?? 0) % 60))
    const [benchPress1RM, setBenchPress1RM] = useState(String(profileData.benchPress1RM ?? ''))

    const [errors, setErrors] = useState({})
    const [saving, setSaving] = useState(false)
    const [saveError, setSaveError] = useState(null)

    const validate = () => {
        const e = {}
        if (!weightLbs || parseInt(weightLbs) < 80) e.weightLbs = 'Required (≥80)'
        if (!heightFt || parseInt(heightFt) < 4) e.heightFt = 'Required (4–7 ft)'
        if (heightIn === '' || parseInt(heightIn) < 0 || parseInt(heightIn) > 11) e.heightIn = '0–11'
        if (!trapBar3RM || parseInt(trapBar3RM) < 45) e.trapBar3RM = 'Required'
        if (!lastHrpCount || parseInt(lastHrpCount) < 0) e.lastHrpCount = 'Required'
        if (!twoMileMin || parseInt(twoMileMin) < 10) e.twoMileMin = 'Required'
        if (twoMileSec === '' || parseInt(twoMileSec) > 59) e.twoMileSec = '0–59'
        setErrors(e)
        return Object.keys(e).length === 0
    }

    const handleSave = async () => {
        if (!validate()) return
        setSaving(true)
        setSaveError(null)
        try {
            const res = await updateProfile(soldierId, {
                trapBar3RM: parseInt(trapBar3RM),
                lastHrpCount: parseInt(lastHrpCount),
                twoMileTimeSeconds: mmssToSeconds(twoMileMin, twoMileSec),
                benchPress1RM: benchPress1RM ? parseInt(benchPress1RM) : null,
                bodyWeightLbs: parseInt(weightLbs),
                heightInches: feetInchesToInches(heightFt, heightIn),
            })
            onSaved(res.data)
        } catch (e) {
            setSaveError(e.response?.data?.message || 'Failed to save changes')
        } finally {
            setSaving(false)
        }
    }

    return (
        <div>
            <div
                className="p-6 rounded mb-4"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '16px' }}>
                    PHYSICAL STATS
                </p>
                <EditInput label="BODY WEIGHT" value={weightLbs} onChange={setWeightLbs} suffix="lbs" error={errors.weightLbs} />
                <label style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.15em', color: colors.textSecondary, display: 'block', marginBottom: '4px' }}>
                    HEIGHT
                </label>
                <div className="flex gap-3 mb-4">
                    <div className="flex-1">
                        <EditInput label="" value={heightFt} onChange={setHeightFt} suffix="ft" error={errors.heightFt} />
                    </div>
                    <div className="flex-1">
                        <EditInput label="" value={heightIn} onChange={setHeightIn} suffix="in" error={errors.heightIn} />
                    </div>
                </div>
            </div>

            <div
                className="p-6 rounded mb-4"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '16px' }}>
                    PERFORMANCE BASELINES
                </p>
                <EditInput label="TRAP BAR 3RM" value={trapBar3RM} onChange={setTrapBar3RM} suffix="lbs" error={errors.trapBar3RM} />
                <EditInput label="LAST HRP COUNT" value={lastHrpCount} onChange={setLastHrpCount} suffix="reps" error={errors.lastHrpCount} />
                <label style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.15em', color: colors.textSecondary, display: 'block', marginBottom: '4px' }}>
                    2-MILE RUN TIME
                </label>
                <div className="flex gap-3 mb-4">
                    <div className="flex-1">
                        <EditInput label="" value={twoMileMin} onChange={setTwoMileMin} suffix="min" error={errors.twoMileMin} />
                    </div>
                    <div className="flex-1">
                        <EditInput label="" value={twoMileSec} onChange={setTwoMileSec} suffix="sec" error={errors.twoMileSec} />
                    </div>
                </div>
                <EditInput label="BENCH PRESS 1RM (optional)" value={benchPress1RM} onChange={setBenchPress1RM} suffix="lbs" />
            </div>

            {saveError && (
                <div className="mb-4 px-4 py-3 rounded" style={{
                    backgroundColor: colors.dangerDim,
                    border: `1px solid ${colors.danger}`,
                    color: colors.danger,
                    fontFamily: fonts.condensed,
                }}>
                    {saveError}
                </div>
            )}

            <div className="flex gap-3">
                <button
                    onClick={onCancel}
                    className="flex-1 py-3 rounded"
                    style={{
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
                    onClick={handleSave}
                    disabled={saving}
                    className="flex-1 py-3 rounded"
                    style={{
                        backgroundColor: colors.accentGold,
                        border: 'none',
                        color: colors.bgPrimary,
                        fontFamily: fonts.condensed,
                        fontWeight: 700,
                        letterSpacing: '0.1em',
                        cursor: 'pointer',
                        opacity: saving ? 0.6 : 1,
                    }}
                >
                    {saving ? 'SAVING...' : 'SAVE CHANGES'}
                </button>
            </div>
        </div>
    )
}

// ── Main page ────────────────────────────────────────────────────────────────

export default function ProfilePage() {
    const { soldier, profile, setProfile, logout } = useSoldier()
    const navigate = useNavigate()
    const [profileData, setProfileData] = useState(null)
    const [loading, setLoading] = useState(true)
    const [editing, setEditing] = useState(false)
    const [error, setError] = useState(null)
    const [confirmDelete, setConfirmDelete] = useState(false)
    const [deleting, setDeleting] = useState(false)

    useEffect(() => {
        if (!soldier?.id) return
        // Use context profile if available
        if (profile) {
            setProfileData(profile)
            setLoading(false)
            return
        }
        getProfile(soldier.id)
            .then(res => {
                setProfileData(res.data)
                setProfile(res.data)
            })
            .catch(() => setError('Failed to load profile'))
            .finally(() => setLoading(false))
    }, [soldier?.id])

    const handleSaved = (updatedProfile) => {
        setProfileData(updatedProfile)
        setProfile(updatedProfile)
        setEditing(false)
    }

    const handleDeleteConfirm = async () => {
        setDeleting(true)
        try {
            await deleteSoldier(soldier.id)
            logout()
            navigate('/login', { replace: true })
        } catch (e) {
            setError('Failed to delete soldier')
            setConfirmDelete(false)
        } finally {
            setDeleting(false)
        }
    }

    if (loading) return (
        <div className="p-8 flex items-center" style={{ color: colors.textSecondary }}>
            <p style={{ fontFamily: fonts.condensed, letterSpacing: '0.1em' }}>LOADING...</p>
        </div>
    )

    return (
        <div className="p-8 max-w-xl">

            {/* Header */}
            <div className="flex justify-between items-start mb-8">
                <div>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em', marginBottom: '4px' }}>
                        SOLDIER
                    </p>
                    <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                        {soldier?.rank} {soldier?.name}
                    </h1>
                </div>
                <div className="flex gap-2">
                    {!editing && profileData && (
                        <button
                            onClick={() => setEditing(true)}
                            style={{
                                fontFamily: fonts.condensed,
                                fontSize: '0.8rem',
                                letterSpacing: '0.1em',
                                color: colors.accentGold,
                                background: 'none',
                                border: `1px solid ${colors.accentGoldDim}`,
                                borderRadius: '4px',
                                padding: '8px 16px',
                                cursor: 'pointer',
                            }}
                            onMouseEnter={e => e.currentTarget.style.borderColor = colors.accentGold}
                            onMouseLeave={e => e.currentTarget.style.borderColor = colors.accentGoldDim}
                        >
                            EDIT
                        </button>
                    )}
                    <button
                        onClick={() => setConfirmDelete(true)}
                        style={{
                            fontFamily: fonts.condensed,
                            fontSize: '0.8rem',
                            letterSpacing: '0.1em',
                            color: colors.danger,
                            background: 'none',
                            border: `1px solid ${colors.dangerDim}`,
                            borderRadius: '4px',
                            padding: '8px 16px',
                            cursor: 'pointer',
                        }}
                        onMouseEnter={e => {
                            e.currentTarget.style.borderColor = colors.danger
                            e.currentTarget.style.backgroundColor = `${colors.danger}15`
                        }}
                        onMouseLeave={e => {
                            e.currentTarget.style.borderColor = colors.dangerDim
                            e.currentTarget.style.backgroundColor = 'transparent'
                        }}
                    >
                        DELETE
                    </button>
                </div>
            </div>

            {/* Confirm delete modal */}
            {confirmDelete && (
                <div
                    className="fixed inset-0 flex items-center justify-center z-50"
                    style={{ backgroundColor: 'rgba(0,0,0,0.7)' }}
                    onClick={() => !deleting && setConfirmDelete(false)}
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
                            {soldier?.rank} {soldier?.name}
                        </p>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginBottom: '24px', lineHeight: 1.5 }}>
                            This will permanently delete this soldier and all associated data. This cannot be undone.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setConfirmDelete(false)}
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

            {error && (
                <div className="mb-5 px-4 py-3 rounded" style={{
                    backgroundColor: colors.dangerDim,
                    border: `1px solid ${colors.danger}`,
                    color: colors.danger,
                    fontFamily: fonts.condensed,
                }}>
                    {error}
                </div>
            )}

            {/* Soldier info panel — always visible */}
            <div
                className="p-6 rounded mb-4"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '4px' }}>
                    SOLDIER INFO
                </p>
                <DataRow label="Rank" value={soldier?.rank} />
                <DataRow label="MOS" value={soldier?.mos} />
                <DataRow label="Gender" value={soldier?.gender} />
                <DataRow label="Date of Birth" value={soldier?.dob} mono />
            </div>

            {/* Profile section */}
            {editing && profileData ? (
                <EditForm
                    profileData={profileData}
                    soldierId={soldier.id}
                    onSaved={handleSaved}
                    onCancel={() => setEditing(false)}
                />
            ) : profileData ? (
                <>
                    <div
                        className="p-6 rounded mb-4"
                        style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
                    >
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '4px' }}>
                            PHYSICAL STATS
                        </p>
                        <DataRow label="Body Weight" value={`${profileData.bodyWeightLbs} lbs`} mono />
                        <DataRow label="Height" value={inchesToDisplay(profileData.heightInches)} mono />
                    </div>

                    <div
                        className="p-6 rounded mb-4"
                        style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
                    >
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '4px' }}>
                            PERFORMANCE BASELINES
                        </p>
                        <DataRow label="Trap Bar 3RM" value={`${profileData.trapBar3RM} lbs`} mono />
                        <DataRow label="Last HRP Count" value={`${profileData.lastHrpCount} reps`} mono />
                        <DataRow label="2-Mile Run" value={secondsToDisplay(profileData.twoMileTimeSeconds)} mono />
                        <DataRow
                            label="Bench Press 1RM"
                            value={profileData.benchPress1RM ? `${profileData.benchPress1RM} lbs` : '—'}
                            mono
                        />
                        {profileData.benchEstimated && profileData.estimatedBench1RM && (
                            <div className="mt-3 px-4 py-3 rounded" style={{
                                backgroundColor: `${colors.accentGold}10`,
                                border: `1px solid ${colors.accentGoldDim}`,
                            }}>
                                <p style={{ fontFamily: fonts.condensed, fontSize: '0.78rem', color: colors.textSecondary }}>
                                    Estimated bench 1RM: <span style={{ color: colors.accentGold, fontFamily: fonts.mono }}>{profileData.estimatedBench1RM} lbs</span>
                                </p>
                            </div>
                        )}
                    </div>
                </>
            ) : (
                /* No profile yet */
                <div
                    className="p-6 rounded text-center"
                    style={{ backgroundColor: colors.bgCard, border: `1px dashed ${colors.accentGoldDim}` }}
                >
                    <p style={{ fontFamily: fonts.condensed, color: colors.textSecondary, marginBottom: '16px' }}>
                        No fitness profile on file.
                    </p>
                    <button
                        onClick={() => navigate('/onboarding')}
                        style={{
                            backgroundColor: colors.accentGold,
                            border: 'none',
                            color: colors.bgPrimary,
                            fontFamily: fonts.condensed,
                            fontWeight: 700,
                            letterSpacing: '0.1em',
                            padding: '10px 24px',
                            borderRadius: '4px',
                            cursor: 'pointer',
                        }}
                    >
                        COMPLETE PROFILE →
                    </button>
                </div>
            )}
        </div>
    )
}