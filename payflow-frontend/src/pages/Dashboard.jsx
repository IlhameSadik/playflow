import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {getCurrentUser, logout} from "../services/authService";
import {getMyAccount} from "../services/accountService";
import {getMyTransactions} from "../services/transactionService";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import {
    getMyNotifications,
    markNotificationAsRead
} from "../services/notificationService";
/* ---------- Helpers ---------- */

const formatAmount = (value, currency = "MAD") => new Intl
    .NumberFormat(
        "fr-FR",
        {
            style: "currency",
            currency,
            minimumFractionDigits: 2
        }
    )
    .format(Number(value ?? 0));

const formatDate = (value) => value
    ? new Date(value).toLocaleDateString("fr-FR", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    })
    : null;

const CREDIT_TYPES = ["DEPOSIT", "CREDIT", "RECEIVED", "INCOMING", "DEPOT"];

const isCredit = (type = "") => CREDIT_TYPES.some(
    (t) => type.toUpperCase().includes(t)
);

const statusStyles = (status = "") => {
    const s = status.toUpperCase();

    if (["ACTIVE", "COMPLETED", "SUCCESS", "SUCCESSFUL"].includes(s)) 
        return "bg-green-50 text-green-700 ring-green-600/20";
    
    if (["PENDING", "IN_PROGRESS"].includes(s)) 
        return "bg-amber-50 text-amber-700 ring-amber-600/20";
    
    if (["FAILED", "REJECTED", "BLOCKED", "CANCELLED", "SUSPENDED"].includes(s)) 
        return "bg-red-50 text-red-700 ring-red-600/20";
    
    return "bg-slate-100 text-slate-600 ring-slate-500/20";
};

const getInitials = (user) => `${user
    ?.firstName
        ?.[0] ?? ""}${user
            ?.lastName
                ?.[0] ?? ""}`.toUpperCase() || "U";

/* ---------- Composants UI ---------- */

const Skeleton = ({
    className = ""
}) => (
    <div className={`animate-pulse rounded-md bg-slate-200/70 ${className}`}/>
);

const Badge = ({children}) => (
    <span
        className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${statusStyles(children)}`}>
        {children}
    </span>
);

const ArrowIcon = ({credit}) => (
    <svg
        className="h-5 w-5"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        strokeWidth="2"
        aria-hidden="true">
        <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d={credit
                ? "M7 7l10 10M17 7v10H7"
                : "M17 7L7 17M7 7h10v10"
}/>
    </svg>
);

function Dashboard() {
    const [notifications, setNotifications] = useState([]);
const [notificationsLoading, setNotificationsLoading] = useState(true);
const [notificationsError, setNotificationsError] = useState("");

const notificationsCount = notifications.filter(
    (n) => !n.isRead
).length;
    const navigate = useNavigate();
    const user = getCurrentUser();

    const [account, setAccount] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const [copied, setCopied] = useState(false);

  useEffect(() => {
    const loadData = async () => {
        try {
            const accountData = await getMyAccount();
            setAccount(accountData);
        } catch (err) {
            console.error("Erreur récupération compte :", err);
            setError("Impossible de récupérer votre compte.");
        }

        try {
            const transactionData = await getMyTransactions();
            setTransactions(
                Array.isArray(transactionData) ? transactionData : []
            );
        } catch (err) {
            console.error("Erreur récupération transactions :", err);
            setTransactions([]);
        } finally {
            setLoading(false);
        }
    };

    const loadNotifications = async () => {
        try {
            const data = await getMyNotifications();
            setNotifications(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Erreur notifications :", err);
            setNotificationsError(
                "Impossible de charger les notifications."
            );
        } finally {
            setNotificationsLoading(false);
        }
    };

    loadData();
    loadNotifications();
}, []);
    const handleLogout = () => {
        logout();
        navigate("/login");
    };
    const loadNotifications = async () => {
    try {
        const data = await getMyNotifications();
        setNotifications(Array.isArray(data) ? data : []);
    } catch (err) {
        setNotificationsError("Impossible de charger les notifications.");
    } finally {
        setNotificationsLoading(false);
    }
};

loadNotifications();
    const handleCopy = async () => {
        if (
            !account
                ?.accountNumber
        ) 
            return;
        
        try {
            await navigator
                .clipboard
                .writeText(account.accountNumber);
            setCopied(true);
            setTimeout(() => setCopied(false), 1800);
        } catch  {
            /* clipboard indisponible */
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 flex">

            {/* ---------- Sidebar ---------- */}

            <Sidebar onLogout={handleLogout} notificationsCount={notificationsCount}/> {/* ---------- Contenu principal ---------- */}

            <div className="flex-1 min-w-0">

                {/* Header */}
                <Header
    title="Transactions"
    subtitle="Consultez l'historique de vos opérations"
    notificationsCount={notificationsCount}
/>

                {/* Main */}
                <main className="mx-auto max-w-7xl px-4 py-8 sm:px-8">

                    {/* Welcome */}
                    <div className="mb-8">
                        <h2 className="text-2xl font-semibold text-slate-900">
                            Bonjour {
                                user
                                    ?.firstName
                            }
                            👋
                        </h2>

                        <p className="mt-1 text-slate-500">
                            Voici un aperçu de votre compte.
                        </p>
                    </div>

                    {/* Error */}
                    {
                        error && (
                            <div
                                role="alert"
                                className="mb-6 flex items-center justify-between gap-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                                <span>{error}</span>

                                <button
                                    onClick={() => window.location.reload()}
                                    className="font-semibold underline underline-offset-2 hover:text-red-800">
                                    Réessayer
                                </button>
                            </div>
                        )
                    }

                    {/* ---------- Account Cards ---------- */}

<div className="grid grid-cols-1 gap-5 md:grid-cols-3">

    {/* Solde — carte principale */}
    <div className="rounded-2xl border border-slate-200 bg-white p-7 md:col-span-2">

        <div className="flex items-start justify-between">

            <div>
                <p className="text-sm font-medium text-slate-500">
                    Solde disponible
                </p>

                {loading ? (
                    <Skeleton className="mt-4 h-11 w-56" />
                ) : (
                    <p className="mt-4 text-4xl font-bold tracking-tight text-slate-900">
                        {formatAmount(
                            account?.balance,
                            account?.currency
                        )}
                    </p>
                )}
            </div>

            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-50 text-blue-600">
                $
            </div>

        </div>

        <div className="mt-8 flex items-center justify-between border-t border-slate-100 pt-5">

            <div>
                <p className="text-xs text-slate-400">
                    Numéro de compte
                </p>

                <p className="mt-1 font-mono text-sm font-medium text-slate-700">
                    {loading
                        ? "•••• •••• ••••"
                        : account?.accountNumber}
                </p>
            </div>

            {!loading && (
                <button
                    type="button"
                    onClick={handleCopy}
                    className="text-xs font-medium text-blue-600 transition hover:text-blue-700"
                >
                    {copied ? "Copié ✓" : "Copier"}
                </button>
            )}

        </div>

    </div>


    {/* Compte */}
    <div className="rounded-2xl border border-slate-200 bg-white p-6">

        <p className="text-sm font-medium text-slate-500">
            Compte
        </p>

        {loading ? (
            <Skeleton className="mt-5 h-7 w-40" />
        ) : (
            <p className="mt-5 truncate font-mono text-lg font-semibold text-slate-900">
                {account?.accountNumber}
            </p>
        )}

        <div className="mt-5">

            <p className="text-xs text-slate-400">
                Statut
            </p>

            <div className="mt-2">
                {loading ? (
                    <Skeleton className="h-6 w-20 rounded-full" />
                ) : (
                    <Badge>
                        {account?.status}
                    </Badge>
                )}
            </div>

        </div>

    </div>


    {/* Transactions */}
    <div className="rounded-2xl border border-slate-200 bg-white p-6">

        <p className="text-sm font-medium text-slate-500">
            Transactions
        </p>

        {loading ? (
            <Skeleton className="mt-5 h-10 w-16" />
        ) : (
            <p className="mt-5 text-4xl font-bold tracking-tight text-slate-900">
                {transactions.length}
            </p>
        )}

        <p className="mt-2 text-sm text-slate-400">
            {transactions.length === 1
                ? "transaction enregistrée"
                : "transactions enregistrées"}
        </p>

    </div>

</div>

                    {/* ---------- Transactions récentes ---------- */}

                    <section className="mt-8 rounded-2xl border border-slate-200 bg-white">

                        <div
                            className="flex items-center justify-between border-b border-slate-200 p-6">

                            <h3 className="text-lg font-semibold text-slate-900">
                                Transactions récentes
                            </h3>

                            {
                                transactions.length > 5 && (
                                    <span className="text-sm text-slate-500">
                                        5 sur {transactions.length}
                                    </span>
                                )
                            }

                        </div>

                        {/* Loading */}
                        {
                            loading
                                ? (
                                    <div className="divide-y divide-slate-100">

                                        {
                                            [0, 1, 2].map((i) => (
                                                <div key={i} className="flex items-center gap-4 p-6">
                                                    <Skeleton className="h-10 w-10 rounded-full"/>

                                                    <div className="flex-1 space-y-2">
                                                        <Skeleton className="h-4 w-32"/>
                                                        <Skeleton className="h-3 w-48"/>
                                                    </div>

                                                    <Skeleton className="h-5 w-20"/>
                                                </div>
                                            ))
                                        }

                                    </div>

                                )
                                : transactions.length === 0
                                    ? (

                                        /* Empty state */
                                        <div className="flex flex-col items-center px-6 py-14 text-center">

                                            <div
                                                className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-100 text-slate-400">
                                                <svg
                                                    className="h-6 w-6"
                                                    fill="none"
                                                    viewBox="0 0 24 24"
                                                    stroke="currentColor"
                                                    strokeWidth="1.8">
                                                    <path
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                                                </svg>
                                            </div>

                                            <p className="mt-4 font-medium text-slate-900">
                                                Aucune transaction pour le moment
                                            </p>

                                            <p className="mt-1 text-sm text-slate-500">
                                                Vos opérations apparaîtront ici.
                                            </p>

                                        </div>

                                    )
                                    : (

                                        /* Transactions */
                                        <ul className="divide-y divide-slate-100">

                                            {
                                                transactions
                                                    .slice(0, 5)
                                                    .map((transaction) => {

                                                        const credit = isCredit(transaction.type);

                                                        const date = formatDate(transaction.createdAt || transaction.date);

                                                        return (
                                                            <li
                                                                key={transaction.id}
                                                                className="flex items-center gap-4 p-4 transition hover:bg-slate-50 sm:p-6">

                                                                <div
                                                                    className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full ${
                                                                    credit
                                                                        ? "bg-green-50 text-green-600"
                                                                        : "bg-slate-100 text-slate-600"}`}>
                                                                    <ArrowIcon credit={credit}/>
                                                                </div>

                                                                <div className="min-w-0 flex-1">

                                                                    <p className="truncate font-medium text-slate-900">
                                                                        {transaction.type}
                                                                    </p>

                                                                    <p className="mt-0.5 truncate text-sm text-slate-500">
                                                                        {transaction.description}

                                                                        {date && ` · ${date}`}
                                                                    </p>

                                                                </div>

                                                                <div className="shrink-0 text-right">

                                                                    <p
                                                                        className={`font-semibold ${
                                                                        credit
                                                                            ? "text-green-600"
                                                                            : "text-slate-900"}`}>
                                                                        {
                                                                            credit
                                                                                ? "+"
                                                                                : "−"
                                                                        }

                                                                        {formatAmount(transaction.amount, "MAD")}
                                                                    </p>

                                                                    <div className="mt-1">
                                                                        <Badge>
                                                                            {transaction.status}
                                                                        </Badge>
                                                                    </div>

                                                                </div>

                                                            </li>
                                                        );
                                                    })
                                            }

                                        </ul>
                                    )
                        }

                    </section>

                </main>
            </div>
        </div>
    );
}

export default Dashboard;