/***********************************************************************
 *  /src/api/alert.gql.js
 *  The collection of queries / mutations / subscriptions for the Alert module
 **********************************************************************/
import { gql } from "@apollo/client";

/* ──────────────────────────── HISTORY (query) ──────────────────────────

------------------------------------------------------------------------ */
export const ALERT_HISTORY_QUERY = gql`
    query AlertLogs(
        $sensorId: String
        $field:    String
        $ack:      Boolean = false
        $limit:    Int = 100
    ) {
        alertLogs(
            sensorId: $sensorId
            field:    $field
            ack:      $ack
            limit:    $limit
        ) {
            id
            created          # Timestamp!
            sensorId
            field
            ruleCode
            level            # INFO / WARNING / CRITICAL
            message
            ack
            ackTime
        }
    }
`;

/* ────────────────────────── LIVE ALERTS (subscription) ──────────────── */
export const ALERT_FIRED_SUB = gql`
    subscription OnAlertTriggered($sensorId: String, $field: String) {
        alertTriggered(sensorId: $sensorId, field: $field) {
            id
            created
            sensorId
            field
            ruleCode
            level
            message
        }
    }
`;

/* ───────────────────────────── RULES (query) ─────────────────────────── */
export const ALERT_RULES_QUERY = gql`
    query AlertRules($sensorId: String, $field: String, $enabled: Boolean) {
        alertRules(sensorId: $sensorId, field: $field, enabled: $enabled) {
            id
            sensorId
            field
            op           # LT / LTE / EQ / GTE / GT / CHANGE
            level
            threshold
            enabled
            created
            updated
        }
    }
`;

/* ───────────────────────── UPDATE RULE (mutation) ───────────────────── */
export const UPDATE_RULE_MUTATION = gql`
    mutation UpdateRule(
        $id:        UUID!
        $op:        Operator
        $level:     AlertLevel
        $threshold: Float
        $enabled:   Boolean
    ) {
        updateRule(
            id:        $id
            op:        $op
            level:     $level
            threshold: $threshold
            enabled:   $enabled
        ) {
            id
            sensorId
            field
            op
            level
            threshold
            enabled
            updated
        }
    }
`;

/* ───────────────────────── CREATE RULE (mutation) ───────────────────── */
export const CREATE_RULE_MUTATION = gql`
    mutation CreateRule(
        $sensorId:  String!
        $field:     String!
        $op:        Operator!
        $level:     AlertLevel!
        $threshold: Float!
        $enabled:   Boolean = true
    ) {
        createRule(
            sensorId:  $sensorId
            field:     $field
            op:        $op
            level:     $level
            threshold: $threshold
            enabled:   $enabled
        ) {
            id
            sensorId
            field
            op
            level
            threshold
            enabled
            created
        }
    }
`;

/* ───────────────────────── DELETE RULE (mutation) ───────────────────── */
export const DELETE_RULE_MUTATION = gql`
    mutation DeleteRule($id: UUID!) {
        deleteRule(id: $id)
    }
`;

/* ─────────────────────── ACKNOWLEDGE ALERT (mutation) ───────────────── */
export const ACK_ALERT_MUTATION = gql`
    mutation AckAlert($id: ID!) {
        acknowledgeAlert(id: $id) {
            id
            ack
            ackTime
        }
    }
`;
