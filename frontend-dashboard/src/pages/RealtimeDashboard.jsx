/* ------------------------------------------------------
   pages/RealtimeDashboard.jsx – live numeric read‑outs
------------------------------------------------------ */
import StatusState      from "@/components/common/StatusState.jsx";
import SensorCard       from "@/components/dashboard/SensorCard.jsx";
import { useLiveSensorMap } from "@/hooks/useLiveSensorMap.js";

export default function RealtimeDashboard() {
    const { values, loading, error, now } = useLiveSensorMap();

    /* UI states */
    if (loading && values.length === 0)
        return <StatusState type="loading" text="Loading sensors…" />;
    if (error)
        return <StatusState type="error" text="Server unavailable" />;
    if (values.length === 0)
        return <StatusState type="empty" text="No live data" />;

    /* helper: pulse if updated < 2 s */
    const isRecent = (ts) => now - new Date(ts).getTime() < 2000;

    return (
        <div className="p-6 max-w-5xl mx-auto">
            <h1 className="text-2xl font-semibold text-center">Live Sensors</h1>

            <div className="grid gap-x-2 gap-y-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 justify-items-center mx-auto">
                {values
                    .sort((a, b) => a.sensorId.localeCompare(b.sensorId))
                    .map((v) => (
                        <SensorCard
                            key={`${v.sensorId}-${v.field}`}
                            v={v}
                            highlight={isRecent(v.ts)}
                        />
                    ))}
            </div>
        </div>
    );
}
