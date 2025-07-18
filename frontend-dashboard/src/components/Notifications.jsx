// src/components/Notifications.jsx
import { useEffect } from "react";
import { useSubscription } from "@apollo/client";
import { ALERT_FIRED_SUB } from "@/api/alert.gql.js";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function Notifications() {
    /* live stream of alerts */
    const { data, error } = useSubscription(ALERT_FIRED_SUB);

    /* error on websocket / fallback */
    useEffect(() => {
        if (error) toast.error("Alert stream disconnected");
    }, [error]);

    /* when a new alert comes in – we display a toast */
    useEffect(() => {
        if (!data?.alertTriggered) return;

        const a = data.alertTriggered; // { id, created, sensorId, field, level, message … }

        switch (a.level) {
            case "INFO":
                toast.info(
                    `${a.sensorId}.${a.field} – ${a.message}`,
                    { autoClose: 5_000 }
                );
                break;
            case "WARN":
                toast.warn(
                    `${a.sensorId}.${a.field} – ${a.message}`,
                    { autoClose: 6_000 }
                );
                break;
            case "CRITICAL":
                toast.error(
                    `${a.sensorId}.${a.field} – ${a.message}`,
                    { autoClose: 10_000 }
                );
                break;
            default:
                toast.error(
                    `Unknown alert level: ${a.level}`,
                    { autoClose: 10_000 }
                );
        }
    }, [data]);

    return (
        <ToastContainer
            position="top-right"
            newestOnTop
            theme="colored"
        />
    );
}
