import { gql } from "@apollo/client";

/* === LIVE dashboard ==================================================== */

/** Snapshot – last value for each (sensorId, field) */
export const CURRENT_VALUES = gql`
    query CurrentValues {
        currentValues {
            sensorId
            field
            value
            ts
        }
    }
`;

/** Subscription – push every new measure */
export const SENSOR_VALUE_UPDATED = gql`
    subscription OnSensorValueUpdated {
        sensorValueUpdated {
            sensorId
            field
            value
            ts
        }
    }
`;

/* === Graphs page (ChartCard) ========================================== */

/** Range of 5-minute buckets between from..to (max 500 points) */
export const AVG_RANGE = gql`
    query AvgRange(
        $sensor: String!
        $field: String!
        $from:  String!
        $to:    String
        $limit: Int = 500
    ) {
        metricsAvg5m(
            sensorId: $sensor
            field:    $field
            from:     $from
            to:       $to
            limit:    $limit
        ) {
            bucket
            avgVal
        }
    }
`;

/** Notification that the aggregate has been recalculated (1×/min) */
export const AVG_UPDATED = gql`
    subscription OnAvgUpdated {
        avg5mUpdated {
            sensorId
            field
        }
    }
`;
