import {BrowserRouter as Router, Routes, Route, Navigate, useLocation} from "react-router-dom";
import {ApolloProvider} from "@apollo/client";
import {ingestClient, alertClient} from "./apolloClient.js";
import Navbar from "./components/Navbar.jsx";
import Dashboard from "./routes/Dashboard.jsx";
import RulesPage from "./routes/RulesPage.jsx";
import AlertsPage from "./routes/AlertsPage.jsx";
import Login from "./routes/Login.jsx";
import {AuthProvider, useAuth} from "./context/AuthContext.jsx";
import Notifications from "./components/Notifications.jsx";
import SensorsPage from "./routes/SensorsPage.jsx";

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
        <ApolloProvider client={ingestClient}>
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
                                <Dashboard/>
                            </PrivateRoute>
                        }/>
                        <Route path="/alerts" element={
                            <PrivateRoute>
                                <AlertsPage/>
                            </PrivateRoute>
                        }/>
                        <Route path="/rules" element={
                            <PrivateRoute>
                                <RulesPage/>
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
        </ApolloProvider>
    );
}