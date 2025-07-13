import {useMemo} from "react";
import {useQuery, gql, useSubscription} from "@apollo/client";
import {
    ResponsiveContainer, LineChart, Line,
    XAxis, YAxis, CartesianGrid, Tooltip,
} from "recharts";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {rangeSpanMs} from "../utils/range.js";

dayjs.extend(utc);

/* ================= GraphQL ================= */
const AVG_5M = gql`
    query AvgRange(
        $sensor: String!
        $field:  String!
        $from:   String!
        $to:     String
        $limit:  Int = 500
    ) {
        metricsAvg5m(
            sensorId: $sensor
            field:    $field
            from:     $from
            to:       $to
            limit:    $limit
        ) {
            bucket
            avgVal
        }
    }
`;

const AVG_UPDATED = gql`
    subscription {
        avg5mUpdated { sensorId field }
    }
`;


export default function ChartCard({
                                      sensorId,
                                      field,
                                      color,
                                      range,
                                      visibleSensors = new Set()
                                  }) {
    const hasFrom = range?.fromMs != null;

    const variables = useMemo(() => {
        if (!hasFrom) return {};          // placeholder
        const vars = {
            sensor: sensorId,
            field,
            from: new Date(range.fromMs).toISOString(),
        };
        if (range?.toMs) {
            vars.to = new Date(range.toMs).toISOString();
        }
        return vars;
    }, [hasFrom, sensorId, field, range?.fromMs, range?.toMs]);

    const {data, loading, error, refetch} = useQuery(AVG_5M, {
        variables,
        skip: !hasFrom,
        fetchPolicy: "no-cache",
    });

    useSubscription(AVG_UPDATED, {
        onData: ({data: {data}}) => {
            const u = data?.avg5mUpdated;
            if (!u || u.sensorId !== sensorId || u.field !== field) return;

            const span = rangeSpanMs(range);          // 1h / 6h / 24h / custom
            const toISO = new Date().toISOString();
            const fromISO = new Date(Date.now() - span).toISOString();

            refetch({sensor: sensorId, field, from: fromISO, to: toISO})
                .catch(() => {
                });
        },
    });


    if (!visibleSensors.has(sensorId) || !hasFrom) return null;

    if (loading) return <p>Loading {field} ...</p>;
    if (error) return <div className="p-4 border rounded text-red-600">
        {field}: server unavailable
    </div>;

    const points = [...data.metricsAvg5m].reverse().map(p => ({
        timeMs: new Date(p.bucket).getTime(),
        value: p.avgVal,
    }));

    return (
        <div className="card p-4 shadow w-full">
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
