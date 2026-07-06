import React, { useEffect, useState } from "react";
import api from "../utils/api";
import { ListRowSkeleton } from "../components/Skeleton";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.get("/orders/me")
      .then(res => {
        setOrders(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load orders");
        setLoading(false);
      });
  }, []);

  if (loading) return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Your Orders</Typography>
      <ListRowSkeleton />
    </Container>
  );
  if (error) return <Container sx={{ mt: 4 }}><Typography color="error">{error}</Typography></Container>;
  if (!orders || orders.length === 0)
    return (
      <Container sx={{ mt: 4 }}>
        <Typography color="text.secondary" align="center" sx={{ py: 6 }}>You have no orders yet</Typography>
      </Container>
    );

  return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>Your Orders</Typography>

      <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
        {orders.map((order) => (
          <Paper key={order.id} sx={{ p: 2 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", color: "text.secondary", fontWeight: 600, mb: 0.5 }}>
              <span>Order #{order.id}</span>
              <span>{new Date(order.createdAt).toLocaleString()}</span>
            </Box>
            <Typography variant="body2" color="text.secondary">
              {order.productNames.join(", ")}
            </Typography>
            <Typography variant="subtitle1" color="primary" fontWeight={700}>₹{order.totalAmount}</Typography>
          </Paper>
        ))}
      </Box>
    </Container>
  );
}

export default Orders;
