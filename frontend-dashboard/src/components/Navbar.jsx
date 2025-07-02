// src/components/Navbar.jsx
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login", { replace: true });   // redirect
    }

    return (
        <nav className="bg-gray-800 text-gray-100 p-4 flex justify-between">
            <span className="font-bold">Smart Home Dashboard</span>

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
