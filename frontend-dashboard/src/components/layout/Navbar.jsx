/* ------------------------------------------------------
   Main top‑navigation bar. Responsive & sticky.
   Uses Tailwind + lucide icon set.
------------------------------------------------------ */
import { NavLink, useNavigate } from "react-router-dom";
import { Menu } from "lucide-react";
import useAuth from "@/hooks/useAuth.js";
import logo from "@/assets/logo.svg";
import { useState } from "react";

export default function Navbar() {
    const { user, logout }           = useAuth();
    const navigate                   = useNavigate();
    const [open, setOpen]            = useState(false);
    const toggle                     = () => setOpen(o => !o);

    function handleLogout() {
        logout();
        navigate("/login", { replace: true });
    }

    const linkClass = ({ isActive }) =>
        `px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200
     ${isActive ? "bg-sky-600 text-white" : "text-gray-700 hover:bg-gray-200"}`;

    return (
        <nav className="sticky top-0 z-30 bg-white shadow-sm">
            {/* Inner wrapper */}
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 flex items-center justify-between h-14">
                {/* Logo & mobile menu button */}
                <div className="flex items-center gap-2">
                    <img src={logo} alt="logo" className="h-16 w-auto py-2"/>
                    <button className="sm:hidden p-2" onClick={toggle}>
                        <Menu className="w-5 h-5 text-gray-600"/>
                    </button>
                </div>

                {/* Links */}
                <div className={`flex-1 sm:flex sm:items-center sm:justify-center ${open ? "block" : "hidden"} sm:block`}>
                    <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-2 sm:space-y-0 text-center">
                        <NavLink to="/dashboard" className={linkClass}>Dashboard</NavLink>
                        <NavLink to="/graphs"    className={linkClass}>Graphs</NavLink>
                        <NavLink to="/alerts"    className={linkClass}>Alerts</NavLink>
                        <NavLink to="/rules"     className={linkClass}>Rules</NavLink>
                        <NavLink to="/sensors"   className={linkClass}>Sensors</NavLink>
                    </div>
                </div>

                {/* User */}
                {user && (
                    <div className="hidden sm:flex items-center gap-3">
                        <span className="text-sm text-gray-600">Hello, {user}</span>
                        <button onClick={handleLogout} className="text-sm text-sky-600 hover:underline">Logout</button>
                    </div>
                )}
            </div>
        </nav>
    );
}
