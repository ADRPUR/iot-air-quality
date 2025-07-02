import {useState} from "react";
import {useNavigate} from "react-router-dom"; // 👈 nou
import {useAuth} from "../context/AuthContext";

export default function Login() {
    const { login } = useAuth();
    const [name, setName] = useState("");
    const navigate  = useNavigate();

    function handleSubmit(e) {
        e.preventDefault();
        login(name || "demo", () => {          // callback fired *after* user is set
            navigate("/dashboard", { replace: true });
        });
    }

    return (
        <div className="h-screen flex items-center justify-center">
            <form onSubmit={handleSubmit} className="card p-8 shadow-lg w-80">
                <h1 className="text-xl mb-4">Sign in</h1>
                <input
                    className="input w-full mb-4"
                    placeholder="User name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <button className="btn w-full" type="submit">
                    Enter
                </button>
            </form>
        </div>
    );
}
