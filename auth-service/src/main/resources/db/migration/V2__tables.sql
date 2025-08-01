CREATE TABLE auth.users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(128) NOT NULL UNIQUE,
    first_name VARCHAR(64),
    last_name  VARCHAR(64),
    password   TEXT         NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE auth.roles
(
    id   UUID PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE auth.user_roles
(
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE auth.refresh_tokens
(
    id      UUID PRIMARY KEY,
    token   varchar(512)        NOT NULL UNIQUE,
    user_id UUID        NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    expiry  TIMESTAMPTZ NOT NULL
);

CREATE TABLE auth.login_audit
(
    id       UUID NOT NULL,
    ts       TIMESTAMPTZ NOT NULL,
    user_id  UUID        NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    ip       varchar(100),
    success  BOOLEAN,
    PRIMARY KEY (id, ts)
);
