package com.example.iot.alert;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "alert.thresholds")
public record ThresholdCfg(Map<String, Double> map) {}
