
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Header from "../components/Header";
import Sidebar from "../components/Sidebar";

import { logout } from "../services/authService";
import { transfer } from "../services/transactionService";
import { getMyAccount } from "../services/accountService";

function Transfer() {
    const navigate = useNavigate();

    const [account, setAccount] = useState(null);

    const [destinationAccount, setDestinationAccount] = useState("");
    const [amount, setAmount] = useState("");
    const [description, setDescription] = useState("");

    const [loading, setLoading] = useState(false);
    const [loadingAccount, setLoadingAccount] = useState(true);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {
        const loadAccount = async () => {
            try {
                const data = await getMyAccount();
                setAccount(data);
            } catch (error) {
                setError("Impossible de récupérer votre compte.");
            } finally {
                setLoadingAccount(false);
            }
        };

        loadAccount();
    }, []);

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        setError("");
        setSuccess("");

        if (!account?.accountNumber) {
            setError("Votre compte est introuvable.");
            return;
        }

        setLoading(true);

        try {
            const idempotencyKey = crypto.randomUUID();

            await transfer({
                sourceAccountNumber: account.accountNumber,
                destinationAccountNumber: destinationAccount,
                amount: Number(amount),
                description: description || null,
                idempotencyKey,
            });

            setSuccess("Transfert effectué avec succès.");

            setDestinationAccount("");
            setAmount("");
            setDescription("");

        } catch (error) {
    console.log("Transfer error:", error);
    console.log("Response:", error.response?.data);

    if (error.response?.data?.message) {
        setError(error.response.data.message);
    } else if (error.response?.data) {
        setError(JSON.stringify(error.response.data));
    } else {
        setError("Impossible d'effectuer le transfert.");
    }
} finally {
    setLoading(false);
}
    };

    return (
        <div className="min-h-screen bg-slate-50 flex">

            <Sidebar onLogout={handleLogout} />

            <div className="flex-1 min-w-0">

                <Header
                    title="Transfert"
                    subtitle="Envoyez de l'argent vers un autre compte PayFlow"
                    notificationsCount={0}
                />

                <main className="p-6 lg:p-8">

                    <div className="max-w-2xl">

                        <div className="rounded-2xl border border-slate-200 bg-white p-6 lg:p-8">

                            <div className="mb-7">
                                <h2 className="text-lg font-semibold text-slate-900">
                                    Nouveau transfert
                                </h2>

                                <p className="mt-1 text-sm text-slate-500">
                                    Saisissez les informations du transfert.
                                </p>
                            </div>

                            {error && (
                                <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
                                    {error}
                                </div>
                            )}

                            {success && (
                                <div className="mb-5 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-600">
                                    {success}
                                </div>
                            )}

                            {/* Source account */}
                            <div className="mb-5">
                                <label className="mb-2 block text-sm font-medium text-slate-700">
                                    Votre compte
                                </label>

                                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                                    {loadingAccount ? (
                                        <span className="text-sm text-slate-400">
                                            Chargement...
                                        </span>
                                    ) : (
                                        <span className="font-mono text-sm font-medium text-slate-700">
                                            {account?.accountNumber}
                                        </span>
                                    )}
                                </div>
                            </div>

                            <form
                                onSubmit={handleSubmit}
                                className="space-y-5"
                            >

                                {/* Destination */}
                                <div>
                                    <label className="mb-2 block text-sm font-medium text-slate-700">
                                        Compte destinataire
                                    </label>

                                    <input
                                        type="text"
                                        value={destinationAccount}
                                        onChange={(e) =>
                                            setDestinationAccount(
                                                e.target.value
                                            )
                                        }
                                        placeholder="PF-XXXXXXXX"
                                        required
                                        className="
                                            w-full rounded-lg
                                            border border-slate-300
                                            px-4 py-3 text-sm
                                            outline-none transition
                                            focus:border-blue-600
                                            focus:ring-2 focus:ring-blue-100
                                        "
                                    />
                                </div>

                                {/* Amount */}
                                <div>
                                    <label className="mb-2 block text-sm font-medium text-slate-700">
                                        Montant
                                    </label>

                                    <div className="relative">

                                        <input
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={amount}
                                            onChange={(e) =>
                                                setAmount(e.target.value)
                                            }
                                            placeholder="0.00"
                                            required
                                            className="
                                                w-full rounded-lg
                                                border border-slate-300
                                                px-4 py-3 pr-16 text-sm
                                                outline-none transition
                                                focus:border-blue-600
                                                focus:ring-2 focus:ring-blue-100
                                            "
                                        />

                                        <span
                                            className="
                                                absolute right-4 top-1/2
                                                -translate-y-1/2
                                                text-sm font-medium
                                                text-slate-400
                                            "
                                        >
                                            MAD
                                        </span>

                                    </div>
                                </div>

                                {/* Description */}
                                <div>
                                    <label className="mb-2 block text-sm font-medium text-slate-700">
                                        Description
                                        <span className="ml-1 font-normal text-slate-400">
                                            (optionnel)
                                        </span>
                                    </label>

                                    <input
                                        type="text"
                                        value={description}
                                        onChange={(e) =>
                                            setDescription(e.target.value)
                                        }
                                        placeholder="Ex : Paiement facture"
                                        className="
                                            w-full rounded-lg
                                            border border-slate-300
                                            px-4 py-3 text-sm
                                            outline-none transition
                                            focus:border-blue-600
                                            focus:ring-2 focus:ring-blue-100
                                        "
                                    />
                                </div>

                                {/* Submit */}
                                <button
                                    type="submit"
                                    disabled={loading || loadingAccount}
                                    className="
                                        w-full rounded-lg
                                        bg-blue-600 py-3
                                        text-sm font-semibold text-white
                                        transition
                                        hover:bg-blue-700
                                        disabled:cursor-not-allowed
                                        disabled:opacity-60
                                        focus:outline-none
                                        focus-visible:ring-4
                                        focus-visible:ring-blue-100
                                    "
                                >
                                    {loading
                                        ? "Traitement..."
                                        : "Continuer"}
                                </button>

                            </form>

                        </div>

                    </div>

                </main>

            </div>
        </div>
    );
}

export default Transfer;
