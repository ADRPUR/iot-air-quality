// src/components/DashboardControls.jsx
import { useState } from "react";

export default function DashboardControls({ onChange }) {
    const ranges = [
        { label: "Last 1h", value: "1h" },
        { label: "Last 6h", value: "6h" },
        { label: "Last 24h", value: "24h" },
        { label: "Custom", value: "custom" },
    ];
    const [sel, setSel] = useState("1h");
    const [custom, setCustom] = useState({ from: "", to: "" });

    const apply = () => {
        const now = Date.now();

        let fromMs, toMs;

        if (sel !== "custom") {
            const hours = Number(sel.replace("h", ""));
            toMs   = now;
            fromMs = now - hours * 3_600_000;
        } else {
            if (!custom.from || !custom.to) return;
            fromMs = new Date(custom.from).getTime();
            toMs   = new Date(custom.to  ).getTime();
            if (isNaN(fromMs) || isNaN(toMs) || fromMs >= toMs) return;
        }

        onChange({ fromMs, toMs });
    };

    return (
        <div className="px-6 mt-4 mb-4 flex items-center gap-3">
            <select
                value={sel}
                onChange={e => setSel(e.target.value)}
                className="border p-1 rounded"
            >
                {ranges.map(r => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                ))}
            </select>
            {sel === "custom" && (
                <>
                    <input
                        type="datetime-local"
                        value={custom.from}
                        onChange={e => setCustom(c => ({ ...c, from: e.target.value }))}
                        className="border p-1 rounded"
                    />
                    <input
                        type="datetime-local"
                        value={custom.to}
                        onChange={e => setCustom(c => ({ ...c, to: e.target.value }))}
                        className="border p-1 rounded"
                    />
                </>
            )}
            <button onClick={apply} className="px-4 py-1 rounded bg-blue-600 hover:bg-blue-700 text-white shadow">
                Apply
            </button>
        </div>
    );
}
