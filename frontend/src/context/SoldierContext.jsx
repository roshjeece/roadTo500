import { createContext, useContext, useState, useEffect } from 'react'
import { getSoldier } from '../api/api'

const SoldierContext = createContext(null)

export function SoldierProvider({ children }) {
    const [soldier, setSoldier] = useState(null)
    const [profile, setProfile] = useState(null)
    const [scores, setScores] = useState(null)
    const [activePlan, setActivePlan] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const savedId = localStorage.getItem('soldierId')
        if (savedId) {
            getSoldier(parseInt(savedId))
                .then(res => setSoldier(res.data))
                .catch(() => localStorage.removeItem('soldierId'))
                .finally(() => setLoading(false))
        } else {
            setLoading(false)
        }
    }, [])

    const login = (soldierData) => {
        localStorage.setItem('soldierId', soldierData.id)
        setSoldier(soldierData)
    }

    const logout = () => {
        localStorage.removeItem('soldierId')
        setSoldier(null)
        setProfile(null)
        setScores(null)
        setActivePlan(null)
    }

    return (
        <SoldierContext.Provider value={{
            soldier, setSoldier,
            profile, setProfile,
            scores, setScores,
            activePlan, setActivePlan,
            loading, login, logout
        }}>
            {children}
        </SoldierContext.Provider>
    )
}

export const useSoldier = () => useContext(SoldierContext)