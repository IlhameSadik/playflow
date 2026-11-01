
import api from "./api";

export const getMyTransactions = async () => {
    const response = await api.get("/transactions/my-transactions");
    return response.data;
};

export const transfer = async ({
    sourceAccountNumber,
    destinationAccountNumber,
    amount,
    description,
    idempotencyKey,
}) => {
    const response = await api.post("/transactions/transfer", {
        sourceAccountNumber,
        destinationAccountNumber,
        amount,
        description,
        idempotencyKey,
    });

    return response.data;
};


 
