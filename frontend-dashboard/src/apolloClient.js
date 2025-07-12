// src/apolloClients.js
import {
    ApolloClient,
    InMemoryCache,
    HttpLink,
    split,
    from,
} from "@apollo/client";
import { onError } from "@apollo/client/link/error";
import { GraphQLWsLink } from "@apollo/client/link/subscriptions";
import { getMainDefinition } from "@apollo/client/utilities";
import { createClient } from "graphql-ws";

// error logger (shared)
const errorLink = onError(({ networkError }) => {
    if (networkError) console.log("[GraphQL Network]", networkError);
});

// HTTP link for queries & mutations
const alertHttpLink = new HttpLink({
    uri: "http://localhost:8081/graphql",
});

// WebSocket link for subscriptions
const alertWsLink = new GraphQLWsLink(
    createClient({
        url: "ws://localhost:8081/graphql",
        connectionParams: {
            // e.g. authToken: localStorage.getItem("token")
        },
    })
);

// HTTP link for queries & mutations
const ingestHttpLink = new HttpLink({
    uri: "http://localhost:8080/graphql",
});

// WebSocket link for subscriptions
const ingestWsLink = new GraphQLWsLink(
    createClient({
        url: "ws://localhost:8080/graphql",
        connectionParams: {
            // e.g. authToken: localStorage.getItem("token")
        },
    })
);

// split based on operation type
const alertSplitLink = split(
    ({ query }) => {
        const def = getMainDefinition(query);
        return (
            def.kind === "OperationDefinition" &&
            def.operation === "subscription"
        );
    },
    alertWsLink,
    alertHttpLink
);

// split based on operation type
const ingestSplitLink = split(
    ({ query }) => {
        const def = getMainDefinition(query);
        return (
            def.kind === "OperationDefinition" &&
            def.operation === "subscription"
        );
    },
    ingestWsLink,
    ingestHttpLink
);

export const alertClient = new ApolloClient({
    link: from([errorLink, alertSplitLink]),
    cache: new InMemoryCache(),
});

// ingestClient stays HTTP‐only:
export const ingestClient = new ApolloClient({
    link: from([errorLink, ingestSplitLink]),
    cache: new InMemoryCache(),
});
