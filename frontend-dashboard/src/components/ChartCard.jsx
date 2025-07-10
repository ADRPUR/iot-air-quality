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
                                      range,
                                      visibleSensors = new Set()
                                  }) {
    if (!visibleSensors.has(sensorId)) return null;
    /* 1. do we have a “from” instant? (always, after Apply) */
    const hasFrom = range?.fromMs != null;

    /* 2. build variables → include `to` ONLY for custom-range */
    const variables = hasFrom
        ? {
            sensor: sensorId,
            field,
            from: new Date(range.fromMs).toISOString(),
            ...(range?.toMs && {       // add `to` if it exists
                to: new Date(range.toMs).toISOString(),
            }),
        }
        : null;

    /* 3. start polling every 5s once we have a `from` time */
    const poll = hasFrom ? 5_000 : 0;

    const {data, loading, error} = useQuery(METRICS_RANGE, {
        variables,
        pollInterval: poll,
        fetchPolicy: "no-cache",
        skip: !hasFrom,
    });

    if (!hasFrom) return null;
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