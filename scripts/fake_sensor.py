#!/usr/bin/env python3
"""
Fake sensor publisher for demo purposes.

Publishes temperature, humidity and PM2.5 messages to MQTT every 2 s.

Topic format:  sensors/<room>/<sensor>
Payload JSON:  {
    "sensor": "dht22_1",
    "field":  "temperature" | "humidity" | "pm25",
    "value":  float,
    "ts":     ISO-8601 timestamp
}
"""

import json
import random
import time
from datetime import datetime, timezone

from faker import Faker
from paho.mqtt import publish

fake = Faker()

BROKER = "localhost"
TOPIC  = "sensors/living/dht22"
DELAY  = 5           # seconds between *each* message

def send(field: str, value: float) -> None:
    """Compose payload and publish to MQTT."""
    payload = {
        "sensor": "dht22_1",
        "field":  field,
        "value":  round(value, 2),
        "ts":     datetime.now(timezone.utc).isoformat()
    }
    publish.single(
        topic=TOPIC,
        payload=json.dumps(payload),
        qos=1,
        hostname=BROKER
    )
    print("→ sent", payload)

while True:
    # Temperature 18–22 °C
    send("temperature", 20 + random.uniform(-2, 2))

    # Humidity 40–60 %
    send("humidity", 50 + random.uniform(-10, 10))

    # PM2.5 5–30 µg/m³
    send("pm25", random.uniform(5, 30))

    time.sleep(DELAY)
