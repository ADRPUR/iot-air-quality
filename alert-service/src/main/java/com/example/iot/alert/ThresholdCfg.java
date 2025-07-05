package com.example.iot.alert;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "alert")
public class ThresholdCfg {

    /** YAML: alert.thresholds.temperature=30 */
    private Map<String, Double> thresholds = Collections.emptyMap();

    public Map<String, Double> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Double> thresholds) {
        this.thresholds = thresholds == null ? Collections.emptyMap() : thresholds;
    }
}
