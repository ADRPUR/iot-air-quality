import ChartCard from "../components/ChartCard";

const charts = [
    // DHT22
    { sensor: "dht22_1", field: "temperature", color: "#f97316" },
    { sensor: "dht22_1", field: "humidity",    color: "#0ea5e9" },

    // BME280
    { sensor: "bme280_1", field: "temperature", color: "#84cc16" },
    { sensor: "bme280_1", field: "humidity",    color: "#22c55e" },
    { sensor: "bme280_1", field: "pressure",    color: "#a855f7" },

    // MQ135
    { sensor: "mq135_1", field: "air_quality",  color: "#ef4444" },

    // PMS5003
    { sensor: "pms5003_1", field: "pm1",  color: "#60a5fa" },
    { sensor: "pms5003_1", field: "pm25", color: "#3b82f6" },
    { sensor: "pms5003_1", field: "pm10", color: "#2563eb" },
];

export default function Dashboard() {
    return (
        <div className="p-6 grid gap-6 lg:grid-cols-3 auto-rows-[minmax(0,_1fr)]">
            {charts.map((c) => (
                <ChartCard
                    key={`${c.sensor}-${c.field}`}
                    sensorId={c.sensor}
                    field={c.field}
                    color={c.color}
                />
            ))}
        </div>
    );
}
