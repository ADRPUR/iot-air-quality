import { createContext, useContext, useState } from "react";

const AuthCtx = createContext(null);

/**
 * Simple in‑memory auth until backend is implemented.
 * Any email / password pair is accepted.
 */
export const useAuthInternal = () => useContext(AuthCtx);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => localStorage.getItem("user"));

    /**
     * Fake login – stores username (email) in localStorage, resolves Promise.
     * Accepts any credentials, optionally calls callback after set.
     */
    const login = (email, _password, cb) => {
        localStorage.setItem("user", email);
        setUser(email);
        cb?.();
        return Promise.resolve();
    };

    const logout = () => {
        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthCtx.Provider value={{ user, login, logout }}>
            {children}
        </AuthCtx.Provider>
    );
}
