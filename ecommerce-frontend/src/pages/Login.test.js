import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Login from "./Login";
import api from "../utils/api";

jest.mock("../utils/api");

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

const mockRefreshCartCount = jest.fn();
jest.mock("../context/CartContext", () => ({
  useCart: () => ({ refreshCartCount: mockRefreshCartCount }),
}));

async function fillAndSubmit(email, password) {
  await userEvent.type(screen.getByLabelText(/Email/i), email);
  await userEvent.type(screen.getByLabelText(/Password/i), password);
  await userEvent.click(screen.getByRole("button", { name: /Login/i }));
}

describe("Login page", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test("shows an error message on invalid credentials", async () => {
    api.post.mockRejectedValueOnce(new Error("Unauthorized"));

    render(<Login />);
    await fillAndSubmit("wrong@example.com", "badpassword");

    expect(await screen.findByText(/Invalid email or password/i)).toBeInTheDocument();
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  test("stores tokens, fetches role, and navigates home on success", async () => {
    api.post.mockResolvedValueOnce({
      data: { accessToken: "access-123", refreshToken: "refresh-456" },
    });
    api.get.mockResolvedValueOnce({ data: { email: "user@example.com", role: "ROLE_USER" } });

    render(<Login />);
    await fillAndSubmit("user@example.com", "correctpassword");

    await waitFor(() => {
      expect(localStorage.getItem("accessToken")).toBe("access-123");
    });
    expect(localStorage.getItem("refreshToken")).toBe("refresh-456");
    expect(localStorage.getItem("userRole")).toBe("ROLE_USER");
    expect(mockRefreshCartCount).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith("/", { replace: true });
  });

  test("shows a generic error when the backend returns no access token", async () => {
    api.post.mockResolvedValueOnce({ data: {} });

    render(<Login />);
    await fillAndSubmit("user@example.com", "correctpassword");

    expect(await screen.findByText(/Login failed\./i)).toBeInTheDocument();
    expect(mockNavigate).not.toHaveBeenCalled();
  });
});
