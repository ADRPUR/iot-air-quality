/* ------------------------------------------------------
   Generic Card wrapper used across the app.
   Props:
     - title?: string           // optional header text
     - icon?:  ReactNode        // optional left-aligned icon
     - actions?: ReactNode      // optional node rendered top‑right
     - className?: string       // extra Tailwind classes
------------------------------------------------------ */
import React from "react";
import clsx from "clsx";

export default function Card({ title, icon, actions, className = "", children }) {
    return (
        <div
            className={clsx(
                "rounded-xl shadow bg-white p-4 flex flex-col w-full ring-1 ring-gray-100",
                className
            )}
        >
            {(title || icon || actions) && (
                <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center gap-2">
                        {icon && <span className="text-gray-500">{icon}</span>}
                        {title && <h2 className="font-semibold text-lg leading-tight">{title}</h2>}
                    </div>
                    {actions && <div className="flex-shrink-0">{actions}</div>}
                </div>
            )}
            <div className="flex-1">{children}</div>
        </div>
    );
}
