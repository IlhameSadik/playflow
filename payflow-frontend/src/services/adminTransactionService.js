
import api from "./api";

export const getAllAdminTransactions = async (filters = {}) => {
    const response = await api.get("/admin/transactions/filter", {
        params: {
            type: filters.type || undefined,
            status: filters.status || undefined,
            search: filters.search || undefined,
        },
    });

    return response.data;
};
