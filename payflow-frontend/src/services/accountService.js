import api from "./api";

export const getMyAccount = async () => {
    const response = await api.get("/accounts/me");
    return response.data;
};