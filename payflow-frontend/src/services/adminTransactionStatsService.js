
import api from "./api";

export const getTransactionStatsByType = async () => {
    const response = await api.get("/admin/stats/type");
    return response.data;
};

export const getTransactionStatsByStatus = async () => {
    const response = await api.get("/admin/stats/status");
    return response.data;
};
