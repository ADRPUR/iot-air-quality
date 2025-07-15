// Simple wrapper around AuthContext
import { useAuthInternal } from "@/context/AuthContext.jsx";

/**
 * Returns the current auth object: { user, login(), logout() }
 */
export default function useAuth() {
    return useAuthInternal();
}

