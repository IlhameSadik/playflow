
import api from "./api";

export const getAllFraudAlerts = async () => {
  const response = await api.get("/admin/fraud-alerts");
  return response.data;
};

export const updateFraudAlertStatus = async (id, status) => {
  const response = await api.put(
    `/admin/fraud-alerts/${id}/status`,
    null,
    { params: { status } }
  );
  return response.data;
};
