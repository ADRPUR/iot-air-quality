import React from "react";
import ReactDOM from "react-dom/client";
import { ApolloProvider } from "@apollo/client";
import { ingestClient } from "@/api/clients";
import App from "./App.jsx";
import "./index.css";        // Tailwind entry

ReactDOM.createRoot(document.getElementById("root")).render(
    <ApolloProvider client={ingestClient}>
        <App/>
    </ApolloProvider>
);