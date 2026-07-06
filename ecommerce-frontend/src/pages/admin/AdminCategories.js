import React, { useEffect, useState } from "react";
import api from "../../utils/api";
import { useToast } from "../../context/ToastContext";
import { ListRowSkeleton } from "../../components/Skeleton";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";

function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categoryForm, setCategoryForm] = useState({ name: "" });
  const { showToast } = useToast();

  const loadData = () => {
    setLoading(true);
    api.get("/categories")
      .then(res => {
        setCategories(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load categories");
        setLoading(false);
      });
  };

  useEffect(loadData, []);

  const handleAddCategory = async (e) => {
    e.preventDefault();
    try {
      await api.post("/categories", { name: categoryForm.name });
      showToast("Category added", "success");
      setCategoryForm({ name: "" });
      loadData();
    } catch (err) {
      console.error(err);
      showToast("Failed to add category", "error");
    }
  };

  const handleDeleteCategory = async (id) => {
    try {
      await api.delete(`/categories/${id}`);
      showToast("Category deleted", "success");
      loadData();
    } catch (err) {
      console.error(err);
      showToast("Failed to delete category. It may still be assigned to products.", "error");
    }
  };

  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <div>
      <Typography variant="h4" gutterBottom>Categories</Typography>

      <Paper sx={{ p: 3 }}>
        <Box component="form" onSubmit={handleAddCategory} sx={{ display: "flex", gap: 2, mb: 3 }}>
          <TextField
            label="Category name"
            size="small"
            value={categoryForm.name}
            onChange={e => setCategoryForm({ name: e.target.value })}
            required
          />
          <Button type="submit" variant="contained">Add Category</Button>
        </Box>

        {loading ? (
          <ListRowSkeleton />
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {categories.map(c => (
                  <TableRow key={c.id}>
                    <TableCell>{c.name}</TableCell>
                    <TableCell align="right">
                      <IconButton color="error" onClick={() => handleDeleteCategory(c.id)}>
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

export default AdminCategories;
