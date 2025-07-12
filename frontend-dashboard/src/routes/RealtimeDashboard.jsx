import { gql, useQuery, useSubscription } from "@apollo/client";
import { useEffect, useState, useMemo } from "react";
import { motion } from "framer-motion";
import { Thermometer, Droplets, Cloud, Sun } from "lucide-react";

/* ───────────── GraphQL ───────────── */
const SNAPSHOT = gql`
    query { currentValues { sensorId field value ts } }
`;

const LIVE = gql`
    subscription { sensorValueUpdated { sensorId field value ts } }
`;

/* ───────────── Helpers ───────────── */
const keyOf = v => `${v.sensorId}-${v.field}`;

const fieldMeta = {
    temperature: { icon: Thermometer, ring: "ring-red-400", unit: "°C" },
    humidity:    { icon: Droplets,    ring: "ring-sky-400", unit: "%"  },
    pressure:    { icon: Cloud,       ring: "ring-gray-400", unit: "hPa" },
    light:       { icon: Sun,         ring: "ring-amber-400", unit: "lux" },
};

function formatField(f) {
    return f.replace(/_/g, " ").replace(/\b\w/g, l => l.toUpperCase());
}

/* ───────────── Page ───────────── */
export default function RealtimeDashboard() {
    const { data }  = useQuery(SNAPSHOT, { fetchPolicy: "network-only" });
    const [map, setMap] = useState(new Map());

    /* populate snapshot */
    useEffect(() => {
        if (data?.currentValues) {
            setMap(new Map(data.currentValues.map(v => [keyOf(v), v])));
        }
    }, [data]);

    /* live updates */
    useSubscription(LIVE, {
        onData: ({ data: { data } }) => {
            const v = data.sensorValueUpdated;
            setMap(m => new Map(m).set(keyOf(v), v));
        },
    });

    const values = useMemo(() =>
            Array.from(map.values()).sort((a, b) => a.sensorId.localeCompare(b.sensorId))
        , [map]);

    return (
        <div className="p-6">
            <h1 className="text-2xl font-semibold text-center mb-6">Live Sensors</h1>

            {/* centered grid */}
            <div className="grid gap-x-4 gap-y-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 justify-items-center mx-auto">
                {values.map(v => (
                    <SensorCard key={keyOf(v)} v={v} />
                ))}
            </div>
        </div>
    );
}

/* ───────────── Card ───────────── */
function SensorCard({ v }) {
    const meta = fieldMeta[v.field] ?? { icon: Sun, ring: "ring-amber-300", unit: "" };
    const Icon = meta.icon;
    const unit = meta.unit;

    /* animation: pulse & scale on every new ts */
    return (
        <motion.div
            key={v.ts}                    /* re‑mount on update → trigger animation */
            initial={{ scale: 0.94, opacity: 0.8 }}
            animate={{ scale: [0.94, 1.06, 1], opacity: 1 }}
            transition={{ duration: 0.8, ease: "easeInOut" }}
            className={`w-64 md:w-72 rounded-xl shadow bg-white p-4 flex flex-col ring-4 ring-offset-0 ${meta.ring}`}
        >
            {/* sensor id */}
            <span className="text-xs text-gray-500 mb-1">{v.sensorId}</span>

            {/* title & icon */}
            <div className="flex items-center gap-1 text-lg font-semibold capitalize">
                <Icon className="w-4 h-4" />
                {formatField(v.field)}
            </div>

            {/* value + unit */}
            <div className="flex items-end justify-start mt-2">
                <span className="text-4xl font-bold leading-none">{v.value}</span>
                {unit && <span className="text-3xl font-semibold ml-1 leading-none">{unit}</span>}
            </div>

            {/* timestamp */}
            <span className="text-[10px] text-gray-400 mt-2">
        {new Date(v.ts).toLocaleTimeString()}
      </span>
        </motion.div>
    );
}
