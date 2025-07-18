/* ------------------------------------------------------
   components/charts/ChartCard.jsx
   5-minute avg graph per (sensor, field)
------------------------------------------------------ */
import { useMemo } from "react";
import { useQuery, useSubscription } from "@apollo/client";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
} from "recharts";
import dayjs from "dayjs";
import utc   from "dayjs/plugin/utc";

import StatusState from "@/components/common/StatusState.jsx";
import { AVG_RANGE, AVG_UPDATED } from "@/api/ingest.gql.js";
import { rangeSpanMs } from "@/utils/range.js";

dayjs.extend(utc);

export default function ChartCard({
                                      sensorId,
                                      field,
                                      range,                 // { fromMs, toMs }
                                      color = "#0ea5e9",
                                      limit = 500,
                                  }) {
    /* ------------------------------------------------------------------ */
    /* 1. Calculate the variables for the query (from/to re-evaluate on refetch) */
    /* ------------------------------------------------------------------ */
    const variables = useMemo(() => {
        if (!range?.fromMs) return null;                 // încă nu avem interval
        const span   = rangeSpanMs(range);
        const now    = Date.now();
        const toISO  = new Date(now).toISOString();
        const fromISO = new Date(now - span).toISOString();

        return {
            sensorId,
            field,
            from:  fromISO,
            to:    range.toMs ? new Date(range.toMs).toISOString() : toISO,
            limit,
        };
    }, [sensorId, field, range?.fromMs, range?.toMs, limit]);

    /* ------------------------------------------------------------------ */
    /* 2. Query + subscription for refresh at each recalculation          */
    /* ------------------------------------------------------------------ */
    const {
        data,
        loading,
        error,
        refetch,
    } = useQuery(AVG_RANGE, {
        variables,
        skip: variables == null,
        fetchPolicy: "no-cache",
    });

    useSubscription(AVG_UPDATED, {
        variables: { sensorId, field },
        onData: ({ data: { data } }) => {
            // when receive the ping, recalculate the current interval again
            // (span = rangeSpanMs) and rerun the query
            if (data?.avg5mUpdated) {
                const span    = rangeSpanMs(range);
                const toISO   = new Date().toISOString();
                const fromISO = new Date(Date.now() - span).toISOString();
                refetch({
                    ...variables,
                    from: fromISO,
                    to:   toISO,
                }).catch(() => {});
            }
        },
    });

    /* ------------------------------------------------------------------ */
    /* 3. Loading/Error States                                 */
    /* ------------------------------------------------------------------ */
    if (variables == null) {
        return <StatusState type="info" text="Selectați un interval" />;
    }

    if (loading && !data) {
        return <StatusState type="loading" text={`${field}: loading…`} />;
    }

    if (error) {
        return <StatusState type="error" text={`${field}: server error`} />;
    }

    /* ------------------------------------------------------------------ */
    /* 4. If the sensor is marked as invisible → we do not render the card           */
    /* ------------------------------------------------------------------ */
    // In any MetricAvg response we have the sensor. We use the first point.
    const first = data.metricsAvg5m?.[0];
    const isVisible = first?.sensor?.visible ?? true;
    if (!isVisible) return null;

    /* ------------------------------------------------------------------ */
    /* 5. Prepare the data and render the graph                              */
    /* ------------------------------------------------------------------ */
    const points = [...data.metricsAvg5m]
        .reverse()
        .map(p => ({
            timeMs: new Date(p.bucket).getTime(),
            value:  p.avgVal,
        }));

    return (
        <div className="rounded-xl shadow bg-white p-4 w-full">
            <h2 className="text-lg font-semibold mb-2 capitalize">
                {sensorId} – {field}
            </h2>

            <ResponsiveContainer width="100%" height={220}>
                <LineChart data={points}>
                    <CartesianGrid strokeDasharray="3 3" />

                    <XAxis
                        dataKey="timeMs"
                        type="number"
                        scale="time"
                        domain={["auto", "auto"]}
                        interval={1}
                        minTickGap={4}
                        tickFormatter={ts => dayjs(ts).format("HH:mm")}
                    />

                    <YAxis />
                    <Tooltip
                        labelFormatter={ts => dayjs(ts).format("YYYY-MM-DD HH:mm:ss")}
                    />

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
