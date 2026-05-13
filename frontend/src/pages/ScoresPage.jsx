import { useState } from 'react'
import { useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { submitScores, getCurrentScores } from '../api/api'
import { colors, fonts } from '../constants/theme'

const EVENTS = [
    {
        key: 'MDL',
        label: 'Max Deadlift',
        hint: 'Score 0–100',
        placeholder: '70',
    },
    {
        key: 'HRP',
        label: 'Hand-Release Push-Up',
        hint: 'Score 0–100',
        placeholder: '65',
    },
    {
        key: 'SDC',
        label: 'Sprint-Drag-Carry',
        hint: 'Score 0–100',
        placeholder: '72',
    },
    {
        key: 'PLK',
        label: 'Plank',
        hint: 'Score 0–100',
        placeholder: '80',
    },
    {
        key: '2MR',
        label: 'Two-Mile Run',
        hint: 'Score 0–100',
        placeholder: '60',
    },
]

function ScoreInput({ event, value, onChange, error }) {
    const score = parseInt(value)
    const pct = (!isNaN(score) && score >= 0) ? Math.min(score, 100) : 0
    const barColor = score >= 100 ? colors.accentGold
        : score >= 80 ? colors.success
            : score >= 60 ? '#8a7a2a'
                : score > 0 ? colors.danger
                    : colors.border

    return (
        <div
            className="p-5 rounded mb-3"
            style={{ backgroundColor: colors.bgCard, border: `1px solid ${error ? colors.danger : colors.border}` }}
        >
            <div className="flex items-center justify-between mb-3">
                <div>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', letterSpacing: '0.15em', color: colors.textSecondary }}>
                        {event.key}
                    </p>
                    <p style={{ fontFamily: fonts.condensed, fontSize: '1rem', color: colors.textPrimary, fontWeight: 500 }}>
                        {event.label}
                    </p>
                </div>
                <input
                    type="number"
                    value={value}
                    onChange={e => onChange(e.target.value)}
                    placeholder={event.placeholder}
                    min={0}
                    max={100}
                    style={{
                        width: '80px',
                        padding: '8px 12px',
                        borderRadius: '4px',
                        backgroundColor: colors.bgPrimary,
                        border: `1px solid ${error ? colors.danger : colors.border}`,
                        color: colors.textPrimary,
                        fontFamily: fonts.mono,
                        fontSize: '1.2rem',
                        textAlign: 'center',
                        outline: 'none',
                    }}
                    onFocus={e => e.currentTarget.style.borderColor = colors.accentGold}
                    onBlur={e => e.currentTarget.style.borderColor = error ? colors.danger : colors.border}
                />
            </div>

            {/* Score bar */}
            <div style={{ height: '3px', backgroundColor: colors.border, borderRadius: '2px' }}>
                <div style={{
                    height: '100%',
                    width: `${pct}%`,
                    backgroundColor: barColor,
                    borderRadius: '2px',
                    transition: 'width 0.3s ease, background-color 0.3s ease',
                }} />
            </div>

            {error && (
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.danger, marginTop: '6px' }}>
                    {error}
                </p>
            )}
        </div>
    )
}

export default function ScoresPage() {
    const { soldier, scores: existingScores, setScores } = useSoldier()
    const navigate = useNavigate()

    // Initialize from existing scores if available
    const [eventValues, setEventValues] = useState(() => {
        const init = {}
        EVENTS.forEach(e => {
            init[e.key] = existingScores?.[e.key] != null ? String(existingScores[e.key]) : ''
        })
        return init
    })
    const [testDate, setTestDate] = useState(() => new Date().toISOString().split('T')[0])
    const [fieldErrors, setFieldErrors] = useState({})
    const [submitting, setSubmitting] = useState(false)
    const [submitError, setSubmitError] = useState(null)

    const totalScore = EVENTS.reduce((sum, e) => {
        const v = parseInt(eventValues[e.key])
        return sum + (isNaN(v) ? 0 : v)
    }, 0)

    const allFilled = EVENTS.every(e => eventValues[e.key] !== '')

    const validate = () => {
        const errs = {}
        EVENTS.forEach(e => {
            const v = parseInt(eventValues[e.key])
            if (eventValues[e.key] === '' || isNaN(v) || v < 0 || v > 100) {
                errs[e.key] = 'Enter a score between 0 and 100'
            }
        })
        if (!testDate) errs.testDate = 'Required'
        setFieldErrors(errs)
        return Object.keys(errs).length === 0
    }

    const handleSubmit = async () => {
        if (!validate()) return
        setSubmitting(true)
        setSubmitError(null)
        try {
            const eventScore = {}
            EVENTS.forEach(e => { eventScore[e.key] = parseInt(eventValues[e.key]) })
            await submitScores({
                soldierId: soldier.id,
                eventScore,
                dateOfTest: testDate,
            })
            // Refetch scores so context is fresh
            const res = await getCurrentScores(soldier.id)
            setScores(res.data)
            navigate('/dashboard')
        } catch (e) {
            setSubmitError(e.response?.data?.message || 'Failed to submit scores')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <div className="p-8 max-w-xl">

            {/* Header */}
            <div className="mb-8">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em', marginBottom: '4px' }}>
                    AFT SCORES
                </p>
                <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    {existingScores ? 'UPDATE SCORES' : 'SUBMIT SCORES'}
                </h1>
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginTop: '6px' }}>
                    Enter your official AFT scores (0–100 per event).
                    {existingScores && ' Submitting will replace your current scores on file.'}
                </p>
            </div>

            {/* Running total */}
            {allFilled && (
                <div
                    className="mb-6 px-5 py-4 rounded flex justify-between items-center"
                    style={{
                        backgroundColor: `${colors.accentGold}10`,
                        border: `1px solid ${colors.accentGoldDim}`,
                    }}
                >
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary, letterSpacing: '0.1em' }}>
                        PROJECTED TOTAL
                    </p>
                    <p style={{ fontFamily: fonts.mono, fontSize: '1.6rem', color: totalScore >= 400 ? colors.success : totalScore >= 300 ? colors.accentGold : colors.danger }}>
                        {totalScore} <span style={{ fontSize: '0.9rem', color: colors.textSecondary }}>/ 500</span>
                    </p>
                </div>
            )}

            {/* Event inputs */}
            {EVENTS.map(event => (
                <ScoreInput
                    key={event.key}
                    event={event}
                    value={eventValues[event.key]}
                    onChange={v => {
                        setEventValues(prev => ({ ...prev, [event.key]: v }))
                        if (fieldErrors[event.key]) setFieldErrors(prev => ({ ...prev, [event.key]: null }))
                    }}
                    error={fieldErrors[event.key]}
                />
            ))}

            {/* Test date */}
            <div className="mt-5 mb-6">
                <label style={{
                    fontFamily: fonts.condensed,
                    fontSize: '0.75rem',
                    letterSpacing: '0.15em',
                    color: colors.textSecondary,
                    display: 'block',
                    marginBottom: '6px',
                }}>
                    DATE OF TEST
                </label>
                <input
                    type="date"
                    value={testDate}
                    onChange={e => setTestDate(e.target.value)}
                    style={{
                        width: '100%',
                        padding: '12px 16px',
                        borderRadius: '4px',
                        backgroundColor: colors.bgCard,
                        border: `1px solid ${fieldErrors.testDate ? colors.danger : colors.border}`,
                        color: colors.textPrimary,
                        fontFamily: fonts.body,
                        fontSize: '0.95rem',
                        outline: 'none',
                    }}
                    onFocus={e => e.currentTarget.style.borderColor = colors.accentGold}
                    onBlur={e => e.currentTarget.style.borderColor = fieldErrors.testDate ? colors.danger : colors.border}
                />
                {fieldErrors.testDate && (
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.danger, marginTop: '4px' }}>
                        {fieldErrors.testDate}
                    </p>
                )}
            </div>

            {submitError && (
                <div className="mb-4 px-4 py-3 rounded" style={{
                    backgroundColor: colors.dangerDim,
                    border: `1px solid ${colors.danger}`,
                    color: colors.danger,
                    fontFamily: fonts.condensed,
                }}>
                    {submitError}
                </div>
            )}

            <div className="flex gap-3">
                <button
                    onClick={() => navigate(-1)}
                    className="py-3 rounded"
                    style={{
                        flex: '0 0 auto',
                        paddingLeft: '20px',
                        paddingRight: '20px',
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
                    onClick={handleSubmit}
                    disabled={submitting}
                    className="flex-1 py-3 rounded"
                    style={{
                        backgroundColor: colors.accentGold,
                        border: 'none',
                        color: colors.bgPrimary,
                        fontFamily: fonts.condensed,
                        fontWeight: 700,
                        letterSpacing: '0.1em',
                        cursor: 'pointer',
                        opacity: submitting ? 0.6 : 1,
                    }}
                >
                    {submitting ? 'SUBMITTING...' : 'SUBMIT SCORES'}
                </button>
            </div>
        </div>
    )
}