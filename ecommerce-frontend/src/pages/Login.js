import React, { useState } from "react";
import api from "../utils/api";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Alert from "@mui/material/Alert";
import Stack from "@mui/material/Stack";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { refreshCartCount } = useCart();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Change "/auth/login" if your backend login path differs
      const res = await api.post("/auth/login", { email, password });

      const accessToken = res?.data?.accessToken;
      const refreshToken = res?.data?.refreshToken;

      if (!accessToken) {
        setError("Login failed.");
        setLoading(false);
        return;
      }

      localStorage.setItem("accessToken", accessToken);
      if (refreshToken) {
        localStorage.setItem("refreshToken", refreshToken);
      }

      try {
        const meRes = await api.get("/auth/me");
        localStorage.setItem("userRole", meRes.data.role);
      } catch (err) {
        console.error("Could not fetch user role", err);
      }

      refreshCartCount();
      navigate("/", { replace: true });

    } catch (err) {
      setError("Invalid email or password");
    }

    setLoading(false);
  };

  return (
    <Container maxWidth="xs" sx={{ mt: 8 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h5" gutterBottom>Login</Typography>

        <Box component="form" onSubmit={handleSubmit}>
          <Stack spacing={2}>
            <TextField
              label="Email"
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              fullWidth
            />

            <TextField
              label="Password"
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              fullWidth
            />

            {error && <Alert severity="error">{error}</Alert>}

            <Button type="submit" variant="contained" size="large" fullWidth disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </Button>
          </Stack>
        </Box>
      </Paper>
    </Container>
  );
}
