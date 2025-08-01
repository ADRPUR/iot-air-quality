package com.example.iot.authservice.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.IntValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Locale;

/**
 * Custom GraphQL scalar for 64-bit Long values.
 */
public final class LongScalar {

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
            .name("Long")
            .description("64-bit signed integer")
            .coercing(new Coercing<Long, Long>() {
                @Override
                public Long serialize(@NotNull Object dataFetcherResult,
                                      @NotNull GraphQLContext context,
                                      @NotNull Locale locale) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof Long l) {
                        return l;
                    }
                    if (dataFetcherResult instanceof Number n) {
                        return n.longValue();
                    }
                    throw new CoercingSerializeException(
                            "Expected a Long or Number but was " + dataFetcherResult.getClass().getSimpleName());
                }

                @Override
                public Long parseValue(@NotNull Object input,
                                       @NotNull GraphQLContext context,
                                       @NotNull Locale locale) throws CoercingParseValueException {
                    if (input instanceof Number n) {
                        return n.longValue();
                    }
                    if (input instanceof String s) {
                        try {
                            return Long.parseLong(s);
                        } catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Invalid Long value: " + s, e);
                        }
                    }
                    throw new CoercingParseValueException(
                            "Expected a Number or numeric String but was " + input.getClass().getSimpleName());
                }

                @Override
                public Long parseLiteral(@NotNull Value<?> input,
                                         @NotNull CoercedVariables variables,
                                         @NotNull GraphQLContext context,
                                         @NotNull Locale locale) throws CoercingParseLiteralException {
                    if (input instanceof IntValue iv) {
                        BigInteger value = iv.getValue();
                        try {
                            return value.longValueExact();
                        } catch (ArithmeticException e) {
                            throw new CoercingParseLiteralException(
                                    "Long literal value out of range: " + value, e);
                        }
                    }
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'IntValue' but was " + input.getClass().getSimpleName());
                }
            })
            .build();

    private LongScalar() {
    }
}
