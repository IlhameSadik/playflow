
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function AdminUsers() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [status, setStatus] = useState("ALL");
  const [search, setSearch] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const params = status === "ALL" ? {} : { status };
      const response = await api.get("/admin/users", { params });

      setUsers(response.data);
    } catch (err) {
      console.error(err);
      setError("Impossible de charger les utilisateurs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, [status]);

  const changeStatus = async (user) => {
    const newStatus = user.status === "ACTIVE" ? "BLOCKED" : "ACTIVE";

    if (!window.confirm(`Voulez-vous vraiment ${newStatus === "BLOCKED" ? "bloquer" : "activer"} cet utilisateur ?`)) {
      return;
    }

    try {
      setUpdatingId(user.id);

      const response = await api.put(
        `/admin/users/${user.id}/status`,
        null,
        { params: { status: newStatus } }
      );

      setUsers((currentUsers) =>
        currentUsers.map((item) =>
          item.id === user.id ? response.data : item
        )
      );
    } catch (err) {
      console.error(err);
      alert("Impossible de modifier le statut de cet utilisateur.");
    } finally {
      setUpdatingId(null);
    }
  };

  const filteredUsers = users.filter((user) => {
    const text = `${user.firstName ?? ""} ${user.lastName ?? ""} ${user.email ?? ""}`;
    return text.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <main className="min-h-screen bg-slate-50 p-4 sm:p-8">
      <div className="mx-auto max-w-7xl">
        <button
          onClick={() => navigate("/admin/dashboard")}
          className="mb-6 text-sm font-medium text-blue-600 hover:underline"
        >
          ← Retour au dashboard
        </button>

        <div className="mb-6">
          <h1 className="text-2xl font-bold text-slate-900">
            Gestion des utilisateurs
          </h1>
          <p className="mt-1 text-slate-500">
            Consulter et gérer les comptes utilisateurs PayFlow.
          </p>
        </div>

        <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <input
            type="search"
            placeholder="Rechercher par nom ou email..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none focus:border-blue-500"
          />

          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="rounded-xl border border-slate-300 bg-white px-4 py-3"
          >
            <option value="ALL">Tous les utilisateurs (API)</option>
            <option value="ACTIVE">Actifs</option>
            <option value="BLOCKED">Bloqués</option>
          </select>
        </div>

        <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-sm">
          {loading ? (
            <p className="p-8 text-center text-slate-500">Chargement...</p>
          ) : error ? (
            <div className="p-8 text-center">
              <p className="text-red-600">{error}</p>
              <button
                onClick={loadUsers}
                className="mt-3 text-blue-600 hover:underline"
              >
                Réessayer
              </button>
            </div>
          ) : filteredUsers.length === 0 ? (
            <p className="p-8 text-center text-slate-500">
              Aucun utilisateur trouvé.
            </p>
          ) : (
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-100 text-slate-600">
                <tr>
                  <th className="px-5 py-4">ID</th>
                  <th className="px-5 py-4">Nom</th>
                  <th className="px-5 py-4">Email</th>
                  <th className="px-5 py-4">Statut</th>
                  <th className="px-5 py-4">Action</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100">
                {filteredUsers.map((user) => (
                  <tr key={user.id} className="hover:bg-slate-50">
                    <td className="px-5 py-4">{user.id}</td>
                    <td className="px-5 py-4 font-medium text-slate-900">
                      {user.firstName} {user.lastName}
                    </td>
                    <td className="px-5 py-4">{user.email}</td>
                    <td className="px-5 py-4">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-semibold ${
                          user.status === "ACTIVE"
                            ? "bg-emerald-100 text-emerald-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {user.status}
                      </span>
                    </td>
                    <td className="px-5 py-4">
                      <button
                        disabled={updatingId === user.id}
                        onClick={() => changeStatus(user)}
                        className={`rounded-lg px-3 py-2 text-xs font-semibold text-white disabled:opacity-50 ${
                          user.status === "ACTIVE"
                            ? "bg-red-600 hover:bg-red-700"
                            : "bg-emerald-600 hover:bg-emerald-700"
                        }`}
                      >
                        {updatingId === user.id
                          ? "Modification..."
                          : user.status === "ACTIVE"
                          ? "Bloquer"
                          : "Activer"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </main>
  );
}

export default AdminUsers;
