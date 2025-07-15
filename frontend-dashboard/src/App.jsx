import {BrowserRouter as Router, Routes, Route, Navigate, useLocation} from "react-router-dom";
import {ApolloProvider} from "@apollo/client";
import {alertClient} from "@/api/clients.js";
import Navbar from "@/components/layout/Navbar.jsx";
import Graphs from "@/pages/Graphs.jsx";
import RulesPage from "@/pages/RulesPage.jsx";
import AlertsPage from "@/pages/AlertsPage.jsx";
import Login from "@/pages/auth/Login.jsx";
import {AuthProvider} from "@/context/AuthContext.jsx";
import useAuth from "@/hooks/useAuth.js";
import Notifications from "@/components/Notifications.jsx";
import SensorsPage from "@/pages/SensorsPage.jsx";
import RealtimeDashboard from "@/pages/RealtimeDashboard.jsx";

function PrivateRoute({children}) {
    const {user} = useAuth();
    const location = useLocation();
    if (!user) {
        return <Navigate to="/login" state={{from: location}} replace/>;
    }
    return children;                      // ← must render the children!
}

export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Navbar/>
                <ApolloProvider client={alertClient}>
                    <Notifications/>
                </ApolloProvider>
                <Routes>
                    {/* redirect root to dashboard */}
                    <Route path="/" element={<Navigate to="/dashboard" replace/>}/>

                    {/* protected route */}
                    <Route path="/dashboard" element={
                        <PrivateRoute>
                            <RealtimeDashboard/>
                        </PrivateRoute>
                    }/>
                    <Route path="/graphs" element={
                        <PrivateRoute>
                            <Graphs/>
                        </PrivateRoute>
                    }/>
                    <Route path="/alerts" element={
                        <PrivateRoute>
                            <ApolloProvider client={alertClient}>
                                <AlertsPage/>
                            </ApolloProvider>
                        </PrivateRoute>
                    }/>
                    <Route path="/rules" element={
                        <PrivateRoute>
                            <ApolloProvider client={alertClient}>
                                <RulesPage/>
                            </ApolloProvider>
                        </PrivateRoute>
                    }/>

                    <Route path="/sensors" element={
                        <PrivateRoute>
                            <SensorsPage/>
                        </PrivateRoute>
                    }/>

                    <Route path="/login" element={<Login/>}/>
                    {/* fallback 404 */}
                    <Route path="*" element={<Navigate to="/dashboard" replace/>}/>
                </Routes>
            </Router>
        </AuthProvider>
    );
}