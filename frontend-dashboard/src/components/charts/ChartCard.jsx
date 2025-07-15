/* ------------------------------------------------------
   Line chart (Recharts) that shows 5‑minute averages.
   Props:
     sensorId: string
     field:    string
     color:    Tailwind color hex/# or class‑based
     range:    { fromMs:number, toMs?:number }
     visibleSensors: Set<string> (filter)
------------------------------------------------------ */
import {useMemo} from "react";
import {useQuery, useSubscription} from "@apollo/client";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
} from "recharts";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {AVG_RANGE, AVG_UPDATED} from "@/api/ingest.gql.js";
import StatusState from "@/components/common/StatusState.jsx";
import {rangeSpanMs} from "@/utils/range.js";

dayjs.extend(utc);

export default function ChartCard({
                                      sensorId,
                                      field,
                                      color = "#0ea5e9",
                                      range,
                                      visibleSensors = new Set(),
                                  }) {
    /* open‑ended range vars (refetch refreshes to) */
    const hasFrom = range?.fromMs != null;

    const variables = useMemo(() => {
        if (!hasFrom) return {};
        return {
            sensor: sensorId,
            field,
            from: new Date(range.fromMs).toISOString(),
            to: range.toMs ? new Date(range.toMs).toISOString() : undefined,
            limit: 500,
        };
    }, [hasFrom, sensorId, field, range?.fromMs, range?.toMs]);

    const {data, loading, error, refetch} = useQuery(AVG_RANGE, {
        variables,
        skip: !hasFrom,
        fetchPolicy: "no-cache",
    });


    /* live refresh when the aggregate is recalculated on the server */
    useSubscription(AVG_UPDATED, {
        onData: ({data: {data}}) => {
            const u = data?.avg5mUpdated;
            if (u?.sensorId === sensorId && u?.field === field) {
                const span = rangeSpanMs(range);
                const toISO = new Date().toISOString();
                const fromISO = new Date(Date.now() - span).toISOString();
                refetch({...variables, from: fromISO, to: toISO}).catch(() => {
                });
            }
        },
    });

    /* visibility filtering */
    if (!visibleSensors.has(sensorId) || !hasFrom) return null;

    if (loading) return <StatusState type="loading" text={`Loading ${field}…`}/>;
    if (error) return <StatusState type="error" text={`${field}: server unavailable`}/>;

    const points = [...data.metricsAvg5m].reverse().map(p => ({
        timeMs: new Date(p.bucket).getTime(),
        value: p.avgVal,
    }));

    return (
        <div className="rounded-xl shadow bg-white p-4 w-full">
            <h2 className="text-lg font-semibold mb-2 capitalize">
                {sensorId} – {field}
            </h2>

            <ResponsiveContainer width="100%" height={220}>
                <LineChart data={points}>
                    <CartesianGrid strokeDasharray="3 3"/>

                    <XAxis
                        dataKey="timeMs"
                        type="number"
                        scale="time"
                        domain={["auto", "auto"]}
                        interval={1}
                        minTickGap={4}
                        tickFormatter={ts => dayjs(ts).format("HH:mm")}
                    />

                    <YAxis/>
                    <Tooltip
                        labelFormatter={ts => dayjs(ts).format("YYYY-MM-DD HH:mm:ss")}
                    />

                    <Line
                        type="monotone"
                        dataKey="value"
                        stroke={color}
                        dot={false}
                        isAnimationActive={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}
