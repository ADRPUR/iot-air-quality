/***********************************************************************
 *  /src/api/auth.gql.js
 *  Toate operaţiile GraphQL expuse de auth-service (port 8083)
 **********************************************************************/
import { gql } from "@apollo/client";

/* ─────────────── LOGIN + REFRESH + LOGOUT ─────────────── */
export const LOGIN_MUTATION = gql`
    mutation Login($email: String!, $password: String!) {
        login(email: $email, password: $password) {
            accessToken
            refreshToken
            expiresIn
        }
    }
`;

export const REFRESH_MUTATION = gql`
    mutation Refresh($refreshToken: String!) {
        refresh(refreshToken: $refreshToken) {
            accessToken
            refreshToken
            expiresIn
        }
    }
`;

export const LOGOUT_MUTATION = gql`
    mutation Logout($refreshToken: String!) {
        logout(refreshToken: $refreshToken)
    }
`;

/* ─────────────── REGISTER USER (public) ─────────────── */
export const REGISTER_MUTATION = gql`
    mutation Register(
        $email:      String!
        $password:   String!
        $firstName:  String
        $lastName:   String
    ) {
        createUser(
            email:     $email
            password:  $password
            firstName: $firstName
            lastName:  $lastName
        ) {
            id
            email
            enabled
            createdAt
        }
    }
`;

/* ─────────────── CURRENT USER (me) ─────────────── */
export const ME_QUERY = gql`
    query Me {
        me {
            id
            email
            firstName
            lastName
            roles
        }
    }
`;
