/* ------------------------------------------------------
   Login page (fullscreen) – pages/auth/Login.jsx
------------------------------------------------------ */
import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { LogIn } from "lucide-react";
import useAuth from "@/hooks/useAuth.js";
import StatusState from "@/components/common/StatusState.jsx";

export default function Login() {
    const { login } = useAuth();
    const navigate   = useNavigate();
    const location   = useLocation();
    const from = location.state?.from?.pathname || "/dashboard";

    const [form, setForm] = useState({ email: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError]     = useState(null);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            await login(form.email, form.password);
            navigate(from, { replace: true });
        } catch (err) {
            console.error(err);
            setError("Invalid credentials");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-md">
                <h1 className="text-2xl font-semibold text-center mb-6 flex items-center justify-center gap-2">
                    <LogIn className="w-6 h-6" /> Sign in
                </h1>

                {error && <StatusState type="error" text={error} />}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            value={form.email}
                            onChange={e => setForm({ ...form, email: e.target.value })}
                            className="w-full rounded-lg border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            value={form.password}
                            onChange={e => setForm({ ...form, password: e.target.value })}
                            className="w-full rounded-lg border px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-sky-600 hover:bg-sky-700 text-white font-semibold py-2 rounded-lg flex items-center justify-center gap-2 disabled:opacity-60"
                    >
                        {loading && <span className="animate-spin w-4 h-4 border-2 border-white border-t-transparent rounded-full" />}
                        Sign in
                    </button>
                </form>
            </div>
        </div>
    );
}
