// src/components/Notifications.jsx
import { useEffect } from "react";
import { useSubscription, gql } from "@apollo/client";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const ALERT_SUB = gql`
    subscription {
        alertFired {
            id ts sensorId field value limit channels
        }
    }
`;

export default function Notifications() {
    const { data, error } = useSubscription(ALERT_SUB);

    useEffect(() => {
        if (error) toast.error("Subscription error");
        if (data) {
            const a = data.alertFired;
            toast.warning(
                `${a.sensorId} ${a.field}=${a.value} > ${a.limit}`,
                { autoClose: 8000 }
            );
        }
    }, [data, error]);

    return <ToastContainer position="top-right" />;
}
