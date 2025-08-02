import {
    ApolloProvider,
    useMutation,
    ApolloLink,
    InMemoryCache,
} from "@apollo/client";
import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { LOGIN_MUTATION, REGISTER_MUTATION, REFRESH_MUTATION, ME_QUERY } from "@/api/auth.gql.js";
import { authClient } from "@/api/clients.js";

const AuthCtx = createContext(null);
export const useAuthInternal = () => useContext(AuthCtx);

/* ---- Helpers pentru localStorage ----------------------------------- */
const LS_ACCESS  = "iot.access";
const LS_REFRESH = "iot.refresh";

function storeTokens({ accessToken, refreshToken }) {
    localStorage.setItem(LS_ACCESS,  accessToken);
    localStorage.setItem(LS_REFRESH, refreshToken);
}
function clearTokens() {
    localStorage.removeItem(LS_ACCESS);
    localStorage.removeItem(LS_REFRESH);
}

export function AuthProvider({ children }) {
    /* ---------------- state ---------------- */
    const [user, setUser]   = useState(null);
    const [ready, setReady] = useState(false);

    const [loginMut]    = useMutation(LOGIN_MUTATION,   { client: authClient });
    const [registerMut] = useMutation(REGISTER_MUTATION,{ client: authClient });
    const [refreshMut]  = useMutation(REFRESH_MUTATION, { client: authClient });

    /* ---------------- boot-strap ------------- */
    useEffect(() => {
        const access = localStorage.getItem(LS_ACCESS);
        if (!access) { setReady(true); return; }

        authClient
            .query({ query: ME_QUERY, fetchPolicy: "network-only" })
            .then(r => setUser(r.data.me))
            .catch(() => clearTokens())
            .finally(() => setReady(true));
    }, []);

    /* ---------------- API -------------------- */
    async function login(email, password) {
        const { data } = await loginMut({ variables: { email, password } });
        storeTokens(data.login);
        setUser(email);
    }

    async function register({ email, password, firstName, lastName }) {
        if (!email) throw new Error("Email is required");

        const variables = {
            email: email.trim(),
            password,
            firstName: firstName || null,
            lastName: lastName || null
        };

        await registerMut({ variables });
        await login(email, password);
    }

    function logout() {
        clearTokens();
        setUser(null);
    }

    /* ---- token inject in header pentru toate cererile auth-client ---- */
    const authLink = useMemo(() =>
        new ApolloLink((operation, forward) => {
            const token = localStorage.getItem(LS_ACCESS);
            if (token) operation.setContext(({ headers = {} }) => ({
                headers: { ...headers, Authorization: `Bearer ${token}` },
            }));
            return forward(operation);
        }), []);
    authClient.setLink(authLink.concat(authClient.link));
    // cache rămâne același

    if (!ready) return null;           // or a global spinner

    return (
        <AuthCtx.Provider value={{ user, login, register, logout }}>
            {children}
        </AuthCtx.Provider>
    );
}
