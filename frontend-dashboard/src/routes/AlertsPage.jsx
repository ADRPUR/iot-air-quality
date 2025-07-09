import {useQuery, gql} from "@apollo/client";
import {alertClient} from "../apolloClient.js";

const ALERT_HISTORY = gql`
    query AlertHistory($limit: Int!) {
        alertHistory(limit: $limit) {
            id
            ts
            sensorId
            field
            value
            limit
            channels
        }
    }
`;

export default function AlertsPage() {
    const {data, loading, error} = useQuery(ALERT_HISTORY, {
        client: alertClient,
        variables: {limit: 100},
        pollInterval: 5000,
    });
    if (loading) return <p>Loading alerts…</p>;
    if (error) return <p>Error loading alerts</p>;

    return (
        <div className="p-6">
            <h1 className="text-2xl font-semibold mb-4">Alert History</h1>
            <div className="overflow-x-auto border rounded-lg shadow-sm">
                <table className="min-w-full bg-white divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                    <tr>
                        {["Time", "Sensor", "Field", "Value", "Limit", "Channels"].map((h) => (
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
                    {data.alertHistory.map((a) => (
                        <tr key={a.id} className="hover:bg-gray-50">
                            <td className="px-4 py-2 text-sm text-gray-700">
                                {new Date(a.ts).toLocaleString()}
                            </td>
                            <td className="px-4 py-2 text-sm text-gray-700">{a.sensorId}</td>
                            <td className="px-4 py-2 text-sm text-gray-700 capitalize">
                                {a.field}
                            </td>
                            <td className="px-4 py-2 text-sm text-gray-700">{a.value}</td>
                            <td className="px-4 py-2 text-sm text-gray-700">{a.limit}</td>
                            <td className="px-4 py-2 text-sm text-gray-700">
                                {a.channels.map((c) => (
                                    <span
                                        key={c}
                                        className="inline-block mr-1 mb-1 px-2 py-0.5 text-xs font-medium bg-blue-100 text-blue-800 rounded-full"
                                    >
                      {c}
                    </span>
                                ))}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
