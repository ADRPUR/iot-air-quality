import { createContext, useContext, useState } from "react";

const AuthCtx = createContext();
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthCtx);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(localStorage.getItem("user"));
    const login  = (u, cb) => { localStorage.setItem("user", u); setUser(u); cb?.(); };
    const logout = () => { localStorage.removeItem("user"); setUser(null); };
    return <AuthCtx.Provider value={{ user, login, logout }}>{children}</AuthCtx.Provider>;
}