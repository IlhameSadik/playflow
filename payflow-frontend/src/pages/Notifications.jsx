
import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import { logout } from "../services/authService";
import {
    getMyNotifications,
    markNotificationAsRead
} from "../services/notificationService";
import { useNavigate } from "react-router-dom";

function Notifications() {
    const navigate = useNavigate();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const notificationsCount = notifications.filter(
        (n) => !n.isRead
    ).length;

    const loadNotifications = async () => {
        try {
            setError("");
            const data = await getMyNotifications();
            setNotifications(Array.isArray(data) ? data : []);
        } catch (err) {
            setError("Impossible de charger les notifications.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadNotifications();
    }, []);

    const handleMarkAsRead = async (id) => {
        try {
            const updated = await markNotificationAsRead(id);

            setNotifications((previous) =>
                previous.map((n) =>
                    n.id === id
                        ? { ...n, ...updated, isRead: true }
                        : n
                )
            );
        } catch (err) {
            setError("Impossible de modifier cette notification.");
        }
    };

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="flex min-h-screen bg-slate-50">
            <Sidebar
                onLogout={handleLogout}
                notificationsCount={notificationsCount}
            />

            <div className="min-w-0 flex-1">
                <Header
                    title="Notifications"
                    subtitle="Suivez les informations concernant votre compte"
                    notificationsCount={notificationsCount}
                />

                <main className="mx-auto max-w-4xl px-4 py-8 sm:px-8">
                    <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white">
                        <div className="border-b border-slate-200 p-6">
                            <h2 className="text-lg font-semibold text-slate-900">
                                Mes notifications
                            </h2>
                            <p className="mt-1 text-sm text-slate-500">
                                {notificationsCount} non lue(s)
                            </p>
                        </div>

                        {error && (
                            <p role="alert" className="p-5 text-sm text-red-600">
                                {error}
                            </p>
                        )}

                        {loading ? (
                            <p className="p-6 text-sm text-slate-500">
                                Chargement...
                            </p>
                        ) : notifications.length === 0 ? (
                            <div className="p-10 text-center text-slate-500">
                                Aucune notification pour le moment.
                            </div>
                        ) : (
                            <ul className="divide-y divide-slate-100">
                                {notifications.map((n) => (
                                    <li
                                        key={n.id}
                                        className={`p-6 ${
                                            n.isRead ? "bg-white" : "bg-blue-50/60"
                                        }`}
                                    >
                                        <div className="flex items-start gap-3">
                                            <span
                                                className={`mt-2 h-2.5 w-2.5 shrink-0 rounded-full ${
                                                    n.isRead
                                                        ? "bg-slate-300"
                                                        : "bg-blue-600"
                                                }`}
                                            />

                                            <div className="min-w-0 flex-1">
                                                <h3 className="font-semibold text-slate-900">
                                                    {n.title}
                                                </h3>

                                                <p className="mt-1 text-sm text-slate-600">
                                                    {n.message}
                                                </p>

                                                {n.createdAt && (
                                                    <p className="mt-2 text-xs text-slate-400">
                                                        {new Date(n.createdAt).toLocaleString("fr-FR")}
                                                    </p>
                                                )}

                                                {!n.isRead && (
                                                    <button
                                                        type="button"
                                                        onClick={() => handleMarkAsRead(n.id)}
                                                        className="mt-3 text-sm font-medium text-blue-600 hover:text-blue-800"
                                                    >
                                                        Marquer comme lue
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>
                </main>
            </div>
        </div>
    );
}

export default Notifications;
