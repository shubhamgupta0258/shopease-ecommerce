// import axios from 'axios';
// const api=axios.create({
//     baseURL:'http://localhost:8080/api',
//     timeout:10000,
// });
// let isRefreshing = false;
// let failedQueue = [];

// const processQueue = (error, token = null) => {
//   failedQueue.forEach(prom => {
//     if (error) {
//       prom.reject(error);
//     } else {
//       prom.resolve(token);
//     }
//   });

//   failedQueue = [];
// };

// //Attach access token automatically
// api.interceptors.request.use(config=> {
//     const token=localStorage.getItem("accessToken");
//     if(token){
//     config.headers.Authorization=`Bearer ${token}`;
// }
// return config;
// }, err=>Promise.reject(err));

// //Global 401 handler: clear token and force login
// // api.interceptors.response.use(
// //     res=>res,
// //     err=> {
// //         const status=err?.response?.status;
// //         if(status===401){
// //             localStorage.removeItem("accessToken");
// //             window.location.href="/login";
// //         }
// //         return Promise.reject(err);
// //     }
// // );
// export default api;
import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080/api",
  timeout: 10000,
});

// ---------------------
// TOKEN REFRESH HELPERS
// ---------------------
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error);
    else prom.resolve(token);
  });
  failedQueue = [];
};

// ---------------------
// REQUEST INTERCEPTOR
// ---------------------
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (err) => Promise.reject(err)
);

// ---------------------
// RESPONSE INTERCEPTOR
// ---------------------
api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    // If token expired (401) AND request not retried earlier
    if (status === 401 && !originalRequest._retry) {

      if (isRefreshing) {
        // queue request until refresh done
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = "Bearer " + token;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = localStorage.getItem("refreshToken");

      if (!refreshToken) {
        isRefreshing = false;
        return Promise.reject(error);
      }

      try {
        const res = await api.post("/auth/refresh", { refreshToken });

        const newAccessToken = res.data.accessToken;

        // Store new access token
        localStorage.setItem("accessToken", newAccessToken);

        // Process queued requests
        processQueue(null, newAccessToken);

        // Retry original request
        originalRequest.headers.Authorization = "Bearer " + newAccessToken;
        return api(originalRequest);

      } catch (err) {
        processQueue(err, null);
        return Promise.reject(err);

      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
