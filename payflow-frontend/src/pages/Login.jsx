import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import ThreeArtwork from "../components/ThreeArtwork";
/* ---------- Icônes (SVG inline, aucune dépendance) ---------- */
const MailIcon = () => (
    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
        <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l9 6 9-6M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
    </svg>
);

const LockIcon = () => (
    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
        <path strokeLinecap="round" strokeLinejoin="round" d="M16 11V7a4 4 0 10-8 0v4m-1 0h10a2 2 0 012 2v6a2 2 0 01-2 2H7a2 2 0 01-2-2v-6a2 2 0 012-2z" />
    </svg>
);

const EyeIcon = ({ off }) =>
    off ? (
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 012.293-3.95M6.6 6.6A9.96 9.96 0 0112 5c4.478 0 8.268 2.943 9.543 7a9.97 9.97 0 01-4.132 5.411M3 3l18 18M9.88 9.88a3 3 0 104.24 4.24" />
        </svg>
    ) : (
        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
    );

const Spinner = () => (
    <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
    </svg>
);

const features = [
    "Envoyez et recevez de l'argent en quelques secondes",
    "Suivez toutes vos dépenses en temps réel",
    "Vos données sont chiffrées et protégées",
];

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (loading) return;

        setError("");
        setLoading(true);

        try {
const data = await login(email, password);

if (data.role === "ADMIN") {
    navigate("/admin/dashboard");
} else {
    navigate("/dashboard");
}
} catch (err) {
            setError(
                err.response?.data?.message || "Email ou mot de passe incorrect."
            );
        } finally {
            setLoading(false);
        }
    };

    const inputBase =
        "w-full rounded-xl border bg-white py-3 pl-11 text-sm text-slate-900 placeholder:text-slate-400 outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100";
    const inputBorder = error ? "border-red-300" : "border-slate-300";

    return (
        <div className="min-h-screen grid lg:grid-cols-2 bg-slate-50">

            {/* Panneau marque (desktop uniquement) */}
            <aside className="hidden lg:flex flex-col justify-between bg-gradient-to-br from-blue-600 via-blue-700 to-indigo-800 p-12 text-white relative overflow-hidden">
                <div className="absolute -top-24 -right-24 h-72 w-72 rounded-full bg-white/10 blur-2xl" />
                <div className="absolute -bottom-32 -left-16 h-80 w-80 rounded-full bg-indigo-400/20 blur-3xl" />

                <h1 className="relative text-2xl font-bold tracking-tight">PayFlow</h1>

                <div className="relative">
                    <h2 className="text-4xl font-bold leading-tight">
                        Gérez votre argent <br /> simplement.
                    </h2>
                    <ul className="mt-8 space-y-4">
                        {features.map((f) => (
                            <li key={f} className="flex items-start gap-3 text-left text-blue-50" style={{ textAlign: "left" }}>
    <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-white/20">
        <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="3" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
        </svg>
    </span>
    <span className="flex-1 text-left" style={{ textAlign: "left" }}>{f}</span>
</li>
                        ))}
                    </ul>
                </div>

                <p className="relative text-sm text-blue-100">© 2026 PayFlow</p>
            </aside>

            {/* Formulaire */}
            <main className="flex items-center justify-center px-4 py-12 sm:px-8">
                <div className="w-full max-w-md">

                    {/* Logo mobile */}
                    <div className="mb-8 text-center lg:hidden">
                        <h1 className="text-3xl font-bold text-blue-600">PayFlow</h1>
                        <p className="mt-2 text-slate-500">Gérez votre argent simplement</p>
                    </div>

                    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/60 sm:p-10">
                        <h2 className="text-2xl font-semibold text-slate-900">
                            Bon retour 
                        </h2>
                        <p className="mt-2 text-sm text-slate-500">
                            Connectez-vous à votre compte PayFlow
                        </p>

                        <form onSubmit={handleSubmit} className="mt-8 space-y-5" noValidate={false}>

                            {/* Email */}
                            <div>
                                <label htmlFor="email" className="mb-2 block text-sm font-medium text-slate-700">
                                    Email
                                </label>
                                <div className="relative">
                                    <span className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
                                        <MailIcon />
                                    </span>
                                    <input
                                        id="email"
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        placeholder="exemple@email.com"
                                        autoComplete="email"
                                        autoFocus
                                        required
                                        aria-invalid={!!error}
                                        className={`${inputBase} ${inputBorder} pr-4`}
                                    />
                                </div>
                            </div>

                            {/* Mot de passe */}
                            <div>
                                {/* <div className="mb-2 flex items-center justify-between">
                                    <label htmlFor="password" className="block text-sm font-medium text-slate-700">
                                        Mot de passe
                                    </label>
                                    <Link
                                        to="/forgot-password"
                                        className="text-sm font-medium text-blue-600 hover:text-blue-700 hover:underline"
                                    >
                                        Mot de passe oublié ?
                                    </Link>
                                </div> */}
                                <div className="relative">
                                    <span className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
                                        <LockIcon />
                                    </span>
                                    <input
                                        id="password"
                                        type={showPassword ? "text" : "password"}
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        placeholder="••••••••"
                                        autoComplete="current-password"
                                        required
                                        aria-invalid={!!error}
                                        className={`${inputBase} ${inputBorder} pr-12`}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowPassword((v) => !v)}
                                        aria-label={showPassword ? "Masquer le mot de passe" : "Afficher le mot de passe"}
                                        className="absolute inset-y-0 right-0 flex items-center rounded-r-xl px-3.5 text-slate-400 transition hover:text-slate-600 focus:outline-none focus-visible:text-blue-600"
                                    >
                                        <EyeIcon off={showPassword} />
                                    </button>
                                </div>
                            </div>

                            {/* Erreur */}
                            {error && (
                                <div
                                    role="alert"
                                    className="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
                                >
                                    <svg className="mt-0.5 h-5 w-5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
                                    </svg>
                                    <span>{error}</span>
                                </div>
                            )}

                            {/* Bouton */}
                            <button
                                type="submit"
                                disabled={loading}
                                className="flex w-full items-center justify-center gap-2 rounded-xl bg-blue-600 py-3 text-sm font-semibold text-white shadow-lg shadow-blue-600/25 transition hover:bg-blue-700 focus:outline-none focus-visible:ring-4 focus-visible:ring-blue-200 active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-60"
                            >
                                {loading && <Spinner />}
                                {loading ? "Connexion..." : "Se connecter"}
                            </button>
                        </form>

                        <p className="mt-8 text-center text-sm text-slate-500">
                            Pas encore de compte ?{" "}
                            <Link to="/register" className="font-semibold text-blue-600 hover:text-blue-700 hover:underline">
                                Créer un compte
                            </Link>
                        </p>
                       <div className="mt-6 -mb-8 sm:-mb-10"><ThreeArtwork /></div>
                    </div>
 
                    <p className="mt-6 text-center text-xs text-slate-400 lg:hidden">
                        © 2026 PayFlow
                    </p>
                </div>
            </main>
        </div>
    );
}

export default Login;