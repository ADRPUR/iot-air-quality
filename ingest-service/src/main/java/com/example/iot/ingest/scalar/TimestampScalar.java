package com.example.iot.ingest.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Custom scalar for ISO-8601 instant (UTC) mapped to {@link Instant}.
 * Accepts String literal or variable value.
 */
public final class TimestampScalar {

    /** Reusable instance (singleton). */
    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
            .name("Timestamp")
            .description("ISO-8601 instant in UTC, e.g. \"2025-07-15T05:42:30.123Z\"")
            .coercing(new Coercing<Instant, String>() {

                /* ------------ OUT: Java -> GraphQL ------------ */
                @Override
                public String serialize(@NotNull Object dataFetcherResult,
                                        @NotNull GraphQLContext context,
                                        @NotNull Locale locale) throws CoercingSerializeException {

                    if (dataFetcherResult instanceof Instant instant) {
                        return instant.toString();        // ISO-8601 Z
                    }
                    throw new CoercingSerializeException(
                            "Expected Instant but was " + dataFetcherResult.getClass().getSimpleName());
                }

                /* ------------ IN: variables ------------ */
                @Override
                public Instant parseValue(@NotNull Object input,
                                          @NotNull GraphQLContext context,
                                          @NotNull Locale locale) throws CoercingParseValueException {

                    if (input instanceof String s) {
                        return parseIsoInstant(s);
                    }
                    throw new CoercingParseValueException(
                            "Expected ISO-8601 string for Timestamp variable");
                }

                /* ------------ IN: literal ------------ */
                @Override
                public Instant parseLiteral(@NotNull Value<?> input,
                                            @NotNull CoercedVariables variables,
                                            @NotNull GraphQLContext context,
                                            @NotNull Locale locale) throws CoercingParseLiteralException {

                    if (input instanceof StringValue sv) {
                        return parseIsoInstant(sv.getValue());
                    }
                    throw new CoercingParseLiteralException(
                            "Timestamp literal must be string but is " + input.getClass().getSimpleName());
                }

                /* ------------ helper ------------ */
                private Instant parseIsoInstant(String text) {
                    try {
                        return Instant.parse(text);      // RFC-3339 / ISO-8601
                    } catch (DateTimeParseException e) {
                        throw new CoercingParseValueException("Invalid ISO-8601 instant: " + text, e);
                    }
                }
            })
            .build();

    private TimestampScalar() {}
}
