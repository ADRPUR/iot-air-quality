/* ------------------------------------------------------
   Login & Register – pages/auth/Login.jsx
------------------------------------------------------ */
import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { LogIn, UserPlus } from "lucide-react";
import useAuth from "@/hooks/useAuth.js";
import StatusState from "@/components/common/StatusState.jsx";

export default function Login() {
    const { login, register } = useAuth();
    const navigate  = useNavigate();
    const location  = useLocation();
    const from = location.state?.from?.pathname || "/dashboard";

    const [mode, setMode] = useState("login");   // "login" | "register"
    const [form, setForm] = useState({ email: "", password: "", firstName: "", lastName: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError]     = useState(null);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true); setError(null);
        try {
            if (mode === "login") {
                await login(form.email, form.password);
            } else {
                await register(form);
            }
            navigate(from, { replace: true });
        } catch (err) {
            console.error(err);
            setError(err.message ?? "Authentication failed");
        } finally { setLoading(false); }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-md">
                {/* --------------- tabs ---------------- */}
                <div className="flex mb-6 border-b">
                    {["login","register"].map(m => (
                        <button
                            key={m}
                            className={`flex-1 py-2 text-sm font-semibold
                                ${mode===m ? "border-b-2 border-sky-600 text-sky-600"
                                : "text-gray-500 hover:text-gray-700"}`}
                            onClick={() => setMode(m)}
                        >
                            {m==="login" ? "Sign in" : "Register"}
                        </button>
                    ))}
                </div>

                {error && <StatusState type="error" text={error}/>}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <input
                        type="email" placeholder="Email"
                        value={form.email}
                        onChange={e=>setForm({...form,email:e.target.value})}
                        className="w-full rounded-lg border px-3 py-2"
                        required
                    />
                    <input
                        type="password" placeholder="Password"
                        value={form.password}
                        onChange={e=>setForm({...form,password:e.target.value})}
                        className="w-full rounded-lg border px-3 py-2"
                        required
                    />

                    {mode==="register" && (
                        <>
                            <input
                                type="text" placeholder="First name"
                                value={form.firstName}
                                onChange={e=>setForm({...form,firstName:e.target.value})}
                                className="w-full rounded-lg border px-3 py-2"
                            />
                            <input
                                type="text" placeholder="Last name"
                                value={form.lastName}
                                onChange={e=>setForm({...form,lastName:e.target.value})}
                                className="w-full rounded-lg border px-3 py-2"
                            />
                        </>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-sky-600 hover:bg-sky-700 text-white font-semibold py-2 rounded-lg flex items-center justify-center gap-2 disabled:opacity-60"
                    >
                        {loading && <span className="animate-spin w-4 h-4 border-2 border-white border-t-transparent rounded-full" />}
                        {mode==="login" ? <><LogIn className="w-4 h-4"/>Sign in</>
                            : <><UserPlus className="w-4 h-4"/>Register</>}
                    </button>
                </form>
            </div>
        </div>
    );
}
