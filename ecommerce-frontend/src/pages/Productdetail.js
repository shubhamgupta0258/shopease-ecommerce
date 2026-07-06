import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../utils/api";
import { useToast } from "../context/ToastContext";
import { useCart } from "../context/CartContext";
import { SkeletonBlock } from "../components/Skeleton";
import Container from "@mui/material/Container";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Stack from "@mui/material/Stack";
import Box from "@mui/material/Box";

function Productdetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { showToast } = useToast();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    api.get(`/products/${id}`)
      .then(res => {
        setProduct(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Could not load product");
        setLoading(false);
      });
  }, [id]);

  const handleAddToCart = async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      showToast("Please login first.", "error");
      return;
    }
    try {
      await api.post("/cart/add", [product.id]);
      showToast("Product added to cart", "success");
      refreshCartCount();
    } catch (err) {
      console.error(err);
      showToast("Failed to add product to cart", "error");
    }
  };

  if (error) return <Container sx={{ mt: 4 }}><Typography color="error">{error}</Typography></Container>;

  return (
    <Container sx={{ mt: 4, mb: 4 }}>
      <Button component={Link} to="/" sx={{ mb: 2 }}>&larr; Back to products</Button>

      {loading ? (
        <Paper sx={{ p: 3, maxWidth: 700 }}>
          <Stack spacing={1.5}>
            <SkeletonBlock style={{ height: 240, width: "100%" }} />
            <SkeletonBlock style={{ height: 24, width: "50%" }} />
            <SkeletonBlock style={{ height: 18, width: "30%" }} />
            <SkeletonBlock style={{ height: 14, width: "90%" }} />
            <SkeletonBlock style={{ height: 38, width: "100%" }} />
          </Stack>
        </Paper>
      ) : (
        <Paper sx={{ p: 3, maxWidth: 700, display: "flex", flexDirection: { xs: "column", sm: "row" }, gap: 3 }}>
          <Box
            component="img"
            src={product.imageUrl || `https://picsum.photos/seed/product${product.id}/500/400`}
            alt={product.name}
            sx={{ width: { xs: "100%", sm: 260 }, height: 220, objectFit: "cover", borderRadius: 2, flexShrink: 0 }}
          />
          <Stack spacing={1.5} sx={{ flexGrow: 1 }}>
            <Typography variant="h5">{product.name}</Typography>
            <Typography variant="h6" color="primary">₹{product.price}</Typography>
            <Typography variant="body2" color="text.secondary">{product.description}</Typography>
            <Button variant="contained" onClick={handleAddToCart} sx={{ alignSelf: "flex-start" }}>
              Add to Cart
            </Button>
          </Stack>
        </Paper>
      )}
    </Container>
  );
}

export default Productdetail;
