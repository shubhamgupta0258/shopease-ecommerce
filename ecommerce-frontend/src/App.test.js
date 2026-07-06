import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { ThemeProvider } from "@mui/material/styles";
import theme from "./theme";
import App from "./App";
import { ToastProvider } from "./context/ToastContext";
import { CartProvider } from "./context/CartContext";
import api from "./utils/api";

jest.mock("./utils/api");

test("renders the Home page with the product catalog heading", async () => {
  api.get.mockImplementation((url) => {
    if (url === "/categories") return Promise.resolve({ data: [] });
    if (url === "/products") return Promise.resolve({ data: { content: [], totalPages: 0 } });
    return Promise.resolve({ data: {} });
  });

  render(
    <ThemeProvider theme={theme}>
      <ToastProvider>
        <CartProvider>
          <App />
        </CartProvider>
      </ToastProvider>
    </ThemeProvider>
  );

  await waitFor(() => {
    expect(screen.getByRole("heading", { name: "Products" })).toBeInTheDocument();
  });
});
