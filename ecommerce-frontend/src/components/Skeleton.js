import React from "react";
import MuiSkeleton from "@mui/material/Skeleton";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";

export function SkeletonBlock({ className = "", style }) {
  return <MuiSkeleton variant="rounded" className={className} style={style} />;
}

export function ProductGridSkeleton({ count = 8 }) {
  return (
    <Grid container spacing={3}>
      {Array.from({ length: count }).map((_, i) => (
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={i}>
          <Card>
            <CardContent>
              <MuiSkeleton variant="text" width="70%" height={28} />
              <MuiSkeleton variant="text" width="90%" />
              <MuiSkeleton variant="text" width="50%" sx={{ mb: 1.5 }} />
              <MuiSkeleton variant="rounded" width="100%" height={38} />
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
}

export function ListRowSkeleton({ count = 3 }) {
  return (
    <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
      {Array.from({ length: count }).map((_, i) => (
        <Box
          key={i}
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            p: 1.5,
            border: "1px solid",
            borderColor: "divider",
            borderRadius: 2,
          }}
        >
          <MuiSkeleton variant="text" width="60%" height={24} />
          <MuiSkeleton variant="rounded" width={80} height={32} />
        </Box>
      ))}
    </Box>
  );
}
