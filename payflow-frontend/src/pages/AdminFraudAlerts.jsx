
import { useEffect, useMemo, useState } from "react";
import {
  getAllFraudAlerts,
  updateFraudAlertStatus,
} from "../services/adminFraudAlertService";

const statusStyles = {
  OPEN: "bg-red-100 text-red-700",
  REVIEWED: "bg-blue-100 text-blue-700",
  DISMISSED: "bg-slate-100 text-slate-600",
};

const formatAmount = (amount) =>
  new Intl.NumberFormat("fr-MA", {
    style: "currency",
    currency: "MAD",
  }).format(amount ?? 0);

const formatDate = (date) =>
  date ? new Date(date).toLocaleString("fr-MA") : "—";

export default function AdminFraudAlerts() {
  const [alerts, setAlerts] = useState([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  const loadAlerts = async () => {
    try {
      setError("");
      const data = await getAllFraudAlerts();
      setAlerts(data);
    } catch (err) {
      console.error(err);
      setError("Impossible de charger les alertes de fraude.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAlerts();
  }, []);

  const filteredAlerts = useMemo(() => {
    const query = search.trim().toLowerCase();

    return alerts.filter((alert) => {
      const matchesStatus = !status || alert.status === status;
      const matchesSearch =
        !query ||
        String(alert.id).includes(query) ||
        String(alert.transactionId).includes(query) ||
        (alert.reason ?? "").toLowerCase().includes(query);

      return matchesStatus && matchesSearch;
    });
  }, [alerts, search, status]);

  const handleStatusChange = async (id, newStatus) => {
    try {
      setUpdatingId(id);
      setError("");
      const updatedAlert = await updateFraudAlertStatus(id, newStatus);

      setAlerts((previous) =>
        previous.map((alert) =>
          alert.id === id ? updatedAlert : alert
        )
      );
    } catch (err) {
      console.error(err);
      setError("Impossible de modifier le statut de l'alerte.");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-8">
      <div className="mx-auto max-w-7xl">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-slate-900">
            Alertes de fraude
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Consulte et traite les alertes détectées par PayFlow.
          </p>
        </div>

        <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
          {[
            { label: "Total", value: alerts.length },
            {
              label: "Ouvertes",
              value: alerts.filter((a) => a.status === "OPEN").length,
            },
            {
              label: "Examinées",
              value: alerts.filter((a) => a.status === "REVIEWED").length,
            },
          ].map((item) => (
            <div
              key={item.label}
              className="rounded-xl border border-slate-200 bg-white p-5"
            >
              <p className="text-sm text-slate-500">{item.label}</p>
              <p className="mt-2 text-2xl font-bold text-slate-900">
                {item.value}
              </p>
            </div>
          ))}
        </div>

        <section className="overflow-hidden rounded-xl border border-slate-200 bg-white">
          <div className="flex flex-col gap-3 border-b border-slate-200 p-4 sm:flex-row">
            <input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Rechercher par ID, transaction ou raison..."
              className="min-w-0 flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500"
            />

            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            >
              <option value="">Tous les statuts</option>
              <option value="OPEN">Ouvertes</option>
              <option value="REVIEWED">Examinées</option>
              <option value="DISMISSED">Ignorées</option>
            </select>

            <button
              onClick={loadAlerts}
              className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium hover:bg-slate-50"
            >
              Actualiser
            </button>
          </div>

          {error && (
            <p className="m-4 rounded-lg bg-red-50 p-3 text-sm text-red-700">
              {error}
            </p>
          )}

          {loading ? (
            <p className="p-8 text-center text-slate-500">
              Chargement des alertes...
            </p>
          ) : filteredAlerts.length === 0 ? (
            <p className="p-8 text-center text-slate-500">
              Aucune alerte trouvée.
            </p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-slate-50 text-xs uppercase text-slate-500">
                  <tr>
                    <th className="px-5 py-4">ID</th>
                    <th className="px-5 py-4">Transaction</th>
                    <th className="px-5 py-4">Montant</th>
                    <th className="px-5 py-4">Raison</th>
                    <th className="px-5 py-4">Date</th>
                    <th className="px-5 py-4">Statut</th>
                    <th className="px-5 py-4">Actions</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-slate-100">
                  {filteredAlerts.map((alert) => (
                    <tr key={alert.id} className="hover:bg-slate-50">
                      <td className="px-5 py-4 font-medium">#{alert.id}</td>
                      <td className="px-5 py-4">
                        #{alert.transactionId}
                      </td>
                      <td className="whitespace-nowrap px-5 py-4">
                        {formatAmount(alert.amount)}
                      </td>
                      <td className="max-w-xs px-5 py-4">
                        {alert.reason || "—"}
                      </td>
                      <td className="whitespace-nowrap px-5 py-4 text-slate-500">
                        {formatDate(alert.createdAt)}
                      </td>
                      <td className="px-5 py-4">
                        <span
                          className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                            statusStyles[alert.status] ??
                            "bg-slate-100 text-slate-600"
                          }`}
                        >
                          {alert.status}
                        </span>
                      </td>
                      <td className="px-5 py-4">
                        {alert.status === "OPEN" ? (
                          <div className="flex flex-wrap gap-2">
                            <button
                              disabled={updatingId === alert.id}
                              onClick={() =>
                                handleStatusChange(alert.id, "REVIEWED")
                              }
                              className="rounded-md bg-blue-50 px-3 py-1.5 text-xs font-semibold text-blue-700 hover:bg-blue-100 disabled:opacity-50"
                            >
                              Examiner
                            </button>
                            <button
                              disabled={updatingId === alert.id}
                              onClick={() =>
                                handleStatusChange(alert.id, "DISMISSED")
                              }
                              className="rounded-md bg-slate-100 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-200 disabled:opacity-50"
                            >
                              Ignorer
                            </button>
                          </div>
                        ) : (
                          <span className="text-xs text-slate-400">
                            Traitée
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
