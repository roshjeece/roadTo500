import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/v1'

const api = axios.create({ baseURL: BASE_URL })

// Soldiers
export const createSoldier = (data) => api.post('/soldiers', data)
export const getSoldier = (id) => api.get(`/soldiers/${id}`)
export const updateSoldier = (id, data) => api.put(`/soldiers/${id}`, data)
export const getAllSoldiers = () => api.get('/soldiers')
export const deleteSoldier = (id) => api.delete(`/soldiers/${id}`)

// Profile
export const createProfile = (soldierId, data) => api.post(`/soldiers/${soldierId}/profile`, data)
export const getProfile = (soldierId) => api.get(`/soldiers/${soldierId}/profile`)
export const updateProfile = (soldierId, data) => api.put(`/soldiers/${soldierId}/profile`, data)

// Performance
export const submitScores = (data) => api.post('/performance', data)
export const getCurrentScores = (soldierId) => api.get(`/performance/scores/${soldierId}`)

// Plan
export const generatePlan = (soldierId, startToday) =>
    api.post(`/plan/${soldierId}?startToday=${startToday}`)
export const getActivePlan = (soldierId) =>
    api.get(`/plan/${soldierId}/active`).then(res => res.status === 204 ? { data: null } : res)

// Sessions
export const getCheckInStatus = (sessionId) => api.get(`/sessions/${sessionId}/checkins`)
export const submitCheckIn = (sessionId, data) => api.post(`/sessions/${sessionId}/checkins`, data)
export const logSessionResult = (data) => api.post('/sessions/result', data)

// Exercises
export const getAllExercises = () => api.get('/exercises')