# ShopEase — Full-Stack E-Commerce Application

ShopEase is a full-stack e-commerce web application with product browsing, cart management, secure checkout with real payment gateway integration, order history, and a complete admin dashboard for managing products, categories, and orders.

## Live Demo

- **Frontend:** [shopease-frontend-rho.vercel.app](https://shopease-frontend-rho.vercel.app)
- **Backend API:** [shopease-backend-production-c526.up.railway.app/api](https://shopease-backend-production-c526.up.railway.app/api)

Frontend is deployed on **Vercel**, backend + Postgres on **Railway**.

**Demo admin login** (to try the admin dashboard): `admin@shopease.com` / `AdminPass123!`

## Features

- **Product catalog** — browse, search, filter by category, sort, and paginate products
- **Cart & checkout** — persistent cart, quantity management, and checkout with [Razorpay](https://razorpay.com/) payment integration (signature-verified server-side)
- **Authentication & authorization** — JWT-based login/registration with access + refresh tokens, role-based access control (user vs. admin)
- **Order history** — users can view their past orders and payment status
- **Admin dashboard** — sidebar-based admin panel to manage products, categories, and view/manage all orders (admin-only, enforced on the backend)
- **Responsive, modern UI** — dark, Linear-inspired theme built with Material UI
- **Automated tests** — JUnit/Mockito unit tests on the backend, React Testing Library tests on the frontend

## Tech Stack

### Backend (`ecommerce-backend/`)
- **Java 17**, **Spring Boot 3.5.4**
- **Spring Data JPA** + **H2** (file-based) database
- **Spring Security** with **JWT** (access & refresh tokens) for stateless authentication
- **Razorpay Java SDK** for payment processing and signature verification
- **Lombok** for boilerplate reduction
- **JUnit 5 + Mockito** for unit testing
- Build tool: **Maven** (via the included `mvnw` wrapper)

### Frontend (`ecommerce-frontend/`)
- **React 19** (Create React App / `react-scripts`)
- **React Router v7** for client-side routing
- **Material UI (MUI) v9** for components and theming, with a custom dark theme
- **Axios** for API communication
- React Context API for cross-cutting state (cart count, toast notifications)
- **React Testing Library + Jest** for component/unit testing

### Deployment
- Frontend hosted on **Vercel**
- Backend + **Postgres** hosted on **Railway**

## Project Structure

```
Ecommerce project/
├── ecommerce-backend/    # Spring Boot REST API
└── ecommerce-frontend/   # React single-page application
```

## Getting Started

### Prerequisites
- Java 17+
- Node.js (18+) and npm
- A Razorpay test account (optional — default test-mode keys are provided for local/demo use)

### Backend
```bash
cd ecommerce-backend
./mvnw spring-boot:run
```
The API starts on `http://localhost:8080`. Secrets (`JWT_SECRET`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`) can be overridden via environment variables; see `src/main/resources/application.properties` for defaults.

### Frontend
```bash
cd ecommerce-frontend
npm install
npm start
```
The app starts on `http://localhost:3000` and expects the backend API to be running on `http://localhost:8080`.

### Running Tests
```bash
# Backend
cd ecommerce-backend
./mvnw test

# Frontend
cd ecommerce-frontend
npm test
```
