/* ------------------------------------------------------
   Unified component for Loading / Error / Empty states.
   Props:
     - type: "loading" | "error" | "empty"
     - text?: string   // subtitle shown under icon/spinner
------------------------------------------------------ */
import React from "react";
import { Loader2, AlertTriangle, Inbox } from "lucide-react";
import clsx from "clsx";

export default function StatusState({ type = "loading", text = "" }) {
    const base = "flex flex-col items-center justify-center gap-3 py-10";

    /** choose icon / spinner + color */
    const content = (() => {
        switch (type) {
            case "error":
                return (
                    <>
                        <AlertTriangle className="w-10 h-10 text-red-500" />
                        <p className="text-red-600 font-medium">{text || "Server unavailable"}</p>
                    </>
                );
            case "empty":
                return (
                    <>
                        <Inbox className="w-10 h-10 text-gray-400" />
                        <p className="text-gray-500">{text || "No data"}</p>
                    </>
                );
            default: // "loading"
                return (
                    <>
                        <Loader2 className="w-10 h-10 animate-spin text-sky-500" />
                        <p className="text-sky-600">{text || "Loading…"}</p>
                    </>
                );
        }
    })();

    return <div className={clsx(base)}>{content}</div>;
}
