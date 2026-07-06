import React, { createContext, useCallback, useContext, useEffect, useState } from "react";
import api from "../utils/api";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [cartCount, setCartCount] = useState(0);

  const refreshCartCount = useCallback(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      setCartCount(0);
      return;
    }
    api.get("/cart")
      .then(res => {
        const total = (res.data.products || []).reduce((sum, p) => sum + p.quantity, 0);
        setCartCount(total);
      })
      .catch(() => setCartCount(0));
  }, []);

  useEffect(() => {
    refreshCartCount();
  }, [refreshCartCount]);

  return (
    <CartContext.Provider value={{ cartCount, refreshCartCount }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return ctx;
}
