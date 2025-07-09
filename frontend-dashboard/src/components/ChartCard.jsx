import {useQuery, gql} from "@apollo/client";
import {
    ResponsiveContainer, LineChart, Line,
    XAxis, YAxis, CartesianGrid, Tooltip,
} from "recharts";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

/* ——— GraphQL (one sensor + field) ——————————————— */
const METRICS_RANGE = gql`
    query MetricsRange(
        $sensor: String!
        $field:  String!
        $from:   String!
        $to:     String
    ) {
        metricsInRange(
            sensorId: $sensor
            field:    $field
            from:     $from
            to:       $to
        ) {
            time value
        }
    }
`;

export default function ChartCard({
                                      sensorId,
                                      field,
                                      color,
                                      range
                                  }) {
    const hasRange = !!range?.fromMs && !!range?.toMs;
    const variables = hasRange
        ? {
            sensor: sensorId,
            field,
            from: new Date(range.fromMs).toISOString(),
            to: new Date(range.toMs).toISOString(),
        }
        : {};

    const {data, loading, error} = useQuery(METRICS_RANGE, {
        variables,
        pollInterval: 5_000,
        fetchPolicy: "no-cache",
        skip: !hasRange,
    });

    if (!hasRange) return null;
    if (loading) return <p>Loading {field} ...</p>;
    if (error) return <div className="p-4 border rounded text-red-600">
        {field}: server unavailable
    </div>

    const points = data.metricsInRange.map(p => ({
        timeMs: new Date(p.time).getTime(),
        value: p.value,
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
                        minTickGap={35}
                    />
                    <YAxis/>
                    <Tooltip labelFormatter={ts =>
                        dayjs(ts).format("YYYY-MM-DD HH:mm:ss")}/>

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