import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { getActivePlan, generatePlan } from '../api/api'
import { colors, fonts } from '../constants/theme'

// Formats "2026-05-12" → "12 MAY"
function formatShortDate(dateStr) {
    if (!dateStr) return ''
    const [, month, day] = dateStr.split('-')
    const months = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC']
    return `${parseInt(day)} ${months[parseInt(month) - 1]}`
}

const SESSION_LABELS = {
    SESSION_1: 'PLK / HRP',
    SESSION_2: 'MDL / SDC',
    SESSION_3: '2MR',
}

const STATUS_COLORS = {
    ACTIVE: colors.accentGold,
    COMPLETED: colors.success,
    INCOMPLETE: colors.danger,
}

function formatPrescription(exercise) {
    const { sets, reps, weight, workTime, distance, pace, plannedExerciseUnit } = exercise
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

function ExerciseRow({ plannedExercise, index }) {
    const { exercise } = plannedExercise
    const prescription = formatPrescription(plannedExercise)

    return (
        <div
            className="flex justify-between items-center py-2"
            style={{ borderBottom: `1px solid ${colors.border}` }}
        >
            <div className="flex items-center gap-3">
        <span style={{ fontFamily: fonts.mono, fontSize: '0.65rem', color: colors.textSecondary, width: '20px' }}>
          {String(index + 1).padStart(2, '0')}
        </span>
                <div>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.9rem', color: colors.textPrimary, fontWeight: 500 }}>
                        {exercise.name}
                    </p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', color: colors.textSecondary }}>
                        {exercise.difficulty}
                    </p>
                </div>
            </div>
            <span style={{ fontFamily: fonts.mono, fontSize: '0.8rem', color: colors.accentGold }}>
        {prescription}
      </span>
        </div>
    )
}

function SessionCard({ session, onOpen }) {
    const [expanded, setExpanded] = useState(false)
    const statusColor = STATUS_COLORS[session.dayStatus] || colors.textSecondary
    const label = SESSION_LABELS[session.sessionType] || session.sessionType

    return (
        <div
            className="rounded mb-3"
            style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
        >
            {/* Header */}
            <div
                className="flex items-center justify-between px-5 py-4 cursor-pointer"
                onClick={() => setExpanded(!expanded)}
                style={{ borderBottom: expanded ? `1px solid ${colors.border}` : 'none' }}
            >
                <div className="flex items-center gap-4">
                    <div>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '1rem', fontWeight: 600, color: colors.textPrimary }}>
                            {label}
                        </p>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary }}>
                            {session.plannedExercises?.length || 0} exercises
                        </p>
                    </div>
                </div>
                <div className="flex items-center gap-3">
          <span style={{
              fontFamily: fonts.condensed,
              fontSize: '0.7rem',
              letterSpacing: '0.1em',
              color: statusColor,
              backgroundColor: `${statusColor}20`,
              padding: '3px 8px',
              borderRadius: '3px',
          }}>
            {session.dayStatus}
          </span>
                    {session.dayStatus === 'ACTIVE' && (
                        <button
                            onClick={(e) => { e.stopPropagation(); onOpen(session.id) }}
                            className="px-4 py-1 rounded"
                            style={{
                                backgroundColor: colors.accentGold,
                                color: colors.bgPrimary,
                                fontFamily: fonts.condensed,
                                fontSize: '0.75rem',
                                fontWeight: 700,
                                letterSpacing: '0.08em',
                                border: 'none',
                                cursor: 'pointer',
                            }}
                        >
                            OPEN
                        </button>
                    )}
                    <span style={{ color: colors.textSecondary, fontSize: '0.8rem' }}>
            {expanded ? '▲' : '▼'}
          </span>
                </div>
            </div>

            {/* Exercises */}
            {expanded && (
                <div className="px-5 py-3">
                    {session.plannedExercises?.map((pe, i) => (
                        <ExerciseRow key={pe.id} plannedExercise={pe} index={i} />
                    ))}
                </div>
            )}
        </div>
    )
}

function DayGroup({ dayLabel, sessions, onOpen }) {
    return (
        <div className="mb-6">
            <div className="flex items-center gap-3 mb-3">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.2em', color: colors.textSecondary }}>
                    {dayLabel}
                </p>
                <div style={{ flex: 1, height: '1px', backgroundColor: colors.border }} />
            </div>
            {sessions.map(session => (
                <SessionCard key={session.id} session={session} onOpen={onOpen} />
            ))}
        </div>
    )
}

export default function PlanPage() {
    const { soldier, activePlan, setActivePlan, loading: contextLoading } = useSoldier()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(true)
    const [generating, setGenerating] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!soldier?.id || contextLoading) return
        if (activePlan) {
            setLoading(false)
            return
        }
        getActivePlan(soldier.id)
            .then(res => setActivePlan(res.data))
            .catch(() => setActivePlan(null))
            .finally(() => setLoading(false))
    }, [soldier?.id, contextLoading])

    const handleGenerate = async () => {
        setGenerating(true)
        setError(null)
        try {
            const res = await generatePlan(soldier.id, true)
            setActivePlan(res.data)
        } catch (e) {
            setError('Failed to generate plan')
        } finally {
            setGenerating(false)
        }
    }

    // Sort sessions by actual date, then group by date for display
    const sortedSessions = [...(activePlan?.plannedSessions || [])]
        .sort((a, b) => a.sessionDate.localeCompare(b.sessionDate))

    // Build ordered groups: [{ dayLabel, date, sessions }]
    const dayGroupMap = {}
    const dayGroupOrder = []
    sortedSessions.forEach(session => {
        const date = session.sessionDate // "2026-05-13"
        if (!dayGroupMap[date]) {
            const dayLabel = `${session.dayOfWeek} · ${formatShortDate(date)}`
            dayGroupMap[date] = { dayLabel, sessions: [] }
            dayGroupOrder.push(date)
        }
        dayGroupMap[date].sessions.push(session)
    })

    const completedCount = activePlan?.plannedSessions?.filter(s => s.dayStatus === 'COMPLETED').length || 0
    const totalCount = activePlan?.plannedSessions?.length || 0

    if (loading) return (
        <div className="flex items-center justify-center h-full" style={{ color: colors.textSecondary }}>
            <p style={{ fontFamily: fonts.condensed, letterSpacing: '0.1em' }}>LOADING...</p>
        </div>
    )

    return (
        <div className="p-8 max-w-3xl">

            {/* Header */}
            <div className="flex justify-between items-start mb-8">
                <div>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em' }}>
                        WEEKLY PLAN
                    </p>
                    <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                        {activePlan ? `${activePlan.weekStart} — ${activePlan.weekEnd}` : 'NO ACTIVE PLAN'}
                    </h1>
                    {activePlan && (
                        <p style={{ fontFamily: fonts.mono, fontSize: '0.8rem', color: colors.accentGold, marginTop: '4px' }}>
                            {completedCount} / {totalCount} SESSIONS COMPLETE
                        </p>
                    )}
                </div>
                <button
                    onClick={handleGenerate}
                    disabled={generating}
                    className="px-5 py-3 rounded"
                    style={{
                        backgroundColor: 'transparent',
                        border: `1px solid ${colors.accentGoldDim}`,
                        color: colors.accentGold,
                        fontFamily: fonts.condensed,
                        fontWeight: 600,
                        letterSpacing: '0.1em',
                        cursor: 'pointer',
                        opacity: generating ? 0.6 : 1,
                    }}
                    onMouseEnter={e => e.currentTarget.style.borderColor = colors.accentGold}
                    onMouseLeave={e => e.currentTarget.style.borderColor = colors.accentGoldDim}
                >
                    {generating ? 'GENERATING...' : '↺ NEW PLAN'}
                </button>
            </div>

            {error && (
                <div className="mb-4 px-4 py-3 rounded" style={{ backgroundColor: colors.dangerDim, border: `1px solid ${colors.danger}`, color: colors.danger, fontFamily: fonts.condensed }}>
                    {error}
                </div>
            )}

            {!activePlan ? (
                <div className="text-center py-16">
                    <p style={{ fontFamily: fonts.condensed, color: colors.textSecondary, marginBottom: '16px' }}>
                        No active plan. Generate one to get started.
                    </p>
                </div>
            ) : (
                dayGroupOrder.map(date => (
                    <DayGroup
                        key={date}
                        dayLabel={dayGroupMap[date].dayLabel}
                        sessions={dayGroupMap[date].sessions}
                        onOpen={(id) => navigate(`/session/${id}`)}
                    />
                ))
            )}
        </div>
    )
}