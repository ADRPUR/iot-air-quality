// src/components/Navbar.jsx
import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login", { replace: true });
    }

    return (
        <nav className="bg-gray-800 text-gray-100 p-4 flex items-center justify-between">
            {/* Brand + nav links */}
            <div className="flex items-center space-x-6">
                <NavLink to="/" className="font-bold text-xl">
                    Smart Home
                </NavLink>
                {user && (
                    <>
                        <NavLink
                            to="/dashboard"
                            className={({ isActive }) =>
                                isActive ? "underline" : "hover:underline"
                            }
                        >
                            Dashboard
                        </NavLink>
                        <NavLink
                            to="/alerts"
                            className={({ isActive }) =>
                                isActive ? "underline" : "hover:underline"
                            }
                        >
                            Alerts
                        </NavLink>
                        <NavLink
                            to="/rules"
                            className={({ isActive }) =>
                                isActive ? "underline" : "hover:underline"
                            }
                        >
                            Rules
                        </NavLink>
                    </>
                )}
            </div>

            {/* User & logout */}
            {user && (
                <div>
                    <span className="mr-4">Hello, {user}</span>
                    <button onClick={handleLogout} className="underline">
                        Logout
                    </button>
                </div>
            )}
        </nav>
    );
}
