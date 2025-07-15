/* ------------------------------------------------------
   Card for realtime dashboard: shows latest value of a sensor field
   Props:
     v: { sensorId, field, value, ts }   // required
     highlight?: boolean                 // pulse + ring accent on update
------------------------------------------------------ */
import React from "react";
import { motion } from "framer-motion";
import { Thermometer, Droplets, Gauge, Activity, HelpCircle, Sun } from "lucide-react";
import clsx from "clsx";

/* field → icon + ring color */
const fieldMeta = {
    temperature: { Icon: Thermometer, ring: "ring-red-400" },
    humidity:    { Icon: Droplets,    ring: "ring-sky-400" },
    pressure:    { Icon: Gauge,       ring: "ring-gray-400" },

    /* particulate matter */
    pm1:  { Icon: Sun, ring: "ring-green-400" },
    pm25: { Icon: Sun, ring: "ring-green-400" },
    pm10: { Icon: Sun, ring: "ring-green-400" },

    /* gases / index */
    air_quality: { Icon: Activity, ring: "ring-amber-400" },
};

function formatField(f) {
    return f.replace(/_/g, " ").replace(/\b\w/g, l => l.toUpperCase());
}

function unitFor(f) {
    switch (f) {
        case "humidity":     return "%";
        case "temperature":  return "°C";
        case "pressure":  return "mmHg";
        case "pm1":
        case "pm25":
        case "pm10":         return "μg/m³";
        case "air_quality":  return "ppm";
        default:              return "";
    }
}

export default function SensorCard({ v, highlight = false }) {
    const meta = fieldMeta[v.field] ?? { Icon: HelpCircle, ring: "ring-amber-400" };
    const unit = unitFor(v.field);

    return (
        <motion.div
            key={v.ts}
            initial={{ scale: 1 }}
            animate={highlight ? { scale: [1, 1.08, 1] } : { scale: 1 }}
            transition={{ duration: 0.8 }}
            className={clsx(
                "w-45 md:w-56 rounded-xl shadow bg-white p-4 flex flex-col ring-1 ring-transparent",
                highlight && meta.ring
            )}
        >
            <div className="flex items-center justify-between mb-2">
                <span className="text-sm text-gray-500">{v.sensorId}</span>
                {meta.Icon ? <meta.Icon className="w-5 h-5 text-gray-400" /> : null}
            </div>

            <span className="text-lg font-semibold capitalize">
        {formatField(v.field)}
      </span>

            <div className="flex-1 flex items-end gap-1 mt-1">
                <span className="text-3xl font-bold leading-none">{v.value}</span>
                {unit && <span className="text-2xl leading-none">{unit}</span>}
            </div>

            <span className="text-xs text-gray-400 mt-2 self-end">
        {new Date(v.ts).toLocaleTimeString()}
      </span>
        </motion.div>
    );
}
