/***********************************************************************
 * pages/AlertsPage.jsx – live list of generated alerts
 **********************************************************************/
import {useApolloClient, useMutation, useQuery, useSubscription,} from "@apollo/client";
import {ACK_ALERT_MUTATION, ALERT_FIRED_SUB, ALERT_HISTORY_QUERY,} from "@/api/alert.gql.js";
import StatusState from "@/components/common/StatusState.jsx";
import clsx from "clsx";
import dayjs from "dayjs";

/* ---- badge color helper -------------------------------------------- */
const levelColor = (lvl) =>
    clsx(
        "px-2 py-0.5 rounded text-xs font-semibold",
        {
            INFO: "bg-sky-100  text-sky-700",
            WARN: "bg-amber-100 text-amber-700",
            CRITICAL: "bg-rose-100 text-rose-700",
        }[lvl] ?? "bg-gray-100 text-gray-600",
    );

export default function AlertsPage() {
    const LIMIT = 100;

    /* ---------- Apollo hooks ------------------------------------------ */
    const client = useApolloClient();

    const {data, loading, error} = useQuery(ALERT_HISTORY_QUERY, {
        variables: {limit: LIMIT},
        fetchPolicy: "network-only",
    });

    const [ackAlert] = useMutation(ACK_ALERT_MUTATION, {refetchQueries: [ALERT_HISTORY_QUERY]});

    /* ---------- live push (subscription) ------------------------------ */
    useSubscription(ALERT_FIRED_SUB, {
        onData: ({client, data: subData}) => {
            const newAlert = subData.data.alertTriggered;
            client.cache.updateQuery(
                {query: ALERT_HISTORY_QUERY, variables: {limit: LIMIT}},
                (prev) => {
                    const prevRows = prev?.alertLogs ?? [];
                    return {alertLogs: [newAlert, ...prevRows].slice(0, LIMIT)};
                },
            );
        },
    });

    /* ---------- ACK handler ------------------------------------------- */
    const handleAck = async (id) => {
        /* 1) server-side */
        await ackAlert({variables: {id}});

        /* 2) client cache – we remove the row */
        client.cache.updateQuery(
            {query: ALERT_HISTORY_QUERY, variables: {limit: LIMIT}},
            (prev) => ({
                alertLogs: (prev?.alertLogs ?? []).filter((r) => r.id !== id),
            }),
        );
    };

    /* ---------- guards ------------------------------------------------- */
    if (loading) return <StatusState type="loading" text="Loading alerts …"/>;
    if (error) return <StatusState type="error" text="Server unavailable"/>;
    const rows = data?.alertLogs ?? [];
    if (!rows.length)
        return <StatusState type="empty" text="No alerts"/>;

    /* ---------- UI ----------------------------------------------------- */
    return (
        <div className="p-6 max-w-6xl mx-auto">
            <h1 className="text-2xl font-semibold mb-6">Alert History</h1>

            <div className="overflow-x-auto shadow rounded-lg ring-1 ring-gray-200">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                    <thead className="bg-gray-50 text-left font-semibold text-gray-600">
                    <tr>
                        <th className="px-4 py-3">Sensor</th>
                        <th className="px-4 py-3">Field</th>
                        <th className="px-4 py-3">Level</th>
                        <th className="px-4 py-3">Message</th>
                        <th className="px-4 py-3">Ack</th>
                        <th className="px-4 py-3">Time</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-100">
                    {rows.map((a) => (
                        <tr key={a.id} className="hover:bg-gray-50">
                            <td className="px-4 py-2 font-medium">{a.sensorId}</td>
                            <td className="px-4 py-2 capitalize">{a.field}</td>

                            {/* level badge */}
                            <td className="px-4 py-2">
                                <span className={levelColor(a.level)}>{a.level}</span>
                            </td>

                            <td className="px-4 py-2 whitespace-pre-line">{a.message}</td>

                            {/* Ack column */}
                            <td className="px-4 py-2">
                                {a.ack ? (
                                    <span className="text-emerald-600 font-semibold">
                      ✓&nbsp;{dayjs(a.ackTime).format("HH:mm:ss")}
                    </span>
                                ) : (
                                    <button
                                        title="Acknowledge"
                                        onClick={() => handleAck(a.id)}
                                        className="text-rose-600 hover:text-rose-800 font-extrabold text-3xl leading-none"
                                    >
                                        •
                                    </button>
                                )}
                            </td>

                            <td className="px-4 py-2">
                                {dayjs(a.created).format("YYYY-MM-DD HH:mm:ss")}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
