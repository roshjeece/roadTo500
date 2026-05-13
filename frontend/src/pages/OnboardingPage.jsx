import { useState } from 'react'
import { useNavigate } from 'react-router'
import { useSoldier } from '../context/SoldierContext'
import { createProfile, updateProfile } from '../api/api'
import { colors, fonts } from '../constants/theme'

// ── Helpers ──────────────────────────────────────────────────────────────────

function feetInchesToInches(feet, inches) {
    return (parseInt(feet) || 0) * 12 + (parseInt(inches) || 0)
}

function mmssToSeconds(minutes, seconds) {
    return (parseInt(minutes) || 0) * 60 + (parseInt(seconds) || 0)
}

// ── Field components ─────────────────────────────────────────────────────────

function FieldLabel({ label, hint }) {
    return (
        <div className="mb-2">
            <label style={{
                fontFamily: fonts.condensed,
                fontSize: '0.75rem',
                letterSpacing: '0.15em',
                color: colors.textSecondary,
                display: 'block',
            }}>
                {label}
            </label>
            {hint && (
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', color: colors.textSecondary, opacity: 0.7, marginTop: '2px' }}>
                    {hint}
                </p>
            )}
        </div>
    )
}

function NumberInput({ value, onChange, placeholder, min, max, suffix, error }) {
    return (
        <div>
            <div className="flex items-center gap-2">
                <input
                    type="number"
                    value={value}
                    onChange={e => onChange(e.target.value)}
                    placeholder={placeholder}
                    min={min}
                    max={max}
                    className="flex-1 px-4 py-3 rounded outline-none"
                    style={{
                        backgroundColor: colors.bgPrimary,
                        border: `1px solid ${error ? colors.danger : colors.border}`,
                        color: colors.textPrimary,
                        fontFamily: fonts.mono,
                        fontSize: '1rem',
                    }}
                    onFocus={e => !error && (e.currentTarget.style.borderColor = colors.accentGold)}
                    onBlur={e => e.currentTarget.style.borderColor = error ? colors.danger : colors.border}
                />
                {suffix && (
                    <span style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary, whiteSpace: 'nowrap' }}>
                        {suffix}
                    </span>
                )}
            </div>
            {error && (
                <p style={{ color: colors.danger, fontFamily: fonts.condensed, fontSize: '0.75rem', marginTop: '4px' }}>{error}</p>
            )}
        </div>
    )
}

// ── Progress indicator ────────────────────────────────────────────────────────

function StepDot({ active, done }) {
    return (
        <div style={{
            width: '8px',
            height: '8px',
            borderRadius: '50%',
            backgroundColor: done ? colors.success : active ? colors.accentGold : colors.border,
            transition: 'background-color 0.3s',
        }} />
    )
}

// ── Main ─────────────────────────────────────────────────────────────────────

const STEPS = [
    { key: 'physical', title: 'PHYSICAL PROFILE', subtitle: 'Your body stats' },
    { key: 'performance', title: 'PERFORMANCE BASELINES', subtitle: 'Your current capability' },
    { key: 'confirm', title: 'CONFIRM & SUBMIT', subtitle: 'Review your profile' },
]

export default function OnboardingPage() {
    const { soldier, profile, setProfile } = useSoldier()
    const navigate = useNavigate()

    const [step, setStep] = useState(0)
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState(null)

    // Physical fields
    const [weightLbs, setWeightLbs] = useState('')
    const [heightFt, setHeightFt] = useState('')
    const [heightIn, setHeightIn] = useState('')

    // Performance fields
    const [trapBar3RM, setTrapBar3RM] = useState('')
    const [lastHrpCount, setLastHrpCount] = useState('')
    const [twoMileMin, setTwoMileMin] = useState('')
    const [twoMileSec, setTwoMileSec] = useState('')
    const [benchPress1RM, setBenchPress1RM] = useState('')

    // Validation per step
    const [fieldErrors, setFieldErrors] = useState({})

    const clearError = (key) => {
        if (fieldErrors[key]) setFieldErrors(prev => ({ ...prev, [key]: null }))
    }

    const validateStep = () => {
        const errs = {}
        if (step === 0) {
            if (!weightLbs || parseInt(weightLbs) < 80 || parseInt(weightLbs) > 400)
                errs.weightLbs = 'Enter a valid weight (80–400 lbs)'
            if (!heightFt || parseInt(heightFt) < 4 || parseInt(heightFt) > 7)
                errs.heightFt = 'Enter feet (4–7)'
            if (heightIn === '' || parseInt(heightIn) < 0 || parseInt(heightIn) > 11)
                errs.heightIn = 'Enter inches (0–11)'
        }
        if (step === 1) {
            if (!trapBar3RM || parseInt(trapBar3RM) < 45)
                errs.trapBar3RM = 'Enter your trap bar 3RM (lbs)'
            if (!lastHrpCount || parseInt(lastHrpCount) < 0)
                errs.lastHrpCount = 'Enter your last HRP count'
            if (!twoMileMin || parseInt(twoMileMin) < 10 || parseInt(twoMileMin) > 30)
                errs.twoMileMin = 'Enter minutes (10–30)'
            if (twoMileSec === '' || parseInt(twoMileSec) < 0 || parseInt(twoMileSec) > 59)
                errs.twoMileSec = 'Enter seconds (0–59)'
        }
        setFieldErrors(errs)
        return Object.keys(errs).length === 0
    }

    const handleNext = () => {
        if (!validateStep()) return
        setError(null)
        setStep(s => s + 1)
    }

    const handleBack = () => {
        setError(null)
        setStep(s => s - 1)
    }

    const buildPayload = () => ({
        trapBar3RM: parseInt(trapBar3RM),
        lastHrpCount: parseInt(lastHrpCount),
        twoMileTimeSeconds: mmssToSeconds(twoMileMin, twoMileSec),
        benchPress1RM: benchPress1RM ? parseInt(benchPress1RM) : null,
        bodyWeightLbs: parseInt(weightLbs),
        heightInches: feetInchesToInches(heightFt, heightIn),
    })

    const handleSubmit = async () => {
        setSubmitting(true)
        setError(null)
        try {
            const payload = buildPayload()
            let res
            if (profile) {
                res = await updateProfile(soldier.id, payload)
            } else {
                res = await createProfile(soldier.id, payload)
            }
            setProfile(res.data)
            navigate('/scores')
        } catch (e) {
            setError(e.response?.data?.message || 'Failed to save profile. Check all fields and try again.')
        } finally {
            setSubmitting(false)
        }
    }

    const twoMileDisplay = (twoMileMin && twoMileSec !== '')
        ? `${twoMileMin}:${String(twoMileSec).padStart(2, '0')}`
        : '—'

    const heightDisplay = (heightFt && heightIn !== '')
        ? `${heightFt}'${String(heightIn).padStart(2, '0')}"`
        : '—'

    const payload = buildPayload()

    return (
        <div className="p-8 max-w-xl">

            {/* Header */}
            <div className="mb-8">
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.75rem', color: colors.textSecondary, letterSpacing: '0.2em', marginBottom: '4px' }}>
                    SETUP
                </p>
                <h1 style={{ fontFamily: fonts.heading, fontSize: '2.5rem', color: colors.textPrimary, letterSpacing: '0.05em' }}>
                    FITNESS PROFILE
                </h1>
                <p style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, marginTop: '6px' }}>
                    This calibrates your plan. All values can be updated later.
                </p>

                {/* Step dots */}
                <div className="flex items-center gap-2 mt-5">
                    {STEPS.map((s, i) => (
                        <StepDot key={s.key} active={i === step} done={i < step} />
                    ))}
                    <span style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', color: colors.textSecondary, letterSpacing: '0.1em', marginLeft: '8px' }}>
                        {STEPS[step].title}
                    </span>
                </div>
            </div>

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

            {/* ── Step 0: Physical ── */}
            {step === 0 && (
                <div
                    className="p-6 rounded mb-6"
                    style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
                >
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '20px' }}>
                        PHYSICAL STATS
                    </p>

                    <div className="mb-5">
                        <FieldLabel label="BODY WEIGHT" hint="In pounds, without gear" />
                        <NumberInput
                            value={weightLbs}
                            onChange={v => { setWeightLbs(v); clearError('weightLbs') }}
                            placeholder="185"
                            min={80} max={400}
                            suffix="lbs"
                            error={fieldErrors.weightLbs}
                        />
                    </div>

                    <div className="mb-2">
                        <FieldLabel label="HEIGHT" />
                        <div className="flex gap-3">
                            <div className="flex-1">
                                <NumberInput
                                    value={heightFt}
                                    onChange={v => { setHeightFt(v); clearError('heightFt') }}
                                    placeholder="5"
                                    min={4} max={7}
                                    suffix="ft"
                                    error={fieldErrors.heightFt}
                                />
                            </div>
                            <div className="flex-1">
                                <NumberInput
                                    value={heightIn}
                                    onChange={v => { setHeightIn(v); clearError('heightIn') }}
                                    placeholder="10"
                                    min={0} max={11}
                                    suffix="in"
                                    error={fieldErrors.heightIn}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* ── Step 1: Performance ── */}
            {step === 1 && (
                <div
                    className="p-6 rounded mb-6"
                    style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
                >
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '20px' }}>
                        PERFORMANCE BASELINES
                    </p>

                    <div className="mb-5">
                        <FieldLabel
                            label="TRAP BAR 3-REP MAX"
                            hint="The most you can deadlift for 3 reps"
                        />
                        <NumberInput
                            value={trapBar3RM}
                            onChange={v => { setTrapBar3RM(v); clearError('trapBar3RM') }}
                            placeholder="275"
                            min={45}
                            suffix="lbs"
                            error={fieldErrors.trapBar3RM}
                        />
                    </div>

                    <div className="mb-5">
                        <FieldLabel
                            label="LAST HRP COUNT"
                            hint="Hand-release push-ups completed in your most recent effort"
                        />
                        <NumberInput
                            value={lastHrpCount}
                            onChange={v => { setLastHrpCount(v); clearError('lastHrpCount') }}
                            placeholder="42"
                            min={0}
                            suffix="reps"
                            error={fieldErrors.lastHrpCount}
                        />
                    </div>

                    <div className="mb-5">
                        <FieldLabel
                            label="2-MILE RUN TIME"
                            hint="Your most recent two-mile run"
                        />
                        <div className="flex items-center gap-3">
                            <div className="flex-1">
                                <NumberInput
                                    value={twoMileMin}
                                    onChange={v => { setTwoMileMin(v); clearError('twoMileMin') }}
                                    placeholder="14"
                                    min={10} max={30}
                                    suffix="min"
                                    error={fieldErrors.twoMileMin}
                                />
                            </div>
                            <div className="flex-1">
                                <NumberInput
                                    value={twoMileSec}
                                    onChange={v => { setTwoMileSec(v); clearError('twoMileSec') }}
                                    placeholder="30"
                                    min={0} max={59}
                                    suffix="sec"
                                    error={fieldErrors.twoMileSec}
                                />
                            </div>
                        </div>
                    </div>

                    <div>
                        <FieldLabel
                            label="BENCH PRESS 1RM"
                            hint="Optional — used to estimate push strength if provided"
                        />
                        <NumberInput
                            value={benchPress1RM}
                            onChange={setBenchPress1RM}
                            placeholder="Leave blank to skip"
                            min={45}
                            suffix="lbs"
                        />
                    </div>
                </div>
            )}

            {/* ── Step 2: Confirm ── */}
            {step === 2 && (
                <div
                    className="p-6 rounded mb-6"
                    style={{ backgroundColor: colors.bgCard, border: `1px solid ${colors.border}` }}
                >
                    <p style={{ fontFamily: fonts.condensed, fontSize: '0.7rem', letterSpacing: '0.2em', color: colors.textSecondary, marginBottom: '20px' }}>
                        REVIEW
                    </p>

                    {[
                        { label: 'Body Weight', value: `${weightLbs} lbs` },
                        { label: 'Height', value: heightDisplay },
                        { label: 'Trap Bar 3RM', value: `${trapBar3RM} lbs` },
                        { label: 'Last HRP Count', value: `${lastHrpCount} reps` },
                        { label: '2-Mile Run', value: twoMileDisplay },
                        { label: 'Bench Press 1RM', value: benchPress1RM ? `${benchPress1RM} lbs` : 'Not provided' },
                    ].map(({ label, value }) => (
                        <div
                            key={label}
                            className="flex justify-between items-center py-3"
                            style={{ borderBottom: `1px solid ${colors.border}` }}
                        >
                            <span style={{ fontFamily: fonts.condensed, fontSize: '0.85rem', color: colors.textSecondary, letterSpacing: '0.05em' }}>
                                {label}
                            </span>
                            <span style={{ fontFamily: fonts.mono, fontSize: '0.9rem', color: colors.textPrimary }}>
                                {value}
                            </span>
                        </div>
                    ))}

                    <div className="mt-5 px-4 py-3 rounded" style={{
                        backgroundColor: `${colors.accentGold}10`,
                        border: `1px solid ${colors.accentGoldDim}`,
                    }}>
                        <p style={{ fontFamily: fonts.condensed, fontSize: '0.8rem', color: colors.textSecondary }}>
                            Next step: submit your AFT scores to generate your first plan.
                        </p>
                    </div>
                </div>
            )}

            {/* Navigation */}
            <div className="flex gap-3">
                {step > 0 && (
                    <button
                        onClick={handleBack}
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
                        ← BACK
                    </button>
                )}

                {step < 2 ? (
                    <button
                        onClick={handleNext}
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
                        NEXT →
                    </button>
                ) : (
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
                        {submitting ? 'SAVING...' : 'SAVE PROFILE'}
                    </button>
                )}
            </div>
        </div>
    )
}