
import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8081/api",
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use(
    (config) => {
        // Pas de token pour l'inscription et la connexion
        const isPublicRoute =
            config.url?.includes("/auth/register") ||
            config.url?.includes("/auth/login");

        if (!isPublicRoute) {
            const token = localStorage.getItem("token");

            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);

export default api;
