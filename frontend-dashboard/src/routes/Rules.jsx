// src/routes/Rules.jsx
import { useQuery, useMutation, gql } from "@apollo/client";
import { useState } from "react";
import { toast } from "react-toastify";

const GET_RULES = gql`
    query { alertRules { id field maxVal enabled } }
`;
const UPDATE_RULE = gql`
    mutation($id: ID!, $maxVal: Float!, $enabled: Boolean!) {
        updateRule(id: $id, maxVal: $maxVal, enabled: $enabled) {
            id field maxVal enabled
        }
    }
`;

function RuleRow({ r, onSave }) {
    const [maxVal, setMaxVal] = useState(r.maxVal);
    const [enabled, setEnabled] = useState(r.enabled);
    const [saving, setSaving] = useState(false);

    const save = async () => {
        if (maxVal <= 0) {
            toast.error("Max value must be > 0");
            return;
        }
        setSaving(true);
        try {
            await onSave({ variables: { id: r.id, maxVal: parseFloat(maxVal), enabled } });
            toast.success("Rule saved");
        } catch {
            toast.error("Save failed");
        } finally {
            setSaving(false);
        }
    };

    return (
        <tr>
            <td className="p-2">{r.field}</td>
            <td className="p-2">
                <input
                    type="number"
                    min="0"
                    value={maxVal}
                    onChange={e => setMaxVal(e.target.value)}
                    className="border p-1 rounded w-20"
                />
            </td>
            <td className="p-2 text-center">
                <input
                    type="checkbox"
                    checked={enabled}
                    onChange={e => setEnabled(e.target.checked)}
                />
            </td>
            <td className="p-2 text-center">
                <button
                    onClick={save}
                    disabled={saving}
                    className="px-3 py-1 rounded bg-blue-600 text-white disabled:opacity-50"
                >
                    {saving ? "…" : "Save"}
                </button>
            </td>
        </tr>
    );
}

export default function RulesPage() {
    const { data, loading, error } = useQuery(GET_RULES);
    const [updateRule] = useMutation(UPDATE_RULE, {
        refetchQueries: [{ query: GET_RULES }],
    });
    if (loading) return <p>Loading rules…</p>;
    if (error) return <p>Error loading rules</p>;

    return (
        <div className="p-4">
            <h1 className="text-xl font-bold mb-4">Alert Rules</h1>
            <table className="min-w-full border-collapse">
                <thead className="bg-gray-100">
                <tr>
                    <th className="p-2">Field</th>
                    <th className="p-2">Max Value</th>
                    <th className="p-2">Enabled</th>
                    <th className="p-2">Action</th>
                </tr>
                </thead>
                <tbody>
                {data.alertRules.map(r => (
                    <RuleRow key={r.id} r={r} onSave={updateRule} />
                ))}
                </tbody>
            </table>
        </div>
    );
}
