import {
    ApolloClient,
    InMemoryCache,
    HttpLink,
    split,
} from "@apollo/client";
import {setContext} from "@apollo/client/link/context";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {getMainDefinition} from "@apollo/client/utilities";
import {createClient as createWsClient} from "graphql-ws";
import {onError} from "@apollo/client/link/error";
import * as operation from "@apollo/client";

/** Helper — creates an Apollo Client for a URL base. */
export function makeApolloClient(baseHttpUrl) {
    if (!baseHttpUrl) {
        throw new Error("Missing GraphQL HTTP URL");
    }
    /* ---------------- HTTP (Bearer) ---------------- */
    const httpLink = new HttpLink({uri: baseHttpUrl, credentials: "include"});

    const authLink = setContext((_, {headers}) => {
        if (operation.operationName === "login" ||
            operation.operationName === "register") {
            return { headers };
        }
        const token = localStorage.getItem("iot.access");
        return {
            headers: {
                ...headers,
                ...(token ? {Authorization: `Bearer ${token}`} : {}),
            },
        };
    });

    // auto convert http://host:port/graphql → ws://host:port/graphql
    const baseWsUrl = baseHttpUrl.replace(/^http/, "ws");

    /* ---------------- WebSocket (Bearer) ----------- */
    const wsLink = new GraphQLWsLink(
        createWsClient({
            url: baseWsUrl,
            lazy: true,
            retryAttempts: 3,
            connectionParams: () => {
                const token = localStorage.getItem("iot.access");
                return token ? {Authorization: `Bearer ${token}`} : {};
            },
        })
    );

    // route: query+mutation → HTTP, subscription → WS
    const splitLink = split(
        ({query}) => {
            const def = getMainDefinition(query);
            return def.kind === "OperationDefinition" && def.operation === "subscription";
        },
        wsLink,
        authLink.concat(httpLink)
    );

    const errorLink = onError(({graphQLErrors, networkError}) => {
        if (graphQLErrors) {
            graphQLErrors.forEach(err => console.error("[GraphQL error]", err));
        }
        if (networkError) console.error("[Network error]", networkError);
    });

    return new ApolloClient({
        link: errorLink.concat(splitLink),
        cache: new InMemoryCache(),
        connectToDevTools: import.meta.env.DEV,
        defaultOptions: {
            watchQuery: {fetchPolicy: "cache-and-network", errorPolicy: "all"},
            query: {fetchPolicy: "network-only", errorPolicy: "all"},
        },
    });
}

/* ───────────────────────── clients instanţiate ───────────────────────── */

/** Ingest-service (sensor data + graphs) */
export const ingestClient = makeApolloClient(
    import.meta.env.VITE_INGEST_URL || "http://localhost:8080/graphql",
);

/** Alert-service (notifications, rules) */
export const alertClient = makeApolloClient(
    import.meta.env.VITE_ALERT_URL || "http://localhost:8081/graphql",
);

/** Auth-service (users & tokens) */
export const authClient = makeApolloClient(
    import.meta.env.VITE_AUTH_URL || "http://localhost:8083/graphql",
);
