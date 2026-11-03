
import { useEffect, useState } from "react";
import api from "../services/api";
import AdminSidebar from "../components/AdminSidebar";

function AdminDeposit() {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState("");
  const [account, setAccount] = useState(null);
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const response = await api.get("/admin/users");
        setUsers(response.data);
      } catch (err) {
        console.error(err);
        setError("Impossible de charger les utilisateurs.");
      }
    };

    loadUsers();
  }, []);

  const handleUserChange = async (userId) => {
    setSelectedUser(userId);
    setAccount(null);
    setSuccess("");
    setError("");

    if (!userId) return;

    try {
      const response = await api.get(`/accounts/admin/user/${userId}`);
      setAccount(response.data);
    } catch (err) {
      console.error(err);
      setError("Impossible de récupérer le compte de cet utilisateur.");
    }
  };

  const handleDeposit = async (e) => {
    e.preventDefault();
    setSuccess("");
    setError("");

    if (!account) {
      setError("Sélectionnez un compte.");
      return;
    }

    if (!amount || Number(amount) <= 0) {
      setError("Saisissez un montant positif.");
      return;
    }

    if (!window.confirm(`Confirmer le dépôt de ${amount} MAD ?`)) {
      return;
    }

    try {
      setLoading(true);

      await api.post(
        `/transactions/admin/deposit/${account.accountNumber}`,
        { amount: Number(amount) }
      );

      // Actualiser le solde
      const response = await api.get(
        `/accounts/admin/user/${selectedUser}`
      );

      setAccount(response.data);
      setSuccess("Dépôt effectué avec succès !");
      setAmount("");
    }catch (err) {
    console.error("URL :", err.config?.url);
    console.error("STATUS :", err.response?.status);
    console.error("RESPONSE :", err.response?.data);
    console.error("TOKEN :", localStorage.getItem("token"));

    setError(`Erreur HTTP ${err.response?.status}`);
}
  };

  return (
    <div className="flex min-h-screen bg-slate-50">
      <AdminSidebar />

      <div className="min-w-0 flex-1">
        <main className="min-h-screen bg-slate-50 p-4 sm:p-8">
          <div className="mx-auto max-w-4xl">

            <div className="mb-8">
              <h1 className="text-2xl font-bold text-slate-900">
                Dépôt d'argent
              </h1>
              <p className="mt-1 text-slate-500">
                Créditer le compte d'un utilisateur.
              </p>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <form onSubmit={handleDeposit} className="space-y-5">

                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-700">
                    Sélectionner un utilisateur
                  </label>

                  <select
                    value={selectedUser}
                    onChange={(e) => handleUserChange(e.target.value)}
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3"
                    required
                  >
                    <option value="">Choisir un utilisateur</option>
                    {users.map((user) => (
                      <option key={user.id} value={user.id}>
                        {user.firstName} {user.lastName} — {user.email}
                      </option>
                    ))}
                  </select>
                </div>

                {account && (
                  <div className="rounded-xl bg-slate-50 p-4">
                    <p className="text-sm text-slate-500">
                      Numéro de compte
                    </p>
                    <p className="font-semibold text-slate-900">
                      {account.accountNumber}
                    </p>

                    <p className="mt-3 text-sm text-slate-500">
                      Solde actuel
                    </p>
                    <p className="text-xl font-bold text-blue-700">
                      {account.balance} {account.currency}
                    </p>
                  </div>
                )}

                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-700">
                    Montant du dépôt
                  </label>

                  <input
                    type="number"
                    min="0.01"
                    step="0.01"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    placeholder="Exemple : 1000"
                    className="w-full rounded-xl border border-slate-300 px-4 py-3"
                    required
                  />
                </div>

                {error && (
                  <p className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
                    {error}
                  </p>
                )}

                {success && (
                  <p className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-700">
                    {success}
                  </p>
                )}

                <button
                  type="submit"
                  disabled={loading || !account}
                  className="w-full rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-50"
                >
                  {loading ? "Traitement..." : "Effectuer le dépôt"}
                </button>

              </form>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}

export default AdminDeposit;
