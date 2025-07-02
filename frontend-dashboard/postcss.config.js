// postcss.config.js  (ESM style)
import tailwind from "@tailwindcss/postcss";
import autoprefixer from "autoprefixer";

export default {
    plugins: [tailwind, autoprefixer],
};