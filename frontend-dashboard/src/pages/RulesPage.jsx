/* ------------------------------------------------------
   pages/RulesPage.jsx – view & edit alert rules
------------------------------------------------------ */
import { useQuery, useMutation } from "@apollo/client";
import {
    ALERT_RULES_QUERY,
    UPDATE_RULE_MUTATION,
} from "@/api/alert.gql.js";
import StatusState from "@/components/common/StatusState.jsx";
import { useState } from "react";
import clsx from "clsx";

export default function RulesPage() {
    const { data, loading, error } = useQuery(ALERT_RULES_QUERY, {
        fetchPolicy: "cache-and-network",
    });

    const [updateRule, { loading: saving }] = useMutation(UPDATE_RULE_MUTATION, {
        refetchQueries: ["AlertRules"],
    });

    /* local editable copy */
    const [drafts, setDrafts] = useState({}); // id -> { maxVal, enabled }

    const rules = data?.alertRules ?? [];

    /* ---------- render guards ---------- */
    if (loading && !data) return <StatusState type="loading" text="Loading rules…" />;
    if (error)             return <StatusState type="error"   text="Server unavailable" />;
    if (!rules.length)     return <StatusState type="empty"   text="No rules configured" />;

    /* ---------- helpers ---------- */
    const edited = (r) => drafts[r.id] ?? { maxVal: r.maxVal, enabled: r.enabled };

    const onFieldChange = (id, key, value) => {
        setDrafts((d) => ({ ...d, [id]: { ...edited({ id, ...d[id] }), [key]: value } }));
    };

    const onSave = async (id) => {
        const { maxVal, enabled } = drafts[id];
        await updateRule({ variables: { id, maxVal: parseFloat(maxVal), enabled } });
        setDrafts((d) => {
            const n = { ...d };
            delete n[id];
            return n;
        });
    };

    /* ---------- UI ---------- */
    return (
        <div className="p-6 max-w-5xl mx-auto">
            <h1 className="text-2xl font-semibold mb-6">Alert Rules</h1>

            <div className="overflow-x-auto shadow rounded-lg ring-1 ring-gray-200">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                    <thead className="bg-gray-50">
                    <tr className="text-left font-semibold text-gray-600">
                        <th className="px-4 py-3">Field</th>
                        <th className="px-4 py-3">Max value</th>
                        <th className="px-4 py-3">Enabled</th>
                        <th className="px-4 py-3 w-24" />
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                    {rules.map((r) => {
                        const d = edited(r);
                        const changed = d.maxVal !== r.maxVal || d.enabled !== r.enabled;
                        return (
                            <tr key={r.id} className="hover:bg-gray-50">
                                <td className="px-4 py-2 capitalize font-medium">{r.field}</td>
                                <td className="px-4 py-2">
                                    <input
                                        type="number"
                                        step="0.1"
                                        min="0"
                                        value={d.maxVal}
                                        onChange={(e) => onFieldChange(r.id, "maxVal", e.target.value)}
                                        className="w-28 border rounded px-2 py-1 text-right"
                                    />
                                </td>
                                <td className="px-4 py-2">
                                    <input
                                        type="checkbox"
                                        checked={d.enabled}
                                        onChange={(e) => onFieldChange(r.id, "enabled", e.target.checked)}
                                    />
                                </td>
                                <td className="px-4 py-2 text-right">
                                    <button
                                        disabled={!changed || saving}
                                        onClick={() => onSave(r.id)}
                                        className={clsx(
                                            "px-3 py-1.5 rounded text-xs text-white",
                                            changed ? "bg-emerald-500 hover:bg-emerald-600" : "bg-gray-300"
                                        )}
                                    >
                                        Save
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
