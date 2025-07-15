import { gql } from "@apollo/client";

    /*  ——— query: last N (implicit 50)  ——— */
        export const ALERT_HISTORY_QUERY = gql`
      query AlertHistory($limit: Int = 50) {
        alertHistory(limit: $limit) {
          id
          ts
          sensorId
          field
          value
          limit
          channels        # ex. ["EMAIL","SMS"]
        }
      }
    `;

    /*  ——— subscription: push immediately  ——— */
        export const ALERT_FIRED_SUB = gql`
      subscription OnAlertFired {
        alertFired {
          id
          ts
          sensorId
          field
          value
          limit
          channels
        }
      }
    `;

    /*  ——— rules (for the /rules page)  ——— */
        export const ALERT_RULES_QUERY = gql`
      query AlertRules {
        alertRules {
          id
          field
          maxVal
          enabled
        }
      }
    `;

    export const UPDATE_RULE_MUTATION = gql`
      mutation UpdateRule($id: ID!, $maxVal: Float!, $enabled: Boolean!) {
        updateRule(id: $id, maxVal: $maxVal, enabled: $enabled) {
          id
          maxVal
          enabled
        }
      }
    `;