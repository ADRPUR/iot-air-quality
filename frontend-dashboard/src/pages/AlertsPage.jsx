/* ------------------------------------------------------
   pages/AlertsPage.jsx – live list of fired alerts (read‑only)
------------------------------------------------------ */
import { useQuery, useSubscription } from "@apollo/client";
import { ALERT_HISTORY_QUERY, ALERT_FIRED_SUB } from "@/api/alert.gql.js";
import StatusState from "@/components/common/StatusState.jsx";
import dayjs from "dayjs";

export default function AlertsPage() {
    /* snapshot */
    const { data, loading, error } = useQuery(ALERT_HISTORY_QUERY, {
        fetchPolicy: "network-only",
    });

    const rows = data?.alertHistory ?? [];

    /* live push – Apollo 3.8+: useSubscription + cache update */
    useSubscription(ALERT_FIRED_SUB, {
        onData: ({ client, data }) => {
            const newAlert = data.data.alertFired;
            client.cache.updateQuery({ query: ALERT_HISTORY_QUERY }, (prev) => {
                const prevRows = prev?.alertHistory ?? [];
                return { alertHistory: [newAlert, ...prevRows] };
            });
        },
    });

    if (loading) return <StatusState type="loading" text="Loading alerts…" />;
    if (error)   return <StatusState type="error"   text="Server unavailable" />;
    if (rows.length === 0) return <StatusState type="empty" text="No alerts" />;

    return (
        <div className="p-6 max-w-5xl mx-auto">
            <h1 className="text-2xl font-semibold mb-6">Alert History</h1>

            <div className="overflow-x-auto shadow rounded-lg ring-1 ring-gray-200">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                    <tr className="text-left text-sm font-semibold text-gray-600">
                        <th className="px-4 py-3">Sensor</th>
                        <th className="px-4 py-3">Field</th>
                        <th className="px-4 py-3">Value</th>
                        <th className="px-4 py-3">Limit</th>
                        <th className="px-4 py-3">Channels</th>
                        <th className="px-4 py-3">Time</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100 text-sm">
                    {rows.map((a) => (
                        <tr key={a.id} className="hover:bg-gray-50">
                            <td className="px-4 py-2 font-medium">{a.sensorId}</td>
                            <td className="px-4 py-2 capitalize">{a.field}</td>
                            <td className="px-4 py-2">{a.value.toFixed(2)}</td>
                            <td className="px-4 py-2">{a.limit.toFixed(2)}</td>
                            <td className="px-4 py-2 whitespace-pre-line">
                                {a.channels.join(", ")}
                            </td>
                            <td className="px-4 py-2">
                                {dayjs(a.ts).format("YYYY‑MM‑DD HH:mm:ss")}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
