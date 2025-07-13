import {useQuery, useMutation, gql} from "@apollo/client";
import {useState} from "react";
import { alertClient } from "../apolloClient.js";

// GraphQL
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

export default function RulesPage() {
    const {data, loading, error} = useQuery(GET_RULES, { client: alertClient });
    const [updateRule] = useMutation(UPDATE_RULE, {
        refetchQueries: [{query: GET_RULES}]
    });

    if (loading) return <p className="p-4 text-gray-500">Loading rules ...</p>;
    if (error) return <p className="p-4 text-red-600">Error loading rules</p>;

    return (
        <div className="p-6">
            <h1 className="text-2xl font-semibold mb-4">Alert Rules</h1>
            <div className="overflow-x-auto border rounded-lg shadow-sm">
                <table className="min-w-full bg-white divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                    <tr>
                        {["Field", "Max Value", "Enabled", "Actions"].map(h => (
                            <th
                                key={h}
                                className="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase tracking-wide"
                            >
                                {h}
                            </th>
                        ))}
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-100">
                    {data.alertRules.map(rule => (
                        <RuleRow key={rule.id} rule={rule} onSave={updateRule}/>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function RuleRow({rule, onSave}) {
    const [maxVal, setMaxVal] = useState(rule.maxVal);
    const [enabled, setEnabled] = useState(rule.enabled);

    const save = () => {
        onSave({
            variables: {
                id: rule.id,
                maxVal: parseFloat(maxVal),
                enabled
            }
        });
    };

    return (
        <tr className="hover:bg-gray-50">
            <td className="px-4 py-2 text-sm text-gray-700">{rule.field}</td>
            <td className="px-4 py-2">
                <input
                    type="number"
                    value={maxVal}
                    onChange={e => setMaxVal(e.target.value)}
                    className="w-full px-2 py-1 border rounded focus:outline-none focus:ring-2 focus:ring-blue-300"
                />
            </td>
            <td className="px-4 py-2 text-center">
                <label className="inline-flex items-center">
                    <input
                        type="checkbox"
                        checked={enabled}
                        onChange={e => setEnabled(e.target.checked)}
                        className="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-300"
                    />
                </label>
            </td>
            <td className="px-4 py-2">
                <button
                    onClick={save}
                    className="px-3 py-1 bg-blue-600 text-white text-sm font-medium rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
                >
                    Save
                </button>
            </td>
        </tr>
    );
}
