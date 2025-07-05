import {BrowserRouter as Router, Routes, Route, Navigate, useLocation} from "react-router-dom";
import { ApolloProvider } from "@apollo/client";
import { client } from "./apolloClient.js";
import { ApolloProvider as AlertApolloProvider } from "@apollo/client";
import { alertClient } from "./alertApolloClient.js";
import Navbar from "./components/Navbar.jsx";
import Dashboard from "./routes/Dashboard.jsx";
import RulesPage from "./routes/RulesPage.jsx";
import AlertsPage from "./routes/AlertsPage.jsx";
import Login from "./routes/Login.jsx";
import { AuthProvider, useAuth } from "./context/AuthContext.jsx";

function PrivateRoute({ children }) {
    const { user } = useAuth();
    const location = useLocation();
    if (!user) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }
    return children;                      // ← must render the children!
}
export default function App() {
    return (
        <ApolloProvider client={client}>
            <AuthProvider>
                <Router>
                    <Navbar />
                    <Routes>
                        {/* redirect root to dashboard */}
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />

                        {/* protected route */}
                        <Route
                            path="/dashboard"
                            element={
                                <PrivateRoute>
                                    <Dashboard />
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="/alerts"
                            element={
                                <PrivateRoute>
                                    <AlertApolloProvider client={alertClient}>
                                        <AlertsPage />
                                    </AlertApolloProvider>
                                </PrivateRoute>
                            }
                        />
                        <Route
                            path="/rules"
                            element={
                                <PrivateRoute>
                                    <AlertApolloProvider client={alertClient}>
                                        <RulesPage />
                                    </AlertApolloProvider>
                                </PrivateRoute>
                            }
                        />

                        <Route path="/login" element={<Login />} />
                        {/* fallback 404 */}
                        <Route path="*" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                </Router>
            </AuthProvider>
        </ApolloProvider>
    );
}