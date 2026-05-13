import { Routes, Route, Navigate } from 'react-router'
import { useSoldier } from './context/SoldierContext'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import OnboardingPage from './pages/OnboardingPage'
import PlanPage from './pages/PlanPage'
import SessionPage from './pages/SessionPage'
import ScoresPage from './pages/ScoresPage'
import ProfilePage from './pages/ProfilePage'

function ProtectedRoute({ children }) {
  const { soldier, loading } = useSoldier()
  if (loading) return null
  if (!soldier) return <Navigate to="/login" replace />
  return children
}

export default function App() {
  const { soldier, loading } = useSoldier()
  if (loading) return null

  return (
      <Routes>
        <Route path="/login" element={
          soldier ? <Navigate to="/dashboard" replace /> : <LoginPage />
        } />
        <Route path="/" element={
          <ProtectedRoute><Layout /></ProtectedRoute>
        }>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="onboarding" element={<OnboardingPage />} />
          <Route path="plan" element={<PlanPage />} />
          <Route path="session/:sessionId" element={<SessionPage />} />
          <Route path="scores" element={<ScoresPage />} />
          <Route path="profile" element={<ProfilePage />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
  )
}