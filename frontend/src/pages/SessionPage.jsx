import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { getCheckInStatus, submitCheckIn, logSessionResult, getActivePlan } from '../api/api'
import { colors, fonts } from '../constants/theme'

const EVENT_LABELS = {
    MDL: 'Max Deadlift',
    HRP: 'Hand-Release Push-Up',
    SDC: 'Sprint-Drag-Carry',
    PLK: 'Plank',
    '2MR': 'Two-Mile Run',
}

const SESSION_LABELS = {
    SESSION_1: 'PLK / HRP',
    SESSION_2: 'MDL / SDC',
    SESSION_3: '2MR',
}

function formatPrescription(pe) {
    const { sets, reps, weight, workTime, distance, pace } = pe
    const parts = []
    if (sets && reps) parts.push(`${sets} × ${reps}`)
    else if (sets && distance) parts.push(`${sets} × ${distance}m`)
    else if (sets && workTime) parts.push(`${sets} × ${workTime}s`)
    else if (sets) parts.push(`${sets} sets`)
    if (weight) parts.push(`@ ${weight} lbs`)
    if (workTime && !sets) parts.push(`${Math.floor(workTime / 60)}:${String(workTime % 60).padStart(2, '0')} min`)
    if (pace) parts.push(`${Math.floor(pace / 60)}:${String(pace % 60).padStart(2, '0')} /800m`)
    return parts.join(' ') || '—'
}

// ── Check-in gate ────────────────────────────────────────────────────────────

function CheckInGate({ sessionId, dueEvents, onComplete }) {
    const [scores, setScores] = useState(() =>
        Object.fromEntries(dueEvents.map(e => [e, '']))
    )
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState(null)

    const handleSubmit = async () => {
        // Validate all filled
        for (const event of dueEvents) {
            const val = parseInt(scores[event])
            if (isNaN(val) || val < 0 || val > 100) {
                setError(`Enter a valid score (0–100) for ${event}`)
                return
            }
        }
        setSubmitting(true)
        setError(null)
        try {
            const eventScores = Object.fromEntries(
                dueEvents.map(e => [e, parseInt(scores[e])])
            )
            await submitCheckIn(sessionId, { eventScores })
            onComplete()
        } catch (e) {
            setError('Failed to submit check-in scores')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <div className="max-w-lg">
            {/* Header */}
            <div className="mb-8">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.accentGold, letterSpacing: '0.2em', marginBottom: '4px' }}>
                    BEFORE YOU BEGIN
                </p>
                <h2 style={{ fontFamily: fonts.heading, fontSize: '2rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    CHECK-IN REQUIRED
                </h2>
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.9rem', color: colors.textSecondary, marginTop: '8px', lineHeight: 1.5 }}>
                    Enter your current event scores before starting this session. These keep your plan calibrated.
                </p>
            </div>

            <div
                className="p-6 rounded mb-6"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '16px' }}>
                    DUE EVENTS
                </p>

                {dueEvents.map(event => (
                    <div key={event} className="mb-5">
                        <div className="flex justify-between items-baseline mb-2">
                            <label style={{ fontFamily: fonts.condensed, fontSize: '0.9rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                                {event}
                                <span style={{ color: colors.textSecondary, fontSize: '0.8rem', marginLeft: '8px' }}>
                                    {EVENT_LABELS[event]}
                                </span>
                            </label>
                            <span style={{ fontFamily: fonts.mono, fontSize: '1.1rem', color: colors.accentGold }}>
                                {scores[event] !== '' ? scores[event] : '—'}
                            </span>
                        </div>
                        <input
                            type="number"
                            min={0}
                            max={100}
                            value={scores[event]}
                            onChange={e => setScores(prev => ({ ...prev, [event]: e.target.value }))}
                            placeholder="0–100"
                            className="w-full px-4 py-3 rounded outline-none"
                            style={{
                                backgroundColor: colors.bgPrimary,
                                border: `1px solid ${colors.border}`,
                                color: colors.textPrimary,
                                fontFamily: fonts.mono,
                                fontSize: '1rem',
                            }}
                            onFocus={e => e.currentTarget.style.borderColor = colors.accentGold}
                            onBlur={e => e.currentTarget.style.borderColor = colors.border}
                        />
                    </div>
                ))}
            </div>

            {error && (
                <div className="mb-4 px-4 py-3 rounded" style={{
                    backgroundColor: colors.dangerDim,
                    border: `1px solid ${colors.danger}`,
                    color: colors.danger,
                    fontFamily: fonts.condensed,
                }}>
                    {error}
                </div>
            )}

            <button
                onClick={handleSubmit}
                disabled={submitting}
                className="w-full py-4 rounded"
                style={{
                    backgroundColor: colors.accentGold,
                    border: 'none',
                    color: colors.bgPrimary,
                    fontFamily: fonts.condensed,
                    fontWeight: 700,
                    fontSize: '1rem',
                    letterSpacing: '0.15em',
                    cursor: 'pointer',
                    opacity: submitting ? 0.6 : 1,
                }}
            >
                {submitting ? 'SUBMITTING...' : 'SUBMIT & START SESSION'}
            </button>
        </div>
    )
}

// ── Exercise list + result logger ────────────────────────────────────────────

function ExerciseRow({ pe, failed, onToggleFailed }) {
    const isFailed = failed.includes(pe.exercise.id)

    return (
        <div
            className="flex items-center justify-between py-4"
            style={{ borderBottom: `1px solid ${colors.border}` }}
        >
            <div className="flex items-center gap-4">
                <button
                    onClick={() => onToggleFailed(pe.exercise.id)}
                    title={isFailed ? 'Mark completed' : 'Mark failed'}
                    style={{
                        width: '22px',
                        height: '22px',
                        borderRadius: '3px',
                        border: `2px solid ${isFailed ? colors.danger : colors.border}`,
                        backgroundColor: isFailed ? `${colors.danger}20` : 'transparent',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        transition: 'all 0.15s',
                    }}
                >
                    {isFailed && (
                        <span style={{ color: colors.danger, fontSize: '0.7rem', fontWeight: 700 }}>✕</span>
                    )}
                </button>
                <div>
                    <p style={{
                        fontFamily: fonts.condensed,
                        fontSize: '0.95rem',
                        color: isFailed ? colors.textSecondary : colors.textPrimary,
                        fontWeight: 500,
                        textDecoration: isFailed ? 'line-through' : 'none',
                        transition: 'all 0.15s',
                    }}>
                        {pe.exercise.name}
                    </p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', color: colors.textSecondary }}>
                        {pe.exercise.difficulty}
                    </p>
                </div>
            </div>
            <span style={{ fontFamily: fonts.mono, fontSize: '0.85rem', color: colors.accentGold, flexShrink: 0 }}>
                {formatPrescription(pe)}
            </span>
        </div>
    )
}

function SessionView({ session, sessionId, onDone }) {
    const [failedIds, setFailedIds] = useState([])
    const [rpe, setRpe] = useState(null)
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState(null)

    const toggleFailed = (exerciseId) => {
        setFailedIds(prev =>
            prev.includes(exerciseId)
                ? prev.filter(id => id !== exerciseId)
                : [...prev, exerciseId]
        )
    }

    const handleLog = async () => {
        setSubmitting(true)
        setError(null)
        try {
            await logSessionResult({
                sessionId: parseInt(sessionId),
                userRPE: rpe,
                failedExerciseIds: failedIds,
            })
            onDone()
        } catch (e) {
            setError('Failed to log session result')
        } finally {
            setSubmitting(false)
        }
    }

    const label = SESSION_LABELS[session.sessionType] || session.sessionType
    const exercises = session.plannedExercises || []
    const failedCount = failedIds.length
    const completedCount = exercises.length - failedCount

    const rpeColor = rpe === null ? colors.textSecondary
        : rpe <= 5 ? colors.success
            : rpe <= 7 ? colors.accentGold
                : colors.danger

    return (
        <div className="max-w-2xl">
            {/* Header */}
            <div className="mb-8">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em', marginBottom: '4px' }}>
                    {session.sessionType?.replace('_', ' ')} · {session.sessionDate}
                </p>
                <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    {label}
                </h1>
                {session.description && (
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.9rem', color: colors.textSecondary, marginTop: '8px', lineHeight: 1.5, maxWidth: '520px' }}>
                        {session.description}
                    </p>
                )}
            </div>

            {/* Exercise list */}
            <div
                className="p-6 rounded mb-6"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <div className="flex justify-between items-center mb-2">
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary }}>
                        EXERCISES — {exercises.length} TOTAL
                    </p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary }}>
                        TAP ✕ TO MARK FAILED
                    </p>
                </div>
                {exercises.map(pe => (
                    <ExerciseRow
                        key={pe.id}
                        pe={pe}
                        failed={failedIds}
                        onToggleFailed={toggleFailed}
                    />
                ))}
                {exercises.length === 0 && (
                    <p style={{ fontFamily: fonts.condensed, color: colors.textSecondary, paddingTop: '8px' }}>
                        No exercises found for this session.
                    </p>
                )}
            </div>

            {/* RPE */}
            <div
                className="p-6 rounded mb-6"
                style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
            >
                <div className="flex justify-between items-baseline mb-4">
                    <div>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary }}>
                            RATE OF PERCEIVED EXERTION
                        </p>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary, marginTop: '2px' }}>
                            Optional — how hard was this session?
                        </p>
                    </div>
                    <span style={{ fontFamily: fonts.mono, fontSize: '1.8rem', color: rpeColor, minWidth: '40px', textAlign: 'right' }}>
                        {rpe ?? '—'}
                    </span>
                </div>

                <div className="flex gap-2">
                    {[1,2,3,4,5,6,7,8,9,10].map(n => {
                        const btnColor = n <= 5 ? colors.success : n <= 7 ? colors.accentGold : colors.danger
                        const active = rpe === n
                        return (
                            <button
                                key={n}
                                onClick={() => setRpe(rpe === n ? null : n)}
                                style={{
                                    flex: 1,
                                    paddingTop: '10px',
                                    paddingBottom: '10px',
                                    borderRadius: '3px',
                                    border: `1px solid ${active ? btnColor : colors.border}`,
                                    backgroundColor: active ? `${btnColor}25` : 'transparent',
                                    color: active ? btnColor : colors.textSecondary,
                                    fontFamily: fonts.mono,
                                    fontSize: '0.8rem',
                                    cursor: 'pointer',
                                    transition: 'all 0.15s',
                                }}
                            >
                                {n}
                            </button>
                        )
                    })}
                </div>

                <div className="flex justify-between mt-2">
                    <span style={{ fontFamily: fonts.condensed, fontSize: '0.65rem', color: colors.success, letterSpacing: '0.1em' }}>EASY</span>
                    <span style={{ fontFamily: fonts.condensed, fontSize: '0.65rem', color: colors.danger, letterSpacing: '0.1em' }}>MAX EFFORT</span>
                </div>
            </div>

            {/* Summary + submit */}
            <div
                className="p-5 rounded mb-6 flex justify-between items-center"
                style={{ backgroundColor: `${colors.accentGold}10`, border: `1px solid ${colors.accentGoldDim}` }}
            >
                <div>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.1em' }}>SESSION SUMMARY</p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.95rem', color: colors.textPrimary, marginTop: '4px' }}>
                        <span style={{ color: colors.success }}>{completedCount} completed</span>
                        {failedCount > 0 && (
                            <span style={{ color: colors.danger }}> · {failedCount} failed</span>
                        )}
                        {rpe && (
                            <span style={{ color: colors.textSecondary }}> · RPE {rpe}</span>
                        )}
                    </p>
                </div>
            </div>

            {error && (
                <div className="mb-4 px-4 py-3 rounded" style={{
                    backgroundColor: colors.dangerDim,
                    border: `1px solid ${colors.danger}`,
                    color: colors.danger,
                    fontFamily: fonts.condensed,
                }}>
                    {error}
                </div>
            )}

            <button
                onClick={handleLog}
                disabled={submitting || exercises.length === 0}
                className="w-full py-4 rounded"
                style={{
                    backgroundColor: colors.accentGold,
                    border: 'none',
                    color: colors.bgPrimary,
                    fontFamily: fonts.condensed,
                    fontWeight: 700,
                    fontSize: '1rem',
                    letterSpacing: '0.15em',
                    cursor: 'pointer',
                    opacity: (submitting || exercises.length === 0) ? 0.6 : 1,
                }}
            >
                {submitting ? 'LOGGING...' : 'LOG SESSION COMPLETE'}
            </button>
        </div>
    )
}

// ── Main page ────────────────────────────────────────────────────────────────

export default function SessionPage() {
    const { sessionId } = useParams()
    const { soldier, activePlan, setActivePlan } = useSoldier()
    const navigate = useNavigate()

    const [session, setSession] = useState(null)
    const [checkIn, setCheckIn] = useState(null) // CheckInResponseDTO
    const [phase, setPhase] = useState('loading') // loading | checkin | session | done | error

    useEffect(() => {
        if (!sessionId) return

        // Find session in activePlan if available
        const found = activePlan?.plannedSessions?.find(s => s.id === parseInt(sessionId))
        if (found) setSession(found)

        getCheckInStatus(sessionId)
            .then(res => {
                setCheckIn(res.data)
                if (res.data.checkInRequired) {
                    setPhase('checkin')
                } else {
                    setPhase('session')
                }
            })
            .catch(() => setPhase('error'))
    }, [sessionId])

    // After loading session, also resolve session data if not in plan context
    useEffect(() => {
        if (phase !== 'session' && phase !== 'checkin') return
        if (session) return
        // session not found in context — we can't fetch single session directly,
        // but we can refetch the active plan to get it
        if (soldier?.id) {
            // Refresh plan to get session detail
            import('../api/api').then(({ getActivePlan }) => {
                getActivePlan(soldier.id).then(res => {
                    const found = res.data?.plannedSessions?.find(s => s.id === parseInt(sessionId))
                    if (found) setSession(found)
                }).catch(() => {})
            })
        }
    }, [phase, session, soldier?.id, sessionId])

    const handleCheckInComplete = () => {
        // Refresh check-in status after submission
        getCheckInStatus(sessionId).then(() => setPhase('session')).catch(() => setPhase('session'))
    }

    const handleSessionDone = () => {
        // Invalidate plan in context so dashboard refetches
        setActivePlan(null)
        setPhase('done')
    }

    if (phase === 'loading') return (
        <div className="p-8 flex items-center" style={{ color: colors.textSecondary }}>
            <p style={{ fontFamily: fonts.condensed, letterSpacing: '0.1em' }}>LOADING SESSION...</p>
        </div>
    )

    if (phase === 'error') return (
        <div className="p-8">
            <p style={{ fontFamily: fonts.condensed, color: colors.danger }}>Failed to load session check-in status.</p>
            <button
                onClick={() => navigate(-1)}
                style={{ fontFamily: fonts.condensed, color: colors.accentGold, background: 'none', border: 'none', cursor: 'pointer', marginTop: '12px' }}
            >
                ← GO BACK
            </button>
        </div>
    )

    if (phase === 'done') return (
        <div className="p-8 max-w-lg">
            <div className="mb-8">
                <div style={{
                    width: '56px',
                    height: '56px',
                    borderRadius: '50%',
                    border: `2px solid ${colors.success}`,
                    backgroundColor: `${colors.success}20`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    marginBottom: '20px',
                }}>
                    <span style={{ color: colors.success, fontSize: '1.4rem' }}>✓</span>
                </div>
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em', marginBottom: '4px' }}>
                    SESSION LOGGED
                </p>
                <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    WELL DONE
                </h1>
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.9rem', color: colors.textSecondary, marginTop: '8px' }}>
                    Your result has been recorded. Keep the momentum going.
                </p>
            </div>
            <div className="flex gap-3">
                <button
                    onClick={() => navigate('/plan')}
                    className="flex-1 py-3 rounded"
                    style={{
                        backgroundColor: colors.accentGold,
                        border: 'none',
                        color: colors.bgPrimary,
                        fontFamily: fonts.condensed,
                        fontWeight: 700,
                        letterSpacing: '0.1em',
                        cursor: 'pointer',
                    }}
                >
                    BACK TO PLAN
                </button>
                <button
                    onClick={() => navigate('/dashboard')}
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
                    DASHBOARD
                </button>
            </div>
        </div>
    )

    return (
        <div className="p-8">
            {/* Back link */}
            <button
                onClick={() => navigate(-1)}
                style={{
                    fontFamily: fonts.condensed,
                    fontSize: '0.8rem',
                    color: colors.textSecondary,
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    letterSpacing: '0.1em',
                    marginBottom: '24px',
                    display: 'block',
                }}
                onMouseEnter={e => e.currentTarget.style.color = colors.accentGold}
                onMouseLeave={e => e.currentTarget.style.color = colors.textSecondary}
            >
                ← BACK
            </button>

            {phase === 'checkin' && checkIn && (
                <CheckInGate
                    sessionId={sessionId}
                    dueEvents={checkIn.dueEvents}
                    onComplete={handleCheckInComplete}
                />
            )}

            {phase === 'session' && session && (
                <SessionView
                    session={session}
                    sessionId={sessionId}
                    onDone={handleSessionDone}
                />
            )}

            {phase === 'session' && !session && (
                <div style={{ color: colors.textSecondary, fontFamily: fonts.condensed }}>
                    Loading session details...
                </div>
            )}
        </div>
    )
}