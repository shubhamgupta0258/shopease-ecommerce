// src/components/AdminRoute.js
import React from "react";
import { Navigate } from "react-router-dom";

export default function AdminRoute({ children }) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  const role = localStorage.getItem("userRole");
  if (role !== "ROLE_ADMIN") {
    return <Navigate to="/" replace />;
  }

  return children;
}
