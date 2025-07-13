// src/routes/SensorsPage.jsx
import { gql, useMutation, useQuery } from "@apollo/client";

const GET_SENSORS = gql`
    query { sensors { sensorId name visible } }
`;

const TOGGLE = gql`
    mutation($sensorId: String!, $visible: Boolean!) {
        setSensorVisibility(sensorId: $sensorId, visible: $visible) {
            sensorId visible
        }
    }
`;

const RENAME = gql`
    mutation($sensorId: String!, $name: String!) {
        renameSensor(sensorId: $sensorId, name: $name) {
            sensorId name
        }
    }
`;

export default function SensorsPage() {
    const { data, loading, error } = useQuery(GET_SENSORS, { pollInterval: 8000 });
    const [toggle] = useMutation(TOGGLE, { refetchQueries: [GET_SENSORS] });
    const [rename] = useMutation(RENAME, { refetchQueries: [GET_SENSORS] });

    if (loading) return <p className="p-4 text-gray-500">Loading sensors ...</p>;
    if (error) return <p className="p-4 text-red-600">Error loading sensors</p>;

    return (
        <div className="p-6 max-w-xl mx-auto space-y-3">
            <h1 className="text-2xl font-semibold mb-4">Sensors</h1>

            {data.sensors.map(s => (
                <SensorCard
                    key={s.sensorId}
                    sensor={s}
                    onToggle={v => toggle({ variables: { sensorId: s.sensorId, visible: v } })}
                    onRename={name => rename({ variables: { sensorId: s.sensorId, name } })}
                />
            ))}
        </div>
    );
}

/* sub-component with inline rename */
import { useState } from "react";
function SensorCard({ sensor, onToggle, onRename }) {
    const [edit, setEdit] = useState(false);
    const [name, setName] = useState(sensor.name ?? "");

    const saveName = () => {
        setEdit(false);
        onRename(name.trim());
    };

    return (
        <div className="border rounded p-3 flex justify-between items-center">
            <div>
                {edit ? (
                    <input
                        value={name}
                        onChange={e => setName(e.target.value)}
                        onBlur={saveName}
                        onKeyDown={e => e.key === "Enter" && saveName()}
                        className="border p-1 rounded mr-2 w-40"
                        autoFocus
                    />
                ) : (
                    <span className="font-medium mr-2">
            {sensor.name || sensor.sensorId}
          </span>
                )}
                <button onClick={() => setEdit(!edit)} className="text-blue-600 text-sm">
                    {edit ? "Save" : "Rename"}
                </button>
            </div>

            <label className="inline-flex items-center space-x-2">
                <input
                    type="checkbox"
                    checked={sensor.visible}
                    onChange={e => onToggle(e.target.checked)}
                />
                <span className="text-sm">{sensor.visible ? "Shown" : "Hidden"}</span>
            </label>
        </div>
    );
}
