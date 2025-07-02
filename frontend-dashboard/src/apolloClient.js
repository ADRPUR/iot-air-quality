import { ApolloClient, InMemoryCache, HttpLink, from } from "@apollo/client";
import { onError } from "@apollo/client/link/error";

const http = new HttpLink({ uri: "http://localhost:8080/graphql" });

const errorLink = onError(({ networkError }) => {
    if (networkError) console.log("[GraphQL-network]", networkError);
    // We let the query continue — components will receive `error`
});

export const client = new ApolloClient({
    link: from([errorLink, http]),
    cache: new InMemoryCache(),
});
