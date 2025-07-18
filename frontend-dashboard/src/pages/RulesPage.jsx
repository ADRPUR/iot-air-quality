/* ------------------------------------------------------
   pages/RulesPage.jsx – view & edit alert rules
------------------------------------------------------ */
import {useQuery, useMutation} from "@apollo/client";
import {
    ALERT_RULES_QUERY,
    CREATE_RULE_MUTATION,
    UPDATE_RULE_MUTATION,
    DELETE_RULE_MUTATION,
} from "@/api/alert.gql.js";
import StatusState from "@/components/common/StatusState.jsx";
import {useState, useMemo} from "react";
import clsx from "clsx";

const OPS = ["LT", "LTE", "EQ", "GTE", "GT", "CHANGE"];
const LEVELS = ["INFO", "WARN", "CRITICAL"];

export default function RulesPage() {
    const {data, loading, error} = useQuery(ALERT_RULES_QUERY, {
        fetchPolicy: "cache-and-network",
    });
    const rules = data?.alertRules ?? [];

    const [createRule] = useMutation(CREATE_RULE_MUTATION, {refetchQueries: [ALERT_RULES_QUERY]});
    const [updateRule] = useMutation(UPDATE_RULE_MUTATION, {refetchQueries: [ALERT_RULES_QUERY]});
    const [deleteRule] = useMutation(DELETE_RULE_MUTATION, {refetchQueries: [ALERT_RULES_QUERY]});

    /* drafts for row edits */
    const [drafts, setDrafts] = useState({});
    const edited = (r) => ({...r, ...drafts[r.id]});
    const onChange = (id, k, v) =>
        setDrafts((d) => ({...d, [id]: {...d[id], [k]: v}}));

    const onSave = async (id) => {
        const d = drafts[id];
        await updateRule({
            variables: {
                id,
                op: d.op,
                level: d.level ,
                threshold: parseFloat(d.threshold),
                enabled: d.enabled,
            },
        });
        setDrafts((p) => {
            const n = {...p};
            delete n[id];
            return n;
        });
    };

    const onDelete = (id) => confirm("Delete this rule?") && deleteRule({variables: {id}});

    /* create-new rule */
    const [newRule, setNewRule] = useState({
        sensorId: "",
        field: "",
        op: "GT",
        level: "WARN",
        threshold: "",
        enabled: true,
    });
    const canCreate = useMemo(
        () => newRule.sensorId && newRule.field && newRule.threshold !== "",
        [newRule],
    );
    const createHandler = () =>
        createRule({
            variables: {...newRule, threshold: parseFloat(newRule.threshold)},
        }).then(() => setNewRule({sensorId: "", field: "", op: "GT", level: "WARN", threshold: "", enabled: true}));

    /* guards */
    if (loading && !data) return <StatusState type="loading" text="Loading rules…"/>;
    if (error) return <StatusState type="error" text="Server unavailable"/>;

    /* UI */
    return (
        <div className="p-6 max-w-5xl mx-auto space-y-6">
            <h1 className="text-2xl font-semibold">Alert Rules</h1>

            {/* ---------- add-new form ---------- */}
            <div className="ring-1 ring-gray-200 rounded-lg p-4 space-x-2 flex flex-wrap items-end">
                <input
                    className="border rounded px-2 py-1"
                    placeholder="sensorId"
                    value={newRule.sensorId}
                    onChange={(e) => setNewRule({...newRule, sensorId: e.target.value})}
                />
                <input
                    className="border rounded px-2 py-1"
                    placeholder="field"
                    value={newRule.field}
                    onChange={(e) => setNewRule({...newRule, field: e.target.value})}
                />
                <select
                    className="border rounded px-2 py-1 appearance-none focus:outline-none"
                    value={newRule.level}
                    onChange={(e) => setNewRule({ ...newRule, level: e.target.value })}
                >
                    {LEVELS.map((o) => (
                        <option key={o}>{o}</option>
                    ))}
                </select>
                <select
                    className="border rounded px-2 py-1 appearance-none focus:outline-none"
                    value={newRule.op}
                    onChange={(e) => setNewRule({ ...newRule, op: e.target.value })}
                >
                    {OPS.map((o) => (
                        <option key={o}>{o}</option>
                    ))}
                </select>
                <input
                    type="number"
                    step="0.1"
                    className="border rounded px-2 py-1 w-28 text-right"
                    placeholder="threshold"
                    value={newRule.threshold}
                    onChange={(e) => setNewRule({...newRule, threshold: e.target.value})}
                />
                <label className="flex items-center space-x-1">
                    <input
                        type="checkbox"
                        checked={newRule.enabled}
                        onChange={(e) => setNewRule({...newRule, enabled: e.target.checked})}
                    />
                    <span className="text-sm">enabled</span>
                </label>
                <button
                    disabled={!canCreate}
                    onClick={createHandler}
                    className="bg-emerald-500 hover:bg-emerald-600 text-white text-sm px-3 py-1.5 rounded disabled:opacity-50"
                >
                    Add rule
                </button>
            </div>

            {/* ---------- list & edit ---------- */}
            <div className="overflow-x-auto shadow rounded-lg ring-1 ring-gray-200">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                    <thead className="bg-gray-50">
                    <tr className="text-left font-semibold text-gray-600">
                        <th className="px-4 py-3">Sensor</th>
                        <th className="px-4 py-3">Field</th>
                        <th className="px-4 py-3">Level</th>
                        <th className="px-4 py-3">Op</th>
                        <th className="px-4 py-3">Threshold</th>
                        <th className="px-4 py-3">Enabled</th>
                        <th className="px-4 py-3 w-28"/>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {rules.length === 0 ? (
                        <tr>
                            <td colSpan={6} className="text-center py-6 text-gray-500">
                                No rules configured yet
                            </td>
                        </tr>
                    ) : (
                        rules.map((r) => {
                            const d = edited(r);
                            const changed =
                                d.level !== r.level || d.op !== r.op || d.threshold !== r.threshold || d.enabled !== r.enabled;
                            return (
                                <tr key={r.id} className="hover:bg-gray-50">
                                    <td className="px-4 py-2 font-medium">{r.sensorId}</td>
                                    <td className="px-4 py-2">{r.field}</td>
                                    <td className="px-4 py-2">
                                        <select
                                            className="border rounded px-4 py-2 appearance-none"
                                            value={d.level}
                                            onChange={(e) => onChange(r.id, "level", e.target.value)}
                                        >
                                            {LEVELS.map((o) => (
                                                <option key={o}>{o}</option>
                                            ))}
                                        </select>
                                    </td>
                                    <td className="px-4 py-2">
                                        <select
                                            className="border rounded px-4 py-2 appearance-none"
                                            value={d.op}
                                            onChange={(e) => onChange(r.id, "op", e.target.value)}
                                        >
                                            {OPS.map((o) => (
                                                <option key={o}>{o}</option>
                                            ))}
                                        </select>
                                    </td>
                                    <td className="px-4 py-2">
                                        <input
                                            type="number"
                                            step="0.1"
                                            className="border rounded px-2 py-1 w-24 text-right"
                                            value={d.threshold}
                                            onChange={(e) => onChange(r.id, "threshold", e.target.value)}
                                        />
                                    </td>
                                    <td className="px-4 py-2">
                                        <input
                                            type="checkbox"
                                            checked={d.enabled}
                                            onChange={(e) => onChange(r.id, "enabled", e.target.checked)}
                                        />
                                    </td>
                                    <td className="px-4 py-2 flex space-x-2">
                                        <button
                                            className={clsx(
                                                "px-2 py-1 text-xs rounded text-white",
                                                changed
                                                    ? "bg-emerald-500 hover:bg-emerald-600"
                                                    : "bg-gray-300 cursor-default",
                                            )}
                                            disabled={!changed}
                                            onClick={() => onSave(r.id)}
                                        >
                                            Save
                                        </button>
                                        <button
                                            className="px-2 py-1 text-xs rounded bg-red-500 hover:bg-red-600 text-white"
                                            onClick={() => onDelete(r.id)}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            );
                        })
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
