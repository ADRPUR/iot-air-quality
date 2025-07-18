package com.example.iot.alert.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

/**
 * Custom scalar for Java UUID mapped to GraphQL UUID scalar.
 */
public final class UUIDScalar {

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
            .name("UUID")
            .description("java.util.UUID scalar type")
            .coercing(new Coercing<UUID, String>() {
                @Override
                public String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext context, @NotNull Locale locale) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof UUID uuid) {
                        return uuid.toString();
                    }
                    throw new CoercingSerializeException("Expected UUID but was " + dataFetcherResult.getClass().getSimpleName());
                }

                @Override
                public UUID parseValue(@NotNull Object input, @NotNull GraphQLContext context, @NotNull Locale locale) throws CoercingParseValueException {
                    if (input instanceof String s) {
                        try {
                            return UUID.fromString(s);
                        } catch (IllegalArgumentException e) {
                            throw new CoercingParseValueException("Invalid UUID format: " + s, e);
                        }
                    }
                    throw new CoercingParseValueException("Expected UUID string but was " + input.getClass().getSimpleName());
                }

                @Override
                public UUID parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext context, @NotNull Locale locale) throws CoercingParseLiteralException {
                    if (input instanceof StringValue sv) {
                        try {
                            return UUID.fromString(sv.getValue());
                        } catch (IllegalArgumentException e) {
                            throw new CoercingParseLiteralException("Invalid UUID format: " + sv.getValue(), e);
                        }
                    }
                    throw new CoercingParseLiteralException("UUID literal must be a string");
                }
            })
            .build();

    private UUIDScalar() {
    }
}

