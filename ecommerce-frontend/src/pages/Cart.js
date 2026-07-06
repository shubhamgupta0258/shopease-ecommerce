import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import api from "../utils/api";
import { useToast } from "../context/ToastContext";
import { useCart } from "../context/CartContext";
import { ListRowSkeleton } from "../components/Skeleton";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import Button from "@mui/material/Button";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { showToast } = useToast();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const res = await api.get("/cart"); // backend returns CartDTO
      setCart(res.data);
      setLoading(false);
    } catch (err) {
      console.error(err);
      setError("Could not load cart");
      setLoading(false);
    }
  };

  // ------------------ REMOVE 1 quantity OR remove product fully ------------------
  const handleDecrease = async (productId) => {
    try {
      await api.delete(`/cart/remove/${productId}`);
      fetchCart(); // reload
      refreshCartCount();
    } catch (err) {
      console.error(err);
      showToast("Failed to decrease quantity", "error");
    }
  };

  // ------------------ ADD 1 MORE quantity ------------------
  const handleIncrease = async (productId) => {
    try {
      await api.post("/cart/add", [productId]); // backend increases quantity
      fetchCart(); // reload
      refreshCartCount();
    } catch (err) {
      console.error(err);
      showToast("Failed to increase quantity", "error");
    }
  };

  if (error) return <Container sx={{ mt: 4 }}><Typography color="error">{error}</Typography></Container>;
  if (loading) return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Your Cart</Typography>
      <ListRowSkeleton />
    </Container>
  );
  if (!cart || !cart.products || cart.products.length === 0)
    return (
      <Container sx={{ mt: 4 }}>
        <Typography color="text.secondary" align="center" sx={{ py: 6 }}>Your cart is empty</Typography>
      </Container>
    );

  return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Your Cart</Typography>

      <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5, mb: 3 }}>
        {cart.products.map((p) => (
          <Paper key={p.id} sx={{ p: 2, display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 2 }}>
            {/* LEFT SIDE — Product info */}
            <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
              <Box
                component="img"
                src={p.imageUrl || `https://picsum.photos/seed/product${p.id}/100/100`}
                alt={p.name}
                sx={{ width: 64, height: 64, objectFit: "cover", borderRadius: 1.5, flexShrink: 0 }}
              />
              <Box>
                <Typography variant="h6">{p.name}</Typography>
                <Typography variant="body2" color="text.secondary">{p.description}</Typography>
                <Typography variant="subtitle1" color="primary" fontWeight={700}>₹{p.price}</Typography>
              </Box>
            </Box>

            {/* RIGHT SIDE — Quantity controls */}
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <IconButton size="small" aria-label="decrease quantity" onClick={() => handleDecrease(p.id)}><RemoveIcon fontSize="small" /></IconButton>
              <Typography fontWeight={700}>{p.quantity}</Typography>
              <IconButton size="small" aria-label="increase quantity" onClick={() => handleIncrease(p.id)}><AddIcon fontSize="small" /></IconButton>
            </Box>
          </Paper>
        ))}
      </Box>

      <Typography variant="h5" align="right" gutterBottom>Total Price: ₹{cart.totalPrice}</Typography>

      <Button component={Link} to="/checkout" variant="contained" size="large" fullWidth>
        Proceed to Checkout
      </Button>
    </Container>
  );
}
