import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../utils/api';
import { useToast } from '../context/ToastContext';
import { useCart } from '../context/CartContext';
import { ProductGridSkeleton } from '../components/Skeleton';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import CardMedia from '@mui/material/CardMedia';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Pagination from '@mui/material/Pagination';
import Box from '@mui/material/Box';

const emptyFilters = { search: "", categoryId: "", minPrice: "", maxPrice: "", sort: "id,asc" };

function Home() {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [filters, setFilters] = useState(emptyFilters);
    const [appliedFilters, setAppliedFilters] = useState(emptyFilters);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const { showToast } = useToast();
    const { refreshCartCount } = useCart();

    useEffect(() => {
        api.get("/categories")
            .then(res => setCategories(res.data))
            .catch(() => setCategories([]));
    }, []);

    useEffect(() => {
        setLoading(true);
        const params = { page, size: 12, sort: appliedFilters.sort };
        if (appliedFilters.search) params.search = appliedFilters.search;
        if (appliedFilters.categoryId) params.categoryId = appliedFilters.categoryId;
        if (appliedFilters.minPrice) params.minPrice = appliedFilters.minPrice;
        if (appliedFilters.maxPrice) params.maxPrice = appliedFilters.maxPrice;

        api.get("/products", { params })
            .then(res => {
                setProducts(res.data.content);
                setTotalPages(res.data.totalPages);
                setLoading(false);
            })
            .catch(() => {
                setError("Could not fetch products");
                setLoading(false);
            });
    }, [appliedFilters, page]);

    const handleApplyFilters = (e) => {
        e.preventDefault();
        setPage(0);
        setAppliedFilters(filters);
    };

    const handleAddToCart = async (productId) => {
        const token = localStorage.getItem("accessToken");

        if (!token) {
            showToast("Please login first.", "error");
            return;
        }

        try {
            await api.post("/cart/add", [productId]);
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
            <Typography variant="h4" gutterBottom>Products</Typography>

            <Paper component="form" onSubmit={handleApplyFilters} sx={{ p: 2, mb: 3 }}>
                <Stack direction="row" spacing={2} useFlexGap sx={{ flexWrap: "wrap", alignItems: "center" }}>
                    <TextField
                        label="Search products..."
                        size="small"
                        value={filters.search}
                        onChange={e => setFilters({ ...filters, search: e.target.value })}
                        sx={{ minWidth: 200 }}
                    />

                    <TextField
                        select
                        label="Category"
                        size="small"
                        value={filters.categoryId}
                        onChange={e => setFilters({ ...filters, categoryId: e.target.value })}
                        sx={{ minWidth: 160 }}
                    >
                        <MenuItem value="">All categories</MenuItem>
                        {categories.map(c => (
                            <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
                        ))}
                    </TextField>

                    <TextField
                        label="Min ₹"
                        type="number"
                        size="small"
                        value={filters.minPrice}
                        onChange={e => setFilters({ ...filters, minPrice: e.target.value })}
                        sx={{ width: 110 }}
                    />

                    <TextField
                        label="Max ₹"
                        type="number"
                        size="small"
                        value={filters.maxPrice}
                        onChange={e => setFilters({ ...filters, maxPrice: e.target.value })}
                        sx={{ width: 110 }}
                    />

                    <TextField
                        select
                        label="Sort"
                        size="small"
                        value={filters.sort}
                        onChange={e => setFilters({ ...filters, sort: e.target.value })}
                        sx={{ minWidth: 170 }}
                    >
                        <MenuItem value="id,asc">Default</MenuItem>
                        <MenuItem value="price,asc">Price: Low to High</MenuItem>
                        <MenuItem value="price,desc">Price: High to Low</MenuItem>
                        <MenuItem value="name,asc">Name: A to Z</MenuItem>
                    </TextField>

                    <Button type="submit" variant="contained">Apply</Button>
                </Stack>
            </Paper>

            {loading ? (
                <ProductGridSkeleton />
            ) : products.length === 0 ? (
                <Typography color="text.secondary" align="center" sx={{ py: 6 }}>
                    No products match your filters
                </Typography>
            ) : (
                <>
                    <Grid container spacing={3}>
                        {products.map(prod => (
                            <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={prod.id}>
                                <Card sx={{ height: "100%", display: "flex", flexDirection: "column" }}>
                                    <CardMedia
                                        component="img"
                                        height="160"
                                        image={prod.imageUrl || `https://picsum.photos/seed/product${prod.id}/400/300`}
                                        alt={prod.name}
                                        sx={{ objectFit: "cover" }}
                                    />
                                    <CardContent sx={{ flexGrow: 1 }}>
                                        <Typography
                                            component={Link}
                                            to={`/product/${prod.id}`}
                                            variant="h6"
                                            sx={{ textDecoration: "none", color: "text.primary", display: "block" }}
                                        >
                                            {prod.name}
                                        </Typography>
                                        {prod.categoryName && (
                                            <Chip label={prod.categoryName} size="small" sx={{ mt: 1, mb: 1 }} />
                                        )}
                                        <Typography variant="body2" color="text.secondary" gutterBottom>
                                            {prod.description}
                                        </Typography>
                                        <Typography variant="h6" color="primary">₹{prod.price}</Typography>
                                    </CardContent>
                                    <CardActions>
                                        <Button
                                            variant="contained"
                                            fullWidth
                                            onClick={() => handleAddToCart(prod.id)}
                                        >
                                            Add to Cart
                                        </Button>
                                    </CardActions>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>

                    <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
                        <Pagination
                            count={totalPages}
                            page={page + 1}
                            onChange={(_e, value) => setPage(value - 1)}
                            color="primary"
                        />
                    </Box>
                </>
            )}
        </Container>
    );
}
export default Home;
