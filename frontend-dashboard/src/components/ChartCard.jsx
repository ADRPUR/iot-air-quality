import { useQuery, gql } from "@apollo/client";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
} from "recharts";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
dayjs.extend(utc);

// GraphQL query — filter by sensorId + field
const METRICS = gql`
    query LatestBySensor($sensor: String!, $field: String!, $limit: Int!) {
        latestMetricsBySensor(sensorId: $sensor, field: $field, limit: $limit) {
            time
            value
        }
    }
`;

export default function ChartCard({ sensorId, field, color = "#8884d8" }) {
    const { data, loading, error } = useQuery(METRICS, {
        variables: { sensor: sensorId, field, limit: 120 },
        pollInterval: 5000,
        errorPolicy: "all",
    });

    if (loading) return <p className="text-gray-500">Loading {field}…</p>;
    if (error)
        return (
            <div className="p-4 border rounded text-red-600">
                {field}: server unavailable
            </div>
        );

    // ISO-string ➜ epoch ms (pentru X-axis time scale)
    const points = data.latestMetricsBySensor
        .map((m) => ({
            value: m.value,
            timeMs: new Date(m.time).getTime(),
        }))
        .reverse();

    return (
        <div className="card p-4 shadow w-full">
            <h2 className="text-lg font-semibold mb-2 capitalize">
                {sensorId} – {field}
            </h2>

            <ResponsiveContainer width="100%" height={200}>
                <LineChart data={points}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis
                        dataKey="timeMs"
                        type="number"
                        scale="time"
                        domain={["auto", "auto"]}
                        tickFormatter={(ts) => dayjs(ts).format("HH:mm")}
                        minTickGap={30}
                    />
                    <YAxis />
                    <Tooltip
                        labelFormatter={(ts) =>
                            dayjs(ts).format("YYYY-MM-DD HH:mm:ss")
                        }
                    />
                    <Line
                        type="monotone"
                        dataKey="value"
                        stroke={color}
                        dot={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}
