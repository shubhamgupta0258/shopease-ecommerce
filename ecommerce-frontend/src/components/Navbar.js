import React from "react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Badge from "@mui/material/Badge";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import { useCart } from "../context/CartContext";

export default function Navbar() {
  const navigate = useNavigate();
  const { cartCount, refreshCartCount } = useCart();
  const isLoggedIn = !!localStorage.getItem("accessToken");
  const isAdmin = localStorage.getItem("userRole") === "ROLE_ADMIN";

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
    refreshCartCount();
    navigate("/login", { replace: true });
  };

  return (
    <AppBar position="sticky" color="inherit" elevation={0} sx={{ borderBottom: "1px solid", borderColor: "divider" }}>
      <Toolbar sx={{ justifyContent: "space-between" }}>
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Button component={RouterLink} to="/" color="inherit">Home</Button>

          {!isAdmin && (
            <Button component={RouterLink} to="/cart" color="inherit" startIcon={
              <Badge badgeContent={cartCount} color="primary">
                <ShoppingCartIcon />
              </Badge>
            }>
              Cart
            </Button>
          )}

          {isLoggedIn && !isAdmin && (
            <Button component={RouterLink} to="/orders" color="inherit" startIcon={<ReceiptLongIcon />}>
              Orders
            </Button>
          )}

          {isAdmin && (
            <Button component={RouterLink} to="/admin" color="inherit" startIcon={<AdminPanelSettingsIcon />}>
              Admin
            </Button>
          )}
        </Box>

        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          {!isLoggedIn && <Button component={RouterLink} to="/login" color="inherit">Login</Button>}
          {!isLoggedIn && <Button component={RouterLink} to="/register" variant="contained">Sign up</Button>}
          {isLoggedIn && (
            <Button variant="outlined" onClick={handleLogout}>Logout</Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
