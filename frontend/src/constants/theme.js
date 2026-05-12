export const colors = {
    bgPrimary: '#0f1a0f',
    bgSecondary: '#1a2a1a',
    bgCard: '#162016',
    bgCardHover: '#1e2e1e',
    accentGold: '#c9a84c',
    accentGoldBright: '#f0c040',
    accentGoldDim: '#8a6a2a',
    textPrimary: '#e8e8d8',
    textSecondary: '#8a9a7a',
    border: '#2a3a2a',
    borderBright: '#3a5a3a',
    danger: '#c44a4a',
    dangerDim: '#8a2a2a',
    success: '#4a8a4a',
    successDim: '#2a5a2a',
}

export const fonts = {
    heading: "'Bebas Neue', sans-serif",
    body: "'Barlow', sans-serif",
    condensed: "'Barlow Condensed', sans-serif",
    mono: "'JetBrains Mono', monospace",
}

export const muiTheme = {
    palette: {
        mode: 'dark',
        primary: {
            main: '#c9a84c',
            light: '#f0c040',
            dark: '#8a6a2a',
        },
        secondary: {
            main: '#4a8a4a',
        },
        background: {
            default: '#0f1a0f',
            paper: '#162016',
        },
        text: {
            primary: '#e8e8d8',
            secondary: '#8a9a7a',
        },
        error: {
            main: '#c44a4a',
        },
        divider: '#2a3a2a',
    },
    typography: {
        fontFamily: "'Barlow', sans-serif",
        h1: { fontFamily: "'Bebas Neue', sans-serif" },
        h2: { fontFamily: "'Bebas Neue', sans-serif" },
        h3: { fontFamily: "'Bebas Neue', sans-serif" },
        h4: { fontFamily: "'Bebas Neue', sans-serif" },
        h5: { fontFamily: "'Bebas Neue', sans-serif" },
        h6: { fontFamily: "'Bebas Neue', sans-serif" },
    },
    components: {
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: 'none',
                    backgroundColor: '#162016',
                    border: '1px solid #2a3a2a',
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    fontFamily: "'Barlow Condensed', sans-serif",
                    fontWeight: 600,
                    letterSpacing: '0.05em',
                    textTransform: 'uppercase',
                },
            },
        },
        MuiTextField: {
            styleOverrides: {
                root: {
                    '& .MuiOutlinedInput-root': {
                        '& fieldset': { borderColor: '#2a3a2a' },
                        '&:hover fieldset': { borderColor: '#c9a84c' },
                        '&.Mui-focused fieldset': { borderColor: '#c9a84c' },
                    },
                },
            },
        },
    },
}