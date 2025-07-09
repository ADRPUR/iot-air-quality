// src/routes/Dashboard.jsx
import { useQuery, gql } from "@apollo/client";
import { useState, useMemo } from "react";
import ChartCard         from "../components/ChartCard";
import DashboardControls from "../components/DashboardControls";

const CHARTS = [
    // DHT22
    { sensor: "dht22_1", field: "temperature", color: "#f97316" },
    { sensor: "dht22_1", field: "humidity",    color: "#0ea5e9" },

    // BME280
    { sensor: "bme280_1", field: "temperature", color: "#84cc16" },
    { sensor: "bme280_1", field: "humidity",    color: "#22c55e" },
    { sensor: "bme280_1", field: "pressure",    color: "#a855f7" },

    // MQ135
    { sensor: "mq135_1",  field: "air_quality", color: "#ef4444" },

    // PMS5003
    { sensor: "pms5003_1", field: "pm1",  color: "#BA487F" },
    { sensor: "pms5003_1", field: "pm25", color: "#FF9587" },
    { sensor: "pms5003_1", field: "pm10", color: "#03A6A1" },
];

const GET_SENSORS = gql`
    query { sensors { sensorId visible } }
`;

export default function Dashboard() {
    const now = Date.now();
    const [range, setRange] = useState({
        fromMs: now - 3_600_000,
        toMs  : now,
    });

    const { data } = useQuery(GET_SENSORS, { pollInterval: 10_000 });
    const visibleSensors = useMemo(() => {
        if (!data) return new Set();        // încă nu avem date
        return new Set(
            data.sensors.filter(s => s.visible).map(s => s.sensorId)
        );
    }, [data]);

    const chartsToShow = CHARTS.filter(c =>
        visibleSensors.size === 0 || visibleSensors.has(c.sensor)
    );

    return (
        <>
            <DashboardControls onChange={setRange} />

            <div className="p-6 grid gap-6 lg:grid-cols-3 auto-rows-[minmax(0,_1fr)]">
                {chartsToShow.map(c => (
                    <ChartCard
                        key={`${c.sensor}-${c.field}`}
                        sensorId={c.sensor}
                        field={c.field}
                        color={c.color}
                        range={range}
                    />
                ))}
            </div>
        </>
    );
}
