import { Navigate } from "react-router-dom";
import { getCurrentUser } from "../services/authService";

function AdminRoute({ children }) {
    const user = getCurrentUser();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

   if (user.role !== "ADMIN") {
    return <Navigate to="/dashboard" replace />;
}

    return children;
}

export default AdminRoute;