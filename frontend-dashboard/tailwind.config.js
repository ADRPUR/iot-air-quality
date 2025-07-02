/** @type {import('tailwindcss').Config} */
export default {
    // Toate fișierele .js/.jsx din src vor fi scanate
    content: ["./src/**/*.{js,jsx}"],

    theme: {
        extend: {
            // aici poți adăuga culori, font-family, spacing custom etc.
            colors: {
                primary: "#6366f1",   // exemplu violet Tailwind
            },
        },
    },
    plugins: [
        // deocamdată niciun plugin; adaugă când ai nevoie de forms/typography etc.
    ],
};
