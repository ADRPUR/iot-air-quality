import { ApolloClient, InMemoryCache, HttpLink, from } from "@apollo/client";
import { onError } from "@apollo/client/link/error";

const alertHttp = new HttpLink({ uri: "http://localhost:8081/graphql" });

const alertErrorLink = onError(({ networkError }) => {
    if (networkError) console.log("[Alert GraphQL network]", networkError);
});

export const alertClient = new ApolloClient({
    link: from([alertErrorLink, alertHttp]),
    cache: new InMemoryCache(),
});

