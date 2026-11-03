
import { useEffect, useState } from "react";
import { getCurrentUser, logout } from "../services/authService";
import { getAdminStats } from "../services/adminStatsService";
import { getAllFraudAlerts } from "../services/adminFraudAlertService";
import { getAllAdminTransactions } from "../services/adminTransactionService";
import { Link, useNavigate } from "react-router-dom";
import AdminSidebar from "../components/AdminSidebar";
const Icon = ({ d }) => (
  <svg
    className="h-6 w-6"
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="1.8"
    aria-hidden="true"
  >
    <path strokeLinecap="round" strokeLinejoin="round" d={d} />
  </svg>
);

const icons = {
  users:
    "M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m6-4.13a4 4 0 11-8 0 4 4 0 018 0zm6 0a3 3 0 11-6 0 3 3 0 016 0z",
  accounts:
    "M3 10h18M7 15h2m4 0h4M5 5h14a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2z",
  transactions:
    "M7 7h10m0 0l-4-4m4 4l-4 4M17 17H7m0 0l4 4m-4-4l4-4",
  fraud:
    "M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z",
  logout:
    "M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1",
  inbox:
    "M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-4a2 2 0 00-2 2 2 2 0 01-2 2 2 2 0 01-2-2 2 2 0 00-2-2H4",
};

const statStyles = {
  Utilisateurs: { icon: icons.users, tone: "bg-blue-50 text-blue-600" },
  Comptes: { icon: icons.accounts, tone: "bg-indigo-50 text-indigo-600" },
  Transactions: {
    icon: icons.transactions,
    tone: "bg-emerald-50 text-emerald-600",
  },
  "Alertes de fraude": {
    icon: icons.fraud,
    tone: "bg-red-50 text-red-600",
  },
};

const getInitials = (user) =>
  `${user?.firstName?.[0] ?? ""}${user?.lastName?.[0] ?? ""}`
    .toUpperCase() || "A";

const EmptyState = ({ title, text }) => (
  <div className="flex flex-col items-center px-6 py-10 text-center">
    <div className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-100 text-slate-400">
      <Icon d={icons.inbox} />
    </div>
    <p className="mt-4 font-medium text-slate-900">{title}</p>
    <p className="mt-1 text-sm text-slate-500">{text}</p>
  </div>
);

const formatDate = (date) =>
  date ? new Date(date).toLocaleString("fr-MA") : "—";

const formatAmount = (amount) =>
  new Intl.NumberFormat("fr-MA", {
    style: "currency",
    currency: "MAD",
  }).format(amount ?? 0);

function AdminDashboard() {
  const navigate = useNavigate();
  const user = getCurrentUser();

  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [fraudAlerts, setFraudAlerts] = useState([]);
  const [fraudLoading, setFraudLoading] = useState(true);
  const [fraudError, setFraudError] = useState("");

  const [transactions, setTransactions] = useState([]);
  const [transactionsLoading, setTransactionsLoading] = useState(true);
  const [transactionsError, setTransactionsError] = useState("");

  useEffect(() => {
    let cancelled = false;

    const loadDashboard = async () => {
      const results = await Promise.allSettled([
        getAdminStats(),
        getAllFraudAlerts(),
        getAllAdminTransactions(),
      ]);

      if (cancelled) return;

      if (results[0].status === "fulfilled") {
        setStats(results[0].value);
      } else {
        setError(
          results[0].reason?.response?.status === 403
            ? "Accès refusé : vérifie les autorisations administrateur."
            : "Impossible de charger les statistiques."
        );
      }

      if (results[1].status === "fulfilled") {
        setFraudAlerts(results[1].value);
      } else {
        console.error("Erreur alertes de fraude :", results[1].reason);
        setFraudError("Impossible de charger les alertes.");
      }

      if (results[2].status === "fulfilled") {
        setTransactions(results[2].value);
      } else {
        console.error("Erreur transactions :", results[2].reason);
        setTransactionsError("Impossible de charger les transactions.");
      }

      setLoading(false);
      setFraudLoading(false);
      setTransactionsLoading(false);
    };

    loadDashboard();

    return () => {
      cancelled = true;
    };
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const cards = [
    { title: "Utilisateurs", key: "totalUsers" },
    { title: "Comptes", key: "totalAccounts" },
    { title: "Transactions", key: "totalTransactions" },
    { title: "Alertes de fraude", key: "openFraudAlerts" },
  ];

  return (
  <div className="flex min-h-screen bg-slate-50">
    <AdminSidebar />

    <div className="min-w-0 flex-1">
      <header className="sticky top-0 z-10 border-b justify-end border-slate-200 bg-white/80 backdrop-blur">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-end px-4 sm:px-8">
          

          <div className="flex items-center gap-3 sm:gap-4">
            <div className="hidden text-right sm:block">
              <p className="text-sm font-semibold text-slate-900">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-slate-500">{user?.email}</p>
            </div>

            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-sm font-semibold text-blue-700">
              {getInitials(user)}
            </div>

           
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-8">
        <section className="relative mb-8 overflow-hidden rounded-2xl bg-gradient-to-br from-blue-600 to-indigo-700 p-6 text-white shadow-lg shadow-blue-600/20 sm:p-8">
          <div className="absolute -right-10 -top-10 h-44 w-44 rounded-full bg-white/10" />
          <div className="absolute -bottom-14 right-24 h-36 w-36 rounded-full bg-white/5" />

          <div className="relative">
            <h2 className="text-2xl font-semibold">
              Bonjour {user?.firstName} 👋
            </h2>
            <p className="mt-1 max-w-xl text-blue-100">
              Bienvenue dans l'espace d'administration PayFlow. Voici une vue
              d'ensemble de la plateforme.
            </p>
          </div>
        </section>

        <section aria-label="Statistiques">
          <h3 className="mb-4 text-lg font-semibold text-slate-900">
            Vue d'ensemble
          </h3>

          {loading && (
            <p className="mb-4 text-sm text-slate-500">
              Chargement des statistiques...
            </p>
          )}

          {error && (
            <div className="mb-4 rounded-lg bg-red-50 p-4 text-sm text-red-700">
              {error}
            </div>
          )}

          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-4">
            {cards.map((stat) => {
              const style = statStyles[stat.title];

              return (
                <div
                  key={stat.key}
                  className="group rounded-2xl border border-slate-200 bg-white p-6 transition hover:-translate-y-0.5 hover:shadow-md"
                >
                  <div className="flex items-start justify-between">
                    <p className="text-sm font-medium text-slate-500">
                      {stat.title}
                    </p>
                    <div
                      className={`flex h-11 w-11 items-center justify-center rounded-xl ${style.tone}`}
                    >
                      <Icon d={style.icon} />
                    </div>
                  </div>

                  <p className="mt-4 text-3xl font-bold tracking-tight text-slate-900">
                    {loading ? "…" : stats ? (stats[stat.key] ?? 0) : "—"}
                  </p>
                </div>
              );
            })}
          </div>
        </section>

        <section className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
          {/* Alertes de fraude récentes */}
          <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white">
            <div className="flex items-center justify-between border-b border-slate-200 p-6">
              <h3 className="text-lg font-semibold text-slate-900">
                Alertes de fraude récentes
              </h3>
              <Link
                to="/admin/fraud-alerts"
                className="text-sm font-medium text-blue-600 hover:text-blue-700"
              >
                Voir tout →
              </Link>
            </div>

            {fraudLoading ? (
              <p className="p-6 text-sm text-slate-500">
                Chargement des alertes...
              </p>
            ) : fraudError ? (
              <p className="p-6 text-sm text-red-600">{fraudError}</p>
            ) : fraudAlerts.length === 0 ? (
              <EmptyState
                title="Aucune alerte"
                text="Aucune alerte de fraude n'a été enregistrée."
              />
            ) : (
              <div className="divide-y divide-slate-100">
                {[...fraudAlerts]
                  .sort(
                    (a, b) =>
                      new Date(b.createdAt) - new Date(a.createdAt)
                  )
                  .slice(0, 5)
                  .map((alert) => (
                    <div key={alert.id} className="p-5">
                      <div className="flex items-center justify-between gap-3">
                        <p className="font-medium text-slate-900">
                          Transaction #{alert.transactionId}
                        </p>
                        <span
                          className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                            alert.status === "OPEN"
                              ? "bg-red-100 text-red-700"
                              : alert.status === "REVIEWED"
                              ? "bg-blue-100 text-blue-700"
                              : "bg-slate-100 text-slate-600"
                          }`}
                        >
                          {alert.status}
                        </span>
                      </div>
                      <p className="mt-1 text-sm text-slate-600">
                        {alert.reason || "Motif non précisé"}
                      </p>
                      <div className="mt-2 flex flex-wrap justify-between gap-2 text-xs text-slate-500">
                        <span>{formatAmount(alert.amount)}</span>
                        <span>{formatDate(alert.createdAt)}</span>
                      </div>
                    </div>
                  ))}
              </div>
            )}
          </div>

          {/* Activité récente */}
          <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white">
            <div className="flex items-center justify-between border-b border-slate-200 p-6">
              <h3 className="text-lg font-semibold text-slate-900">
                Activité récente
              </h3>
              <Link
                to="/admin/transactions"
                className="text-sm font-medium text-blue-600 hover:text-blue-700"
              >
                Voir tout →
              </Link>
            </div>

            {transactionsLoading ? (
              <p className="p-6 text-sm text-slate-500">
                Chargement des transactions...
              </p>
            ) : transactionsError ? (
              <p className="p-6 text-sm text-red-600">{transactionsError}</p>
            ) : transactions.length === 0 ? (
              <EmptyState
                title="Aucune activité"
                text="Aucune transaction n'a été enregistrée."
              />
            ) : (
              <div className="divide-y divide-slate-100">
                {[...transactions]
                  .sort(
                    (a, b) =>
                      new Date(b.createdAt) - new Date(a.createdAt)
                  )
                  .slice(0, 5)
                  .map((transaction) => (
                    <div key={transaction.id} className="p-5">
                      <div className="flex items-center justify-between gap-3">
                        <p className="font-medium text-slate-900">
                          Transaction #{transaction.id}
                        </p>
                        <span className="text-xs font-semibold text-emerald-700">
                          {transaction.status}
                        </span>
                      </div>
                      <p className="mt-1 text-sm text-slate-600">
                        {transaction.type} ·{" "}
                        {formatAmount(transaction.amount)}
                      </p>
                      <p className="mt-1 text-xs text-slate-500">
                        {formatDate(transaction.createdAt)}
                      </p>
                    </div>
                  ))}
              </div>
            )}
          </div>
        </section>
      </main>
    </div>
      </div>
  );
}

export default AdminDashboard;