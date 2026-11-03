import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";

import Transfer from "./pages/Transfer";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Transactions from "./pages/Transactions";
import AdminRoute from "./components/AdminRoute";
import ProtectedRoute from "./components/ProtectedRoute";
import AdminDeposit from "./pages/AdminDeposit";
import AdminDashboard from "./pages/AdminDashboard";
import AdminUsers from "./pages/AdminUsers";
import AdminTransactions from "./pages/AdminTransactions";
import AdminFraudAlerts from "./pages/AdminFraudAlerts";

import Notifications from "./pages/Notifications";
import Register from "./pages/Register";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Authentication */}
                <Route path="/login" element={<Login />}/>
                <Route path="/register" element={<Register />}/> {/* User */}
                <Route
                    path="/dashboard"
                    element={<ProtectedRoute > <Dashboard/>
                </ProtectedRoute>
                    }
                />

                <Route
                    path="/transactions"
                    element={<ProtectedRoute > <Transactions/>
                </ProtectedRoute>
                    }
                />

                <Route
                    path="/transfer"
                    element={<ProtectedRoute > <Transfer/>
                </ProtectedRoute>
                    }
                />

                <Route
                    path="/notifications"
                    element={<ProtectedRoute > <Notifications/>
                </ProtectedRoute>
                    }
                /> {/* Admin */}
                <Route
                    path="/admin/dashboard"
                    element={<AdminRoute > <AdminDashboard/>
                </AdminRoute>
                    }
                />
                <Route
                    path="/admin/deposit"
                    element={<AdminRoute > <AdminDeposit/>
                </AdminRoute>
                    }
                />

                <Route
                    path="/admin/users"
                    element={<AdminRoute > <AdminUsers/>
                </AdminRoute>
                    }
                />

                <Route
                    path="/admin/transactions"
                    element={<AdminRoute > <AdminTransactions/>
                </AdminRoute>
                    }
                />

                <Route
                    path="/admin/fraud-alerts"
                    element={<AdminRoute > <AdminFraudAlerts/>
                </AdminRoute>
                    }
                />

                <Route path="*" element={<Navigate to = "/login" replace />}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
