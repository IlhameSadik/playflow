
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import ThreeArtwork from "../components/ThreeArtwork";

// Fonctionnalités affichées dans le panneau latéral
const features = [
  "Envoyez et recevez de l'argent en quelques secondes",
  "Suivez toutes vos dépenses en temps réel",
  "Vos données sont chiffrées et protégées",
];

export default function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  // Gestion des champs
  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // Inscription
  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (form.password !== form.confirmPassword) {
      setError("Les mots de passe ne correspondent pas.");
      return;
    }

    if (form.password.length < 8) {
      setError("Le mot de passe doit contenir au moins 8 caractères.");
      return;
    }

    setLoading(true);

    try {
      const response = await api.post("/auth/register", {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim().toLowerCase(),
        password: form.password,
      });

      setSuccess(
        response.data?.message || "Compte créé avec succès !"
      );

      setTimeout(() => {
        navigate("/login");
      }, 1500);

    } catch (err) {
  console.error("ERREUR INSCRIPTION :", err);

  console.log("STATUS :", err.response?.status);
  console.log("DATA :", err.response?.data);
  console.log("MESSAGE :", err.message);

  setError(
    err.response?.data?.message ||
    err.response?.data?.error ||
    err.message ||
    "Erreur inconnue"
  );
} finally {
  setLoading(false);
}
  };

  const inputClass =
    "w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100";

  return (
    <div className="flex min-h-screen bg-slate-50">

      {/* PANNEAU LATÉRAL - DESKTOP */}
      <aside className="relative hidden w-1/2 flex-col justify-between overflow-hidden bg-gradient-to-br from-blue-600 via-blue-700 to-indigo-800 p-12 text-white lg:flex">

        {/* Décoration */}
        <div className="absolute -top-24 -right-24 h-72 w-72 rounded-full bg-white/10 blur-2xl" />
        <div className="absolute -bottom-32 -left-16 h-80 w-80 rounded-full bg-indigo-400/20 blur-3xl" />

        {/* Logo */}
        <h1 className="relative text-2xl font-bold tracking-tight">
          PayFlow
        </h1>

        {/* Présentation */}
        <div className="relative">
          <h2 className="text-4xl font-bold leading-tight">
            Gérez votre argent
            <br />
            simplement.
          </h2>


          {/* Fonctionnalités */}
          <ul className="mt-8 space-y-4">
            {features.map((feature) => (
              <li
                key={feature}
                className="flex items-start gap-3 text-left text-blue-50"
              >
                <span className="mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-white/20">
                  <svg
                    className="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth="3"
                    aria-hidden="true"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                </span>

                <span className="flex-1 text-left">
                  {feature}
                </span>
              </li>
            ))}
          </ul>
        </div>

        {/* Footer */}
        <p className="relative text-sm text-blue-100">
          © 2026 PayFlow
        </p>
      </aside>

      {/* FORMULAIRE */}
      <main className="flex w-full items-center justify-center px-4 py-12 sm:px-8 lg:w-1/2">

        <div className="w-full max-w-md">

          {/* Logo mobile */}
          <div className="mb-8 text-center lg:hidden">
            <h1 className="text-3xl font-bold text-blue-600">
              PayFlow
            </h1>
            <p className="mt-2 text-slate-500">
              Gérez votre argent simplement
            </p>
          </div>

          {/* Carte du formulaire */}
          <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/60 sm:p-10">

            <h2 className="text-2xl font-semibold text-slate-900">
              Créer un compte
            </h2>

            <p className="mt-2 text-sm text-slate-500">
              Rejoignez PayFlow pour gérer votre argent simplement.
            </p>

            <form onSubmit={handleSubmit} className="mt-6 space-y-4">

              {/* Prénom */}
              <input
                name="firstName"
                type="text"
                placeholder="Prénom"
                autoComplete="given-name"
                value={form.firstName}
                onChange={handleChange}
                required
                className={inputClass}
              />

              {/* Nom */}
              <input
                name="lastName"
                type="text"
                placeholder="Nom"
                autoComplete="family-name"
                value={form.lastName}
                onChange={handleChange}
                required
                className={inputClass}
              />

              {/* Email */}
              <input
                name="email"
                type="email"
                placeholder="Adresse e-mail"
                autoComplete="email"
                value={form.email}
                onChange={handleChange}
                required
                className={inputClass}
              />

              {/* Mot de passe */}
              <input
                name="password"
                type="password"
                placeholder="Mot de passe (8 caractères minimum)"
                autoComplete="new-password"
                value={form.password}
                onChange={handleChange}
                minLength={8}
                required
                className={inputClass}
              />

              {/* Confirmation du mot de passe */}
              <input
                name="confirmPassword"
                type="password"
                placeholder="Confirmer le mot de passe"
                autoComplete="new-password"
                value={form.confirmPassword}
                onChange={handleChange}
                minLength={8}
                required
                className={inputClass}
              />

              {/* Message d'erreur */}
              {error && (
                <p
                  role="alert"
                  className="rounded-lg bg-red-50 p-3 text-sm text-red-700"
                >
                  {error}
                </p>
              )}

              {/* Message de succès */}
              {success && (
                <p
                  role="status"
                  className="rounded-lg bg-green-50 p-3 text-sm text-green-700"
                >
                  {success}
                </p>
              )}

              {/* Bouton d'inscription */}
              <button
                type="submit"
                disabled={loading}
                className="w-full rounded-xl bg-blue-600 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {loading
                  ? "Création du compte..."
                  : "Créer mon compte"}
              </button>

            </form>

            {/* Lien vers Login */}
            <p className="mt-6 text-center text-sm text-slate-500">
              Déjà un compte ?{" "}
              <Link
                to="/login"
                className="font-semibold text-blue-600 hover:underline"
              >
                Se connecter
              </Link>
            </p>

            {/* Illustration Three.js */}
            <div className="mt-6 -mb-10 flex justify-center">
              <ThreeArtwork />
            </div>

          </div>
        </div>
      </main>
    </div>
  );
}
