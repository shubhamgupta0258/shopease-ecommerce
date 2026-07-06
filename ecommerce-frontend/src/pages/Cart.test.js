import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Cart from "./Cart";
import api from "../utils/api";

jest.mock("../utils/api");

const mockShowToast = jest.fn();
const mockRefreshCartCount = jest.fn();

jest.mock("../context/ToastContext", () => ({
  useToast: () => ({ showToast: mockShowToast }),
}));

jest.mock("../context/CartContext", () => ({
  useCart: () => ({ refreshCartCount: mockRefreshCartCount }),
}));

const sampleCart = {
  products: [
    { id: 1, name: "Laptop Pro", description: "Upgraded gaming laptop", price: 1499.99, quantity: 2 },
  ],
  totalPrice: 2999.98,
};

function renderCart() {
  return render(
    <MemoryRouter>
      <Cart />
    </MemoryRouter>
  );
}

describe("Cart page", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders cart items fetched from the API", async () => {
    api.get.mockResolvedValueOnce({ data: sampleCart });

    renderCart();

    expect(await screen.findByText("Laptop Pro")).toBeInTheDocument();
    expect(screen.getByText(/Total Price: ₹2999.98/)).toBeInTheDocument();
    expect(api.get).toHaveBeenCalledWith("/cart");
  });

  test("shows empty-cart message when there are no products", async () => {
    api.get.mockResolvedValueOnce({ data: { products: [], totalPrice: 0 } });

    renderCart();

    expect(await screen.findByText(/Your cart is empty/i)).toBeInTheDocument();
  });

  test("clicking + calls the add-to-cart endpoint and refreshes the cart", async () => {
    api.get.mockResolvedValue({ data: sampleCart });
    api.post.mockResolvedValueOnce({ data: {} });
    renderCart();
    await screen.findByText("Laptop Pro");

    await userEvent.click(screen.getByLabelText("increase quantity"));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/cart/add", [1]);
    });
    expect(mockRefreshCartCount).toHaveBeenCalled();
  });

  test("clicking - calls the remove endpoint and refreshes the cart", async () => {
    api.get.mockResolvedValue({ data: sampleCart });
    api.delete.mockResolvedValueOnce({ data: {} });
    renderCart();
    await screen.findByText("Laptop Pro");

    await userEvent.click(screen.getByLabelText("decrease quantity"));

    await waitFor(() => {
      expect(api.delete).toHaveBeenCalledWith("/cart/remove/1");
    });
    expect(mockRefreshCartCount).toHaveBeenCalled();
  });

  test("shows an error toast if decreasing quantity fails", async () => {
    api.get.mockResolvedValue({ data: sampleCart });
    api.delete.mockRejectedValueOnce(new Error("network error"));
    renderCart();
    await screen.findByText("Laptop Pro");

    await userEvent.click(screen.getByLabelText("decrease quantity"));

    await waitFor(() => {
      expect(mockShowToast).toHaveBeenCalledWith("Failed to decrease quantity", "error");
    });
  });
});
