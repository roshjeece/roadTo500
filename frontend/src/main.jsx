import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router'
import { ThemeProvider, createTheme } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import { muiTheme } from './constants/theme'
import { SoldierProvider } from './context/SoldierContext'
import App from './App'
import './index.css'

const theme = createTheme(muiTheme)

createRoot(document.getElementById('root')).render(
        <BrowserRouter>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <SoldierProvider>
                    <App />
                </SoldierProvider>
            </ThemeProvider>
        </BrowserRouter>
)