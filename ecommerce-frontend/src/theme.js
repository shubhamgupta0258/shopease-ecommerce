import { createTheme } from "@mui/material/styles";

const GRADIENT = "linear-gradient(135deg, #6E56CF 0%, #5E6AD2 100%)";
const GRADIENT_HOVER = "linear-gradient(135deg, #7C64E8 0%, #6C78E8 100%)";

const theme = createTheme({
  palette: {
    mode: "dark",
    primary: {
      main: "#5E6AD2",
      dark: "#4B56B8",
      light: "#7C64E8",
      contrastText: "#ffffff",
    },
    error: {
      main: "#F87171",
      dark: "#EF4444",
    },
    success: {
      main: "#4ADE80",
    },
    background: {
      default: "#0B0B10",
      paper: "#16161D",
    },
    text: {
      primary: "#F7F8F8",
      secondary: "#8A8F98",
    },
    divider: "rgba(255, 255, 255, 0.08)",
  },
  shape: {
    borderRadius: 12,
  },
  typography: {
    fontFamily: [
      "-apple-system",
      "BlinkMacSystemFont",
      '"Segoe UI"',
      "Roboto",
      "Oxygen",
      "Ubuntu",
      "Cantarell",
      '"Fira Sans"',
      '"Droid Sans"',
      '"Helvetica Neue"',
      "sans-serif",
    ].join(","),
    h4: { fontWeight: 700 },
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundImage:
            "radial-gradient(ellipse 80% 50% at 50% -20%, rgba(94, 106, 210, 0.25), transparent)",
          backgroundRepeat: "no-repeat",
        },
      },
    },
    MuiButton: {
      defaultProps: {
        disableElevation: true,
      },
      styleOverrides: {
        root: {
          textTransform: "none",
          fontWeight: 600,
          borderRadius: 999,
          transition: "transform 0.15s ease, box-shadow 0.15s ease, opacity 0.15s ease",
        },
        contained: {
          backgroundImage: GRADIENT,
          boxShadow: "0 4px 20px rgba(94, 106, 210, 0.35)",
          "&:hover": {
            backgroundImage: GRADIENT_HOVER,
            boxShadow: "0 6px 28px rgba(94, 106, 210, 0.55)",
            transform: "translateY(-1px)",
          },
          "&:active": {
            transform: "translateY(0)",
          },
          "&.Mui-disabled": {
            backgroundImage: "none",
            background: "rgba(255, 255, 255, 0.08)",
            color: "rgba(255, 255, 255, 0.3)",
            boxShadow: "none",
          },
        },
        outlined: {
          borderColor: "rgba(255, 255, 255, 0.16)",
          "&:hover": {
            borderColor: "#5E6AD2",
            backgroundColor: "rgba(94, 106, 210, 0.08)",
          },
        },
        text: {
          "&:hover": {
            backgroundColor: "rgba(255, 255, 255, 0.06)",
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: "1px solid rgba(255, 255, 255, 0.08)",
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: "1px solid rgba(255, 255, 255, 0.08)",
          transition: "transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease",
          "&:hover": {
            transform: "translateY(-3px)",
            borderColor: "rgba(94, 106, 210, 0.5)",
            boxShadow: "0 12px 32px rgba(0, 0, 0, 0.4)",
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: "rgba(11, 11, 16, 0.8)",
          backdropFilter: "blur(12px)",
          backgroundImage: "none",
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          backgroundColor: "rgba(94, 106, 210, 0.15)",
          color: "#B4BCF0",
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundImage: "none",
          borderRight: "1px solid rgba(255, 255, 255, 0.08)",
        },
      },
    },
  },
});

export default theme;
