# IoT Air‑Quality & Smart‑Home Ingest Platform

> **Monorepo** that demonstrates an end‑to‑end data flow: ESP32 sensors → MQTT → Spring Boot → TimescaleDB & Kafka → React dashboard.
>
> *All documentation below is written in **English**; code comments are in English, too.*

---

## Table of Contents

1. [High‑level Architecture](#high-level-architecture)
2. [Technology Stack](#technology-stack)
3. [Requirements](#requirements)
4. [Quick Start](#quick-start)
5. [Project Layout](#project-layout)
6. [Developer Workflow](#developer-workflow)
7. [Handy Commands](#handy-commands)

---

## High‑level Architecture

```
ESP32 (ESPHome) ── MQTT  ──► Mosquitto ▶ ingest‑service (Spring Boot)
                                     │
                                     ├─ TimescaleDB 17  ← hypertables
                                     └─ Kafka 7.6  → Streams / Dashboard
```

## Technology Stack

| Layer       | Technology                                |
| ----------- |-------------------------------------------|
| Firmware    | **ESPHome** (YAML)                        |
| Messaging   | **Mosquitto 2.x** (MQTT)                  |
| Ingestion   | **Spring Boot 3.4** • **Java 21**         |
| Persistence | **TimescaleDB 2.x** on **PostgreSQL 17**  |
| Streaming   | **Kafka 7.6** + **Kafka‑UI**              |
| Front‑end   | **React (Vite)** • Material‑UI • Recharts |

## Requirements

* Docker + Docker Compose v2
* JDK 21 (Temurin, Corretto, or Azul)
* Maven ≥ 3.9 (the repo ships with **mvnw** wrapper)

## Quick Start

```bash
# 1) Clone the repo
 git clone https://github.com/<your-user>/iot-air-quality.git
 cd iot-air-quality

# 2) Start infrastructure (MQTT + Kafka)
 docker compose -f infra/docker-compose.yml up -d

# 3) Run ingest‑service locally
 cd ingest-service
 ./mvnw spring-boot:run
```

> **TimescaleDB** is assumed to be installed locally and a database named
> **smarthome** already exists. See [`docs/DB_SETUP.md`](docs/DB_SETUP.md) for instructions.

## Project Layout

```
infra/                # docker-compose, Mosquitto config
ingest-service/       # Spring Boot micro‑service (Maven)
frontend-dashboard/   # React app (Vite) 
```

## Developer Workflow

1. **docker compose up** – bring up Mosquitto & Kafka.
2. **Fake sensor publisher** (Python script) pushes MQTT messages → backend.
3. Backend writes to TimescaleDB and produces to Kafka.
4. React dashboard renders live charts.

## Handy Commands

| Purpose                           | Command                                                    |
| --------------------------------- | ---------------------------------------------------------- |
| Rebuild infrastructure containers | `docker compose -f infra/docker-compose.yml up -d --build` |
| Tail Mosquitto logs               | `docker compose logs -f mosquitto`                         |
| Tail Kafka logs                   | `docker compose logs -f kafka`                             |
| Run unit & integration tests      | `./mvnw verify`                                            |
| Build Docker image for Spring     | `./mvnw spring-boot:build-image`                           |


# IoT Air Quality / Smart Home PoC

Full stack proof of concept that ingests sensor data (temperature, humidity, PM2·5) via **MQTT**, stores it in **TimescaleDB**, mirrors the raw JSON to **Kafka**, and exposes a **GraphQL** API consumed by a **React + Tailwind** dashboard.
Motion enabled video cameras and other Smart Home devices can be added later through the same pipeline.

```
Python (fake_sensor) ─► MQTT (Mosquitto) ─► ingest service (Spring Boot)
      │                       │
      ▼                       ▼
 TimescaleDB        Kafka topic `iot.raw` ─► Kafka UI
```

---

## 1  Repository layout

```
.
├─ ingest-service/          # Spring Boot 3, Java 21
│   ├─ src/main/java/…
│   └─ Dockerfile           # multistage build
├─ frontend-dashboard/      # Vite + React + Tailwind 4
│   └─ src/…
├─ infra/                   # docker-compose stack, mosquitto.conf
│   └─ docker-compose.yml
├─ scripts/                 # Python helpers
│   └─ fake_sensor.py
└─ DB_SETUP.md              # Timescale schema + retention/aggregates
```

---

## 2  Quick start – full stack in Docker

```bash
cd infra
# build images & start everything
docker compose up -d --build   # GraphQL API → http://localhost:8080
                              # Kafka UI     → http://localhost:8082
```

*Mosquitto* is mapped on **1883**, Kafka broker on **9092**.

Generate demo data:

```bash
pip install paho mqtt faker
python scripts/fake_sensor.py
```

Dashboard (if already built) at **[http://localhost:5173](http://localhost:5173)**.

---

## 3  Dev mode – ingest local, infra in Docker

1. Start only infrastructure (profile `prod` disabled):

   ```bash
   cd infra
   docker compose up -d mosquitto kafka zookeeper kafka-ui
   ```
2. In **ingest-service** run:

   ```bash
   SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
   MQTT_BROKER=tcp://localhost:1883 \
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
3. Logs will show `RX SensorRecord…` for each MQTT publish.

---

## 4  Frontend dashboard

```bash
cd frontend-dashboard
npm install          # installs React, Vite, Tailwind 4
npm run dev          # http://localhost:5173
```

* Sign in on `/login` (mock) → live charts on `/dashboard`.

---

## 5  Database bootstrap & retention

All SQL lives in **DB\_SETUP.md**: hypertable creation, 5 minute continuous
aggregate, 30 day retention policy.
Run in `psql -U postgres -d smarthome`.

---

## 6  Tech stack

| Layer               | Technology                                  | Notes                                        |
| ------------------- |---------------------------------------------|----------------------------------------------|
| Transport           | MQTT / Mosquitto 2.0                        | topic `sensors/#`                            |
| Ingest microservice | Spring Boot 3.4, Java 21                    | MqttPaho v5, Kafka template, JPA + Timescale |
| Storage             | TimescaleDB 2.x                             | hypertable + CGA + retention                 |
| Stream              | Kafka 3.6 + Kafka UI                        | raw JSON forwarded                           |
| API                 | Spring GraphQL                              | `/graphql` + subscriptions (planned)         |
| UI                  | React 18, Vite 5, Tailwind 4, Apollo Client | live polling 5 s                             |

---

## 7 How to add a real sensor / camera

1. Flash ESPHome YAML (to do) on ESP32 → publish to `sensors/<room>/<id>`.
2. Camera with motion → push MQTT or ONVIF event → same topic family.
3. Dashboard auto discovers new `field` values; add new `<ChartCard field="x"/>`.

---

## 8  CI/CD (roadmap)

* GitHub Actions: Maven Build, Docker Buildx, push `ghcr.io/<user>/ingest`.
* Tag `latest` on merge to *main*, tag `vX.Y.0` on release.

---

## 9  Security / hardening (roadmap)

* Mosquitto ACL + JWT auth
* mTLS Kafka
* Prometheus & Grafana stack
* OAuth2 login for dashboard.

---

## 10  Development tips

* `docker compose logs -f ingest kafka mosquitto` for tailing.
* Reset Kafka volume: `docker compose down -v kafkadata`.
* HMR in Vite sometimes caches CSS → restart with `CTRL+C` then `npm run dev`.

---

## License

MIT (see LICENSE).


---

*Feel free to open an issue or a pull request if you spot any problems or want to contribute!*

