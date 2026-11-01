
import { useEffect, useState } from "react";
import { getAllAdminTransactions } from "../services/adminTransactionService";

export default function AdminTransactions() {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [search, setSearch] = useState("");
    const [type, setType] = useState("");
    const [status, setStatus] = useState("");

    useEffect(() => {
        const loadTransactions = async () => {
            setLoading(true);
            setError("");

            try {
                const data = await getAllAdminTransactions({
                    search,
                    type,
                    status,
                });

                setTransactions(data);
            } catch (err) {
                console.error(err);
                setError("Impossible de charger les transactions.");
            } finally {
                setLoading(false);
            }
        };

        loadTransactions();
    }, [search, type, status]);

    const formatDate = (date) =>
        date ? new Date(date).toLocaleString("fr-FR") : "—";

    const formatAmount = (amount) =>
        `${Number(amount).toLocaleString("fr-FR", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        })} MAD`;

    const statusStyle = (value) => {
        switch (value) {
            case "COMPLETED":
                return "bg-green-100 text-green-700";
            case "PENDING":
                return "bg-yellow-100 text-yellow-700";
            case "FAILED":
            case "CANCELLED":
                return "bg-red-100 text-red-700";
            default:
                return "bg-gray-100 text-gray-700";
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6 md:p-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800">
                    Gestion des transactions
                </h1>
                <p className="mt-2 text-gray-500">
                    Consultez et recherchez les transactions PayFlow.
                </p>
            </div>

            <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-3">
                <input
                    type="text"
                    placeholder="Rechercher par ID ou compte..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="rounded-xl border border-gray-200 bg-white px-4 py-3 outline-none focus:border-indigo-500"
                />

                <select
                    value={type}
                    onChange={(e) => setType(e.target.value)}
                    className="rounded-xl border border-gray-200 bg-white px-4 py-3 outline-none focus:border-indigo-500"
                >
                    <option value="">Tous les types</option>
                    <option value="TRANSFER">Transfert</option>
                    <option value="DEPOSIT">Dépôt</option>
                    <option value="WITHDRAWAL">Retrait</option>
                </select>

                <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="rounded-xl border border-gray-200 bg-white px-4 py-3 outline-none focus:border-indigo-500"
                >
                    <option value="">Tous les statuts</option>
                    <option value="COMPLETED">Terminé</option>
                    <option value="PENDING">En attente</option>
                    <option value="FAILED">Échoué</option>
                    <option value="CANCELLED">Annulé</option>
                </select>
            </div>

            <div className="mb-4 flex items-center justify-between">
                <p className="text-sm text-gray-500">
                    {loading
                        ? "Chargement..."
                        : `${transactions.length} transaction(s)`}
                </p>

                <button
                    onClick={() => {
                        setSearch("");
                        setType("");
                        setStatus("");
                    }}
                    className="rounded-lg border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100"
                >
                    Réinitialiser les filtres
                </button>
            </div>

            <div className="overflow-x-auto rounded-2xl border border-gray-100 bg-white shadow-sm">
                {loading ? (
                    <p className="p-8 text-center text-gray-500">
                        Chargement des transactions...
                    </p>
                ) : error ? (
                    <p className="p-8 text-center text-red-600">
                        {error}
                    </p>
                ) : transactions.length === 0 ? (
                    <p className="p-8 text-center text-gray-500">
                        Aucune transaction trouvée.
                    </p>
                ) : (
                    <table className="w-full text-left text-sm">
                        <thead className="bg-gray-50 text-gray-500">
                            <tr>
                                <th className="px-5 py-4">ID</th>
                                <th className="px-5 py-4">Type</th>
                                <th className="px-5 py-4">Compte source</th>
                                <th className="px-5 py-4">Compte destination</th>
                                <th className="px-5 py-4">Montant</th>
                                <th className="px-5 py-4">Statut</th>
                                <th className="px-5 py-4">Date</th>
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-gray-100">
                            {transactions.map((transaction) => (
                                <tr
                                    key={transaction.id}
                                    className="hover:bg-gray-50"
                                >
                                    <td className="px-5 py-4 font-medium text-gray-800">
                                        #{transaction.id}
                                    </td>
                                    <td className="px-5 py-4 text-gray-600">
                                        {transaction.type}
                                    </td>
                                    <td className="px-5 py-4 text-gray-600">
                                        {transaction.sourceAccountNumber}
                                    </td>
                                    <td className="px-5 py-4 text-gray-600">
                                        {transaction.destinationAccountNumber}
                                    </td>
                                    <td className="whitespace-nowrap px-5 py-4 font-semibold text-gray-800">
                                        {formatAmount(transaction.amount)}
                                    </td>
                                    <td className="px-5 py-4">
                                        <span
                                            className={`rounded-full px-3 py-1 text-xs font-semibold ${statusStyle(transaction.status)}`}
                                        >
                                            {transaction.status}
                                        </span>
                                    </td>
                                    <td className="whitespace-nowrap px-5 py-4 text-gray-500">
                                        {formatDate(transaction.createdAt)}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
