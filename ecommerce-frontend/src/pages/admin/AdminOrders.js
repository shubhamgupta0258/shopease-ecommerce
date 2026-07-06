import React, { useEffect, useState } from "react";
import api from "../../utils/api";
import { useToast } from "../../context/ToastContext";
import { ListRowSkeleton } from "../../components/Skeleton";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { showToast } = useToast();

  const loadData = () => {
    setLoading(true);
    api.get("/orders")
      .then(res => {
        setOrders(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load orders");
        setLoading(false);
      });
  };

  useEffect(loadData, []);

  const handleDeleteOrder = async (id) => {
    try {
      await api.delete(`/orders/${id}`);
      showToast("Order deleted", "success");
      loadData();
    } catch (err) {
      console.error(err);
      showToast(err?.response?.data || "Failed to delete order", "error");
    }
  };

  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <div>
      <Typography variant="h4" gutterBottom>All Orders</Typography>

      <Paper sx={{ p: 3 }}>
        {loading ? (
          <ListRowSkeleton />
        ) : orders.length === 0 ? (
          <Typography color="text.secondary" align="center" sx={{ py: 4 }}>No orders placed yet</Typography>
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Order</TableCell>
                  <TableCell>Buyer</TableCell>
                  <TableCell>Items</TableCell>
                  <TableCell>Total</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map(o => (
                  <TableRow key={o.id}>
                    <TableCell>#{o.id}</TableCell>
                    <TableCell>{o.username}</TableCell>
                    <TableCell>{o.productNames.join(", ") || "(no items recorded)"}</TableCell>
                    <TableCell>₹{o.totalAmount}</TableCell>
                    <TableCell>{new Date(o.createdAt).toLocaleString()}</TableCell>
                    <TableCell align="right">
                      <IconButton color="error" onClick={() => handleDeleteOrder(o.id)}>
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>
    </div>
  );
}

export default AdminOrders;
