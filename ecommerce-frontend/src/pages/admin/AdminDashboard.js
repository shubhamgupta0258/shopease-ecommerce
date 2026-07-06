import React, { useEffect, useState } from "react";
import api from "../../utils/api";
import { SkeletonBlock } from "../../components/Skeleton";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";

function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([
      api.get("/products", { params: { size: 1 } }),
      api.get("/categories"),
      api.get("/orders"),
    ])
      .then(([productsRes, categoriesRes, ordersRes]) => {
        const orders = ordersRes.data;
        const revenue = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);

        setStats({
          totalProducts: productsRes.data.totalElements,
          totalCategories: categoriesRes.data.length,
          totalOrders: orders.length,
          totalRevenue: revenue,
        });
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load dashboard data");
        setLoading(false);
      });
  }, []);

  if (error) return <Typography color="error">{error}</Typography>;

  const cards = loading
    ? null
    : [
        { label: "Products", value: stats.totalProducts },
        { label: "Categories", value: stats.totalCategories },
        { label: "Orders", value: stats.totalOrders },
        { label: "Total Revenue", value: `₹${stats.totalRevenue.toFixed(2)}` },
      ];

  return (
    <div>
      <Typography variant="h4" gutterBottom>Dashboard</Typography>

      <Grid container spacing={3}>
        {loading
          ? Array.from({ length: 4 }).map((_, i) => (
              <Grid size={{ xs: 12, sm: 6, md: 3 }} key={i}>
                <Paper sx={{ p: 3, textAlign: "center" }}>
                  <SkeletonBlock style={{ height: 32, width: "60%", margin: "0 auto 8px" }} />
                  <SkeletonBlock style={{ height: 14, width: "40%", margin: "0 auto" }} />
                </Paper>
              </Grid>
            ))
          : cards.map(c => (
              <Grid size={{ xs: 12, sm: 6, md: 3 }} key={c.label}>
                <Paper sx={{ p: 3, textAlign: "center" }}>
                  <Typography variant="h4" color="primary" fontWeight={700}>{c.value}</Typography>
                  <Typography variant="body2" color="text.secondary" mt={0.5}>{c.label}</Typography>
                </Paper>
              </Grid>
            ))}
      </Grid>
    </div>
  );
}

export default AdminDashboard;
