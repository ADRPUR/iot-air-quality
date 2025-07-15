/* ------------------------------------------------------
   pages/SensorsPage.jsx – list, rename & toggle visibility of sensors
------------------------------------------------------ */
import { useQuery, useMutation, gql } from "@apollo/client";
import { useState } from "react";
import StatusState from "@/components/common/StatusState.jsx";
import clsx from "clsx";

/* ----------------- GraphQL ----------------- */
const GET_SENSORS = gql`
    query Sensors {
        sensors {
            sensorId
            name
            visible
        }
    }
`;

const SET_VISIBILITY = gql`
    mutation SetSensorVisibility($sensorId: String!, $visible: Boolean!) {
        setSensorVisibility(sensorId: $sensorId, visible: $visible) {
            sensorId
            visible
        }
    }
`;

const RENAME_SENSOR = gql`
    mutation RenameSensor($sensorId: String!, $name: String!) {
        renameSensor(sensorId: $sensorId, name: $name) {
            sensorId
            name
        }
    }
`;

/* ----------------- Component ---------------- */
export default function SensorsPage() {
    const {
        data,
        loading,
        error,
        refetch,
    } = useQuery(GET_SENSORS, { pollInterval: 5_000 });

    const [setVisible] = useMutation(SET_VISIBILITY, {
        onCompleted: () => refetch(),
    });
    const [rename] = useMutation(RENAME_SENSOR, {
        onCompleted: () => refetch(),
    });

    const [editName, setEditName] = useState({}); // {sensorId: draftName}

    if (loading && !data?.sensors)
        return <StatusState type="loading" text="Loading sensors…" />;
    if (error)
        return <StatusState type="error" text="Server unavailable" />;
    if (!data?.sensors?.length)
        return <StatusState type="empty" text="No sensors" />;

    const handleToggle = (s) => {
        setVisible({ variables: { sensorId: s.sensorId, visible: !s.visible } });
    };

    const handleSaveName = (s) => {
        const newName = editName[s.sensorId];
        if (newName != null && newName.trim() !== "" && newName !== s.name) {
            rename({ variables: { sensorId: s.sensorId, name: newName.trim() } });
        }
    };

    return (
        <div className="p-6 max-w-5xl mx-auto">
            <h1 className="text-2xl font-semibold mb-6">Sensors</h1>

            <div className="overflow-x-auto shadow rounded-lg ring-1 ring-gray-200">
                <table className="min-w-full divide-y divide-gray-200 text-sm">
                    <thead className="bg-gray-50 text-left font-semibold text-gray-600">
                    <tr>
                        <th className="px-4 py-3">Sensor&nbsp;ID</th>
                        <th className="px-4 py-3">Name</th>
                        <th className="px-4 py-3">Visible</th>
                        <th className="px-4 py-3" />
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                    {data.sensors.map((s) => (
                        <tr key={s.sensorId} className="hover:bg-gray-50">
                            <td className="px-4 py-2 font-mono text-xs">{s.sensorId}</td>

                            {/* editable name */}
                            <td className="px-4 py-2">
                                <input
                                    value={editName[s.sensorId] ?? s.name ?? ""}
                                    onChange={(e) =>
                                        setEditName((m) => ({ ...m, [s.sensorId]: e.target.value }))
                                    }
                                    className="w-full border rounded px-2 py-1 text-sm"
                                    placeholder="(no name)"
                                />
                            </td>

                            {/* toggle visible */}
                            <td className="px-4 py-2">
                                <label className="inline-flex items-center gap-2 cursor-pointer">
                                    <input
                                        type="checkbox"
                                        checked={s.visible}
                                        onChange={() => handleToggle(s)}
                                        className="h-4 w-4 text-sky-600"
                                    />
                                    <span>{s.visible ? "Yes" : "No"}</span>
                                </label>
                            </td>

                            {/* save btn */}
                            <td className="px-4 py-2 text-right">
                                <button
                                    onClick={() => handleSaveName(s)}
                                    disabled={(editName[s.sensorId] ?? s.name) === s.name}
                                    className={clsx(
                                        "px-3 py-1.5 text-xs rounded text-white",
                                        (editName[s.sensorId] ?? s.name) === s.name
                                            ? "bg-gray-400 cursor-not-allowed"
                                            : "bg-emerald-500 hover:bg-emerald-600"
                                    )}
                                >
                                    Save
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
