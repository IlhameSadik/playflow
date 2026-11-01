import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

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
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (form.password !== form.confirmPassword) {
      setError("Les mots de passe ne correspondent pas.");
      return;
    }

    setLoading(true);

    try {
      await api.post("/auth/register", {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        password: form.password,
      });

      setSuccess("Compte créé avec succès ! Redirection vers la connexion...");
      setTimeout(() => navigate("/login"), 1500);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Impossible de créer le compte. Vérifiez les informations."
      );
    } finally {
      setLoading(false);
    }
  };

  const inputClass =
    "w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-blue-600 focus:ring-4 focus:ring-blue-100";

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-10">
      <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 shadow-xl">
        <h1 className="text-3xl font-bold text-blue-600">PayFlow</h1>

        <h2 className="mt-6 text-2xl font-semibold text-slate-900">
          Créer un compte
        </h2>
        <p className="mt-2 text-sm text-slate-500">
          Rejoignez PayFlow pour gérer votre argent simplement.
        </p>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <input
            name="firstName"
            placeholder="Prénom"
            autoComplete="given-name"
            value={form.firstName}
            onChange={handleChange}
            required
            className={inputClass}
          />

          <input
            name="lastName"
            placeholder="Nom"
            autoComplete="family-name"
            value={form.lastName}
            onChange={handleChange}
            required
            className={inputClass}
          />

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

          {error && (
            <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
              {error}
            </p>
          )}

          {success && (
            <p className="rounded-lg bg-green-50 p-3 text-sm text-green-700">
              {success}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-xl bg-blue-600 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:opacity-60"
          >
            {loading ? "Création du compte..." : "Créer mon compte"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Déjà un compte ?{" "}
          <Link to="/login" className="font-semibold text-blue-600 hover:underline">
            Se connecter
          </Link>
        </p>
      </div>
    </div>
  );
}