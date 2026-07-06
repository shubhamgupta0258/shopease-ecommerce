import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../utils/api";
import { useToast } from "../context/ToastContext";
import { useCart } from "../context/CartContext";
import { ListRowSkeleton } from "../components/Skeleton";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";

function loadRazorpayScript() {
  return new Promise((resolve) => {
    if (window.Razorpay) {
      resolve(true);
      return;
    }
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

function Checkout() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [paying, setPaying] = useState(false);
  const [contact, setContact] = useState("");
  const [userEmail, setUserEmail] = useState("");
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    api.get("/cart")
      .then(res => {
        setCart(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load cart");
        setLoading(false);
      });

    api.get("/auth/me")
      .then(res => setUserEmail(res.data.email))
      .catch(() => {});
  }, []);

  const handlePay = async () => {
    if (!/^[6-9]\d{9}$/.test(contact)) {
      showToast("Please enter a valid 10-digit mobile number.", "error");
      return;
    }

    setPaying(true);

    const scriptLoaded = await loadRazorpayScript();
    if (!scriptLoaded) {
      showToast("Could not load payment gateway. Check your connection.", "error");
      setPaying(false);
      return;
    }

    try {
      const { data: keyData } = await api.get("/payment/key");
      const { data: orderData } = await api.post(
        `/payment/createOrder?amount=${cart.totalPrice}`
      );

      const options = {
        key: keyData.keyId,
        amount: orderData.amount,
        currency: orderData.currency,
        name: "Ecommerce Store",
        description: "Order payment",
        order_id: orderData.orderId,
        prefill: {
          contact: contact,
          email: userEmail,
        },
        handler: async (response) => {
          try {
            await api.post("/checkout", {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });
            showToast("Payment successful! Order placed.", "success");
            refreshCartCount();
            navigate("/orders");
          } catch (err) {
            console.error(err);
            showToast("Payment succeeded but order placement failed. Contact support.", "error");
          } finally {
            setPaying(false);
          }
        },
        modal: {
          ondismiss: () => setPaying(false),
        },
        theme: { color: "#4f46e5" },
      };

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (err) {
      console.error(err);
      showToast("Could not start payment. Please try again.", "error");
      setPaying(false);
    }
  };

  if (loading) return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Checkout</Typography>
      <ListRowSkeleton />
    </Container>
  );
  if (error) return <Container sx={{ mt: 4 }}><Typography color="error">{error}</Typography></Container>;
  if (!cart || !cart.products || cart.products.length === 0)
    return (
      <Container sx={{ mt: 4 }}>
        <Typography color="text.secondary" align="center" sx={{ py: 6 }}>Your cart is empty</Typography>
      </Container>
    );

  return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Checkout</Typography>

      <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5, mb: 3 }}>
        {cart.products.map((p) => (
          <Paper key={p.id} sx={{ p: 2, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <Box>
              <Typography variant="h6">{p.name}</Typography>
              <Typography variant="body2" color="text.secondary">Qty: {p.quantity}</Typography>
            </Box>
            <Typography variant="subtitle1" color="primary" fontWeight={700}>
              ₹{(p.price * p.quantity).toFixed(2)}
            </Typography>
          </Paper>
        ))}
      </Box>

      <Typography variant="h5" align="right" gutterBottom>Total: ₹{cart.totalPrice}</Typography>

      <TextField
        label="Mobile Number (for payment confirmation)"
        placeholder="10-digit mobile number"
        value={contact}
        onChange={e => setContact(e.target.value.replace(/\D/g, "").slice(0, 10))}
        fullWidth
        sx={{ mb: 2 }}
      />

      <Button variant="contained" size="large" fullWidth onClick={handlePay} disabled={paying}>
        {paying ? "Processing..." : `Pay ₹${cart.totalPrice}`}
      </Button>
    </Container>
  );
}

export default Checkout;
