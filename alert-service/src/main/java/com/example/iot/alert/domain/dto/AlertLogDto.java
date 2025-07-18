package com.example.iot.alert.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * DTO sent/received on the Alert Service API.
 * Used for REST/GraphQL & MapStruct.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)          // don't send unnecessary nulls
public class AlertLogDto {

    Long id;                    // PK (maybe missing at creation)
    Instant created;

    String sensorId;
    String field;

    String ruleCode;            // rule identifier
    AlertLevel level;           // enum INFO|WARN|CRITICAL

    String message;             // “Temp > 30 °C on dht22_1”

    Boolean ack;                // true if the operator confirmed
    Instant ackTime;            // when was it confirmed
}

