import { useEffect, useState } from "react";
import { useQuery, useSubscription } from "@apollo/client";
import { CURRENT_VALUES, SENSOR_VALUE_UPDATED } from "@/api/ingest.gql.js";

/** Live map: latest value per (sensorId, field) + lastKey for highlight */
export function useLiveSensorMap() {
    const { data, loading, error } = useQuery(CURRENT_VALUES, {
        fetchPolicy: "network-only",
    });

    const [map, setMap] = useState(new Map());
    const [now, setNow] = useState(Date.now());

    /* snapshot → init map */
    useEffect(() => {
        if (data?.currentValues) {
            setMap(new Map(data.currentValues.map(v => [key(v), v])));
        }
    }, [data]);

    /* live updates */
    useSubscription(SENSOR_VALUE_UPDATED, {
        onData: ({ data: { data } }) => {
            const v = data.sensorValueUpdated;
            setNow(Date.now());
            setMap((m) => new Map(m).set(key(v), v));
        },
    });

    return { values: Array.from(map.values()), loading, error, now };
}

function key(v) {
    return `${v.sensorId}-${v.field}`;
}
