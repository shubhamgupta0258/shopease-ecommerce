import React, { useEffect, useState } from "react";
import api from "../../utils/api";
import { useToast } from "../../context/ToastContext";
import { ListRowSkeleton } from "../../components/Skeleton";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";
import Button from "@mui/material/Button";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import Chip from "@mui/material/Chip";

const emptyProductForm = { name: "", description: "", price: "", quantity: "", categoryId: "", imageUrl: "" };

function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { showToast } = useToast();

  const [productForm, setProductForm] = useState(emptyProductForm);
  const [editingProductId, setEditingProductId] = useState(null);

  const loadData = () => {
    setLoading(true);
    Promise.all([
      api.get("/products", { params: { size: 100 } }),
      api.get("/categories"),
    ])
      .then(([productsRes, categoriesRes]) => {
        setProducts(productsRes.data.content);
        setCategories(categoriesRes.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load products");
        setLoading(false);
      });
  };

  useEffect(loadData, []);

  const handleSubmitProduct = async (e) => {
    e.preventDefault();
    const payload = {
      name: productForm.name,
      description: productForm.description,
      price: parseFloat(productForm.price),
      quantity: productForm.quantity ? parseInt(productForm.quantity, 10) : null,
      categoryId: productForm.categoryId ? parseInt(productForm.categoryId, 10) : null,
      imageUrl: productForm.imageUrl || null,
    };

    try {
      if (editingProductId) {
        await api.put(`/products/${editingProductId}`, payload);
      } else {
        await api.post("/products", payload);
      }
      showToast(editingProductId ? "Product updated" : "Product added", "success");
      setProductForm(emptyProductForm);
      setEditingProductId(null);
      loadData();
    } catch (err) {
      console.error(err);
      showToast(editingProductId ? "Failed to update product" : "Failed to add product", "error");
    }
  };

  const handleEditProduct = (p) => {
    setEditingProductId(p.id);
    setProductForm({
      name: p.name,
      description: p.description || "",
      price: p.price,
      quantity: p.quantity ?? "",
      categoryId: p.categoryId ?? "",
      imageUrl: p.imageUrl || "",
    });
  };

  const handleCancelEdit = () => {
    setEditingProductId(null);
    setProductForm(emptyProductForm);
  };

  const handleDeleteProduct = async (id) => {
    try {
      await api.delete(`/products/${id}`);
      showToast("Product deleted", "success");
      loadData();
    } catch (err) {
      console.error(err);
      showToast(err?.response?.data || "Failed to delete product", "error");
    }
  };

  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <div>
      <Typography variant="h4" gutterBottom>Products</Typography>

      <Paper sx={{ p: 3 }}>
        <Box component="form" onSubmit={handleSubmitProduct} sx={{ display: "flex", gap: 2, flexWrap: "wrap", mb: 3, alignItems: "center" }}>
          <TextField
            label="Name"
            size="small"
            value={productForm.name}
            onChange={e => setProductForm({ ...productForm, name: e.target.value })}
            required
          />
          <TextField
            label="Description"
            size="small"
            value={productForm.description}
            onChange={e => setProductForm({ ...productForm, description: e.target.value })}
          />
          <TextField
            label="Price"
            type="number"
            size="small"
            sx={{ width: 110 }}
            value={productForm.price}
            onChange={e => setProductForm({ ...productForm, price: e.target.value })}
            required
          />
          <TextField
            label="Quantity"
            type="number"
            size="small"
            sx={{ width: 110 }}
            value={productForm.quantity}
            onChange={e => setProductForm({ ...productForm, quantity: e.target.value })}
          />
          <TextField
            select
            label="Category"
            size="small"
            sx={{ minWidth: 160 }}
            value={productForm.categoryId}
            onChange={e => setProductForm({ ...productForm, categoryId: e.target.value })}
          >
            <MenuItem value="">No category</MenuItem>
            {categories.map(c => (
              <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
            ))}
          </TextField>
          <TextField
            label="Image URL"
            size="small"
            sx={{ minWidth: 220 }}
            value={productForm.imageUrl}
            onChange={e => setProductForm({ ...productForm, imageUrl: e.target.value })}
          />
          <Button type="submit" variant="contained">
            {editingProductId ? "Update Product" : "Add Product"}
          </Button>
          {editingProductId && (
            <Button type="button" onClick={handleCancelEdit}>Cancel</Button>
          )}
        </Box>

        {loading ? (
          <ListRowSkeleton />
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Image</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Price</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {products.map(p => (
                  <TableRow key={p.id}>
                    <TableCell>
                      <Box
                        component="img"
                        src={p.imageUrl || `https://picsum.photos/seed/product${p.id}/100/100`}
                        alt={p.name}
                        sx={{ width: 48, height: 48, objectFit: "cover", borderRadius: 1 }}
                      />
                    </TableCell>
                    <TableCell>{p.name}</TableCell>
                    <TableCell>₹{p.price}</TableCell>
                    <TableCell>{p.categoryName ? <Chip label={p.categoryName} size="small" /> : "—"}</TableCell>
                    <TableCell align="right">
                      <IconButton onClick={() => handleEditProduct(p)}>
                        <EditIcon />
                      </IconButton>
                      <IconButton color="error" onClick={() => handleDeleteProduct(p.id)}>
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

export default AdminProducts;
