#!/usr/bin/env python3
"""
Fake sensor publisher for the IoT Air-Quality PoC.

Topics:
  sensors/living/<sensor_id>

Payload:
  {
    "sensor": "dht22_1",
    "field":  "temperature" | "humidity" | "pressure"
              | "air_quality" | "pm1" | "pm25" | "pm10",
    "value":  <float>,
    "ts":     "<ISO-8601 UTC>"
  }
"""

import json
import random
import time
from datetime import datetime, timezone
from paho.mqtt import publish

# ───────────────────  CONFIG  ───────────────────
BROKER = "localhost"                 # or "mosquitto" inside Docker net
PORT   = 1883
MQTT_USER = "iotuser"
MQTT_PASS = "supersecret"
DELAY  = 5                           # seconds between publish rounds
ROOM   = "living"
# ────────────────────────────────────────────────

def now_iso() -> str:
    return datetime.now(timezone.utc).isoformat(timespec="microseconds")

def send(sensor: str, field: str, value: float):
    payload = {
        "sensor": sensor,
        "field":  field,
        "value":  round(value, 2),
        "ts":     now_iso(),
    }
    publish.single(
        topic=f"sensors/{ROOM}/{sensor}",
        payload=json.dumps(payload),
        hostname=BROKER,
        port=PORT,
        qos=1,
        auth={"username": MQTT_USER, "password": MQTT_PASS},
    )
    print("→", payload)

while True:
    # DHT22
    send("dht22_1", "temperature", 20 + random.uniform(-2, 2))
    send("dht22_1", "humidity",    50 + random.uniform(-10, 10))

    # BME280
    send("bme280_1", "temperature", 20 + random.uniform(-1.5, 1.5))
    send("bme280_1", "humidity",    45 + random.uniform(-8, 8))
    send("bme280_1", "pressure",    1013 + random.uniform(-5, 5))   # hPa

    # MQ135 (air-quality index proxy)
    send("mq135_1", "air_quality", 100 + random.uniform(-20, 20))   # ppm-equiv CO₂

    # PMS5003 (particulate matter)
    send("pms5003_1", "pm1",  random.uniform(0, 10))
    send("pms5003_1", "pm25", random.uniform(5, 35))
    send("pms5003_1", "pm10", random.uniform(10, 50))

    time.sleep(DELAY)
