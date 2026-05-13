import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { getCurrentScores, getActivePlan, generatePlan, getProfile } from '../api/api'
import { colors, fonts } from '../constants/theme'

const EVENT_LABELS = {
    MDL: 'Max Deadlift',
    HRP: 'Hand-Release Push-Up',
    SDC: 'Sprint-Drag-Carry',
    PLK: 'Plank',
    '2MR': 'Two-Mile Run',
}

const EVENT_ORDER = ['MDL', 'HRP', 'SDC', 'PLK', '2MR']

function ScoreBar({ event, score }) {
    const pct = Math.min(score, 100)
    const color = score >= 100 ? colors.accentGold
        : score >= 80 ? colors.success
            : score >= 60 ? '#8a7a2a'
                : colors.danger

    return (
        <div className="mb-4">
            <div className="flex justify-between items-baseline mb-1">
        <span style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', letterSpacing: '0.1em', color: colors.textSecondary }}>
          {event}
        </span>
                <span style={{ fontFamily: fonts.mono, fontSize: '1.1rem', color }}>
          {score}
        </span>
            </div>
            <div style={{ height: '4px', backgroundColor: colors.border, borderRadius: '2px' }}>
                <div style={{
                    height: '100%',
                    width: `${pct}%`,
                    backgroundColor: color,
                    borderRadius: '2px',
                    transition: 'width 0.6s ease',
                }} />
            </div>
            <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', color: colors.textSecondary, marginTop: '2px' }}>
                {EVENT_LABELS[event]}
            </p>
        </div>
    )
}

function SessionCard({ session, onOpen }) {
    const statusColor = session.dayStatus === 'COMPLETED' ? colors.success
        : session.dayStatus === 'INCOMPLETE' ? colors.danger
            : colors.accentGold

    const typeLabel = session.sessionType === 'SESSION_1' ? 'PLK / HRP'
        : session.sessionType === 'SESSION_2' ? 'MDL / SDC'
            : '2MR'

    return (
        <button
            onClick={() => onOpen(session.id)}
            className="w-full text-left p-4 rounded mb-2 transition-all"
            style={{
                backgroundColor: colors.bgCard,
                border: `1px solid ${colors.border}`,
            }}
            onMouseEnter={e => e.currentTarget.style.borderColor = colors.accentGold}
            onMouseLeave={e => e.currentTarget.style.borderColor = colors.border}
        >
            <div className="flex justify-between items-center">
                <div>
          <span style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.1em' }}>
            {session.dayOfWeek} · {session.sessionDate}
          </span>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '1rem', fontWeight: 600, color: colors.textPrimary, marginTop: '2px' }}>
                        {typeLabel}
                    </p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary, marginTop: '1px' }}>
                        {session.plannedExercises?.length || 0} exercises
                    </p>
                </div>
                <div className="text-right">
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
                </div>
            </div>
        </button>
    )
}

export default function DashboardPage() {
    const { soldier, scores, setScores, activePlan, setActivePlan, profile, setProfile } = useSoldier()
    const navigate = useNavigate()
    const [pageLoading, setPageLoading] = useState(true)
    const [generatingPlan, setGeneratingPlan] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!soldier?.id) return
        console.log('effect running, soldier:', soldier?.id, 'activePlan:', activePlan)
        Promise.all([
            getCurrentScores(soldier.id),
            getProfile(soldier.id).catch(() => null),
            activePlan ? Promise.resolve(null) : getActivePlan(soldier.id).catch(() => null),
        ]).then(([scoresRes, profileRes, planRes]) => {
            setScores(scoresRes.data)
            if (profileRes) setProfile(profileRes.data)
            if (planRes) setActivePlan(planRes.data)
        }).catch(() => {
            setError('Failed to load data')
        }).finally(() => setPageLoading(false))
    }, [soldier?.id])

    const handleGeneratePlan = async () => {
        setGeneratingPlan(true)
        try {
            const res = await generatePlan(soldier.id, true)
            setActivePlan(res.data)
        } catch (e) {
            setError('Failed to generate plan')
        } finally {
            setGeneratingPlan(false)
        }
    }

    const totalScore = scores
        ? Object.values(scores).reduce((a, b) => a + b, 0)
        : 0

    const hasProfile = !!profile
    const hasScores = scores && Object.values(scores).some(s => s > 0)

    if (pageLoading) return (
        <div className="flex items-center justify-center h-full" style={{ color: colors.textSecondary }}>
            <p style={{ fontFamily: fonts.condensed, letterSpacing: '0.1em' }}>LOADING...</p>
        </div>
    )

    return (
        <div className="p-8 max-w-6xl">

            {/* Header */}
            <div className="mb-8">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em' }}>
                    WELCOME BACK
                </p>
                <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    {soldier?.name}
                </h1>
                {hasScores && (
                    <p style={{ fontFamily: fonts.mono, fontSize: '0.9rem', color: colors.accentGold, marginTop: '4px' }}>
                        TOTAL SCORE: {totalScore} / 500
                    </p>
                )}
            </div>

            {error && (
                <div className="mb-6 px-4 py-3 rounded" style={{ backgroundColor: colors.dangerDim, border: `1px solid ${colors.danger}`, color: colors.danger, fontFamily: fonts.condensed }}>
                    {error}
                </div>
            )}

            {/* Alerts */}
            {!hasProfile && (
                <div
                    className="mb-6 px-5 py-4 rounded flex items-center justify-between cursor-pointer"
                    style={{ backgroundColor: `${colors.accentGold}15`, border: `1px solid ${colors.accentGoldDim}` }}
                    onClick={() => navigate('/onboarding')}
                >
                    <div>
                        <p style={{ fontFamily: fonts.condensed, fontWeight: 600, color: colors.accentGold, letterSpacing: '0.05em' }}>
                            PROFILE INCOMPLETE
                        </p>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginTop: '2px' }}>
                            Complete your fitness profile to receive a personalized plan
                        </p>
                    </div>
                    <span style={{ color: colors.accentGold, fontSize: '1.2rem' }}>→</span>
                </div>
            )}

            {!hasScores && (
                <div
                    className="mb-6 px-5 py-4 rounded flex items-center justify-between cursor-pointer"
                    style={{ backgroundColor: `${colors.accentGold}15`, border: `1px solid ${colors.accentGoldDim}` }}
                    onClick={() => navigate('/scores')}
                >
                    <div>
                        <p style={{ fontFamily: fonts.condensed, fontWeight: 600, color: colors.accentGold, letterSpacing: '0.05em' }}>
                            NO AFT SCORES ON FILE
                        </p>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginTop: '2px' }}>
                            Submit your most recent AFT scores to get started
                        </p>
                    </div>
                    <span style={{ color: colors.accentGold, fontSize: '1.2rem' }}>→</span>
                </div>
            )}

            <div className="grid grid-cols-2 gap-6">

                {/* Scores panel */}
                <div className="p-6 rounded" style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}>
                    <div className="flex justify-between items-center mb-5">
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.2em', color: colors.textSecondary }}>
                            CURRENT SCORES
                        </p>
                        <button
                            onClick={() => navigate('/scores')}
                            style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.accentGold, letterSpacing: '0.1em', background: 'none', border: 'none', cursor: 'pointer' }}
                        >
                            UPDATE →
                        </button>
                    </div>
                    {hasScores ? (
                        EVENT_ORDER.map(event => (
                            <ScoreBar key={event} event={event} score={scores[event] || 0} />
                        ))
                    ) : (
                        <p style={{ color: colors.textSecondary, fontFamily: fonts.condensed }}>No scores on file.</p>
                    )}
                </div>

                {/* Plan panel */}
                <div className="p-6 rounded" style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}>
                    <div className="flex justify-between items-center mb-5">
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.2em', color: colors.textSecondary }}>
                            THIS WEEK'S PLAN
                        </p>
                        <button
                            onClick={() => navigate('/plan')}
                            style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.accentGold, letterSpacing: '0.1em', background: 'none', border: 'none', cursor: 'pointer' }}
                        >
                            VIEW ALL →
                        </button>
                    </div>

                    {activePlan ? (
                        <>
                            <p style={{ fontFamily: fonts.mono, fontSize: '0.75rem', color: colors.textSecondary, marginBottom: '12px' }}>
                                {activePlan.weekStart} — {activePlan.weekEnd}
                            </p>
                            {[...(activePlan.plannedSessions || [])]
                                .sort((a, b) => a.sessionDate.localeCompare(b.sessionDate))
                                .slice(0, 4).map(session => (
                                    <SessionCard
                                        key={session.id}
                                        session={session}
                                        onOpen={(id) => navigate(`/session/${id}`)}
                                    />
                                ))}
                            {activePlan.plannedSessions?.length > 4 && (
                                <p style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary, textAlign: 'center', marginTop: '8px' }}>
                                    +{activePlan.plannedSessions.length - 4} more sessions
                                </p>
                            )}
                        </>
                    ) : (
                        <div className="text-center py-6">
                            <p style={{ fontFamily: fonts.condensed, color: colors.textSecondary, marginBottom: '16px' }}>
                                No active plan found.
                            </p>
                            {hasScores && hasProfile && (
                                <button
                                    onClick={handleGeneratePlan}
                                    disabled={generatingPlan}
                                    className="px-6 py-3 rounded"
                                    style={{
                                        backgroundColor: colors.accentGold,
                                        color: colors.bgPrimary,
                                        fontFamily: fonts.condensed,
                                        fontWeight: 700,
                                        letterSpacing: '0.1em',
                                        border: 'none',
                                        cursor: 'pointer',
                                        opacity: generatingPlan ? 0.6 : 1,
                                    }}
                                >
                                    {generatingPlan ? 'GENERATING...' : 'GENERATE PLAN'}
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}