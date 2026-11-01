
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import { getMyTransactions } from "../services/transactionService";
import { logout } from "../services/authService";

function formatAmount(amount, currency = "MAD") {
    return new Intl.NumberFormat("fr-MA", {
        style: "currency",
        currency,
    }).format(amount ?? 0);
}

function formatDate(date) {
    return new Intl.DateTimeFormat("fr-MA", {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(new Date(date));
}

function getStatusStyle(status) {
    switch (status) {
        case "COMPLETED":
            return "bg-green-50 text-green-700";

        case "PENDING":
            return "bg-yellow-50 text-yellow-700";

        case "FAILED":
        case "CANCELLED":
            return "bg-red-50 text-red-700";

        default:
            return "bg-slate-100 text-slate-600";
    }
}

function Transactions() {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        const loadTransactions = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await getMyTransactions();
                setTransactions(data);
            } catch (err) {
                setError("Impossible de charger les transactions.");
            } finally {
                setLoading(false);
            }
        };

        loadTransactions();
    }, []);

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="min-h-screen bg-slate-50 flex">

            <Sidebar onLogout={handleLogout} />

            <div className="flex-1 min-w-0">

                {/* Header */}
               <Header
    title="Transactions"
    subtitle="Consultez l'historique de vos opérations"
    notificationsCount={0}
/>

                {/* Content */}
                <main className="p-6 lg:p-8">

                    {/* Error */}
                    {error && (
                        <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
                            {error}
                        </div>
                    )}

                    {/* Transactions card */}
                    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white">

                        <div className="border-b border-slate-200 px-6 py-5">
                            <h2 className="text-base font-semibold text-slate-900">
                                Historique
                            </h2>

                            <p className="mt-1 text-sm text-slate-500">
                                {transactions.length} transaction
                                {transactions.length !== 1 ? "s" : ""}
                            </p>
                        </div>

                        {loading ? (
                            <div className="p-6 text-sm text-slate-500">
                                Chargement des transactions...
                            </div>
                        ) : transactions.length === 0 ? (
                            <div className="p-10 text-center">
                                <p className="text-sm font-medium text-slate-700">
                                    Aucune transaction
                                </p>

                                <p className="mt-1 text-sm text-slate-400">
                                    Vos opérations apparaîtront ici.
                                </p>
                            </div>
                        ) : (
                            <div className="overflow-x-auto">
                                <table className="w-full min-w-[750px]">

                                    <thead className="bg-slate-50">
                                        <tr>
                                            <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                                                Type
                                            </th>

                                            <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                                                Montant
                                            </th>

                                            <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                                                Description
                                            </th>

                                            <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                                                Statut
                                            </th>

                                            <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wide text-slate-400">
                                                Date
                                            </th>
                                        </tr>
                                    </thead>

                                    <tbody className="divide-y divide-slate-100">

                                        {transactions.map((transaction) => (
                                            <tr
                                                key={transaction.id}
                                                className="transition hover:bg-slate-50"
                                            >
                                                <td className="px-6 py-4">
                                                    <span className="text-sm font-medium text-slate-900">
                                                        {transaction.type}
                                                    </span>
                                                </td>

                                                <td className="px-6 py-4">
                                                    <span className="text-sm font-semibold text-slate-900">
                                                        {formatAmount(
                                                            transaction.amount
                                                        )}
                                                    </span>
                                                </td>

                                                <td className="px-6 py-4">
                                                    <span className="text-sm text-slate-500">
                                                        {transaction.description || "—"}
                                                    </span>
                                                </td>

                                                <td className="px-6 py-4">
                                                    <span
                                                        className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusStyle(
                                                            transaction.status
                                                        )}`}
                                                    >
                                                        {transaction.status}
                                                    </span>
                                                </td>

                                                <td className="px-6 py-4 text-sm text-slate-500">
                                                    {formatDate(
                                                        transaction.createdAt
                                                    )}
                                                </td>
                                            </tr>
                                        ))}

                                    </tbody>

                                </table>
                            </div>
                        )}

                    </div>

                </main>

            </div>
        </div>
    );
}

export default Transactions;
