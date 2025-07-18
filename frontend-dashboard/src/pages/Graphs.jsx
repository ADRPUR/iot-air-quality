/* ------------------------------------------------------
   pages/Graphs.jsx – historical charts per sensor/field
------------------------------------------------------ */
import { useState } from "react";

import DashboardControls from "@/components/DashboardControls.jsx";
import ChartCard         from "@/components/charts/ChartCard.jsx";
import { presetToRange } from "@/utils/range.js";

/* ---- ce grafice afișăm ------------------------------- */
const CHARTS = [
    { sensor: "dht22_1",  field: "temperature", color: "#f97316" },
    { sensor: "dht22_1",  field: "humidity",    color: "#0ea5e9" },
    { sensor: "bme280_1", field: "temperature", color: "#84cc16" },
    { sensor: "bme280_1", field: "humidity",    color: "#22c55e" },
    { sensor: "bme280_1", field: "pressure",    color: "#a855f7" },
    { sensor: "mq135_1",  field: "air_quality", color: "#ef4444" },
    { sensor: "pms5003_1", field: "pm1",  color: "#BA487F" },
    { sensor: "pms5003_1", field: "pm25", color: "#FF9587" },
    { sensor: "pms5003_1", field: "pm10", color: "#03A6A1" },
];

export default function GraphsPage() {
    const [range, setRange] = useState(() => presetToRange("1h"));

    return (
        <>
            <DashboardControls
                onChange={(r) =>
                    setRange(r.preset ? presetToRange(r.preset, r) : r)
                }
            />

            <div className="p-6 grid gap-6 lg:grid-cols-3 auto-rows-[minmax(0,_1fr)]">
                {CHARTS.map((c) => (
                    <ChartCard
                        key={`${c.sensor}-${c.field}`}
                        sensorId={c.sensor}
                        field={c.field}
                        color={c.color}
                        range={range}   // { fromMs, toMs }
                    />
                ))}
            </div>
        </>
    );
}
