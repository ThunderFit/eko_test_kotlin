package com.example.demo.controller

import com.example.demo.controller.dto.CurrencyInput
import com.example.demo.exception.BadInputException
import com.example.demo.exception.CurrencyException
import com.example.demo.exception.CurrencyIdUndefinedException
import com.example.demo.http.client.CoinBaseClient
import com.example.demo.controller.dto.Currency
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.mono
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono


@Controller
class CurrencyQuery(
    private val coinbaseClient: CoinBaseClient,
) {
    @QueryMapping
    fun convert(@Argument @Valid input: CurrencyInput): Mono<List<Currency?>> = mono {

        if (input.dstCodes.isEmpty()) {
            throw BadInputException("currencies")
        }

        val currencyIds = coinbaseClient.getCurrencies()

        if (input.srcCode !in currencyIds) {
            throw CurrencyIdUndefinedException("input.id")
        }
        if (!currencyIds.containsAll(input.dstCodes)) {
            throw CurrencyIdUndefinedException("currencies")
        }

        val rates = coinbaseClient.getRates(input.srcCode)

        input.dstCodes.map { code ->
            rates[code]?.let { rate ->
                Currency(code, input.price * rate)
            }
        }
    }

    @GraphQlExceptionHandler
    fun handleException(ex: CurrencyException, env: DataFetchingEnvironment): GraphQLError {
        return GraphqlErrorBuilder.newError(env)
            .errorType(ex.errorType)
            .message(ex.message)
            .extensions(
                mapOf(
                    "code" to ex.code,
                    "params" to ex.params,
                )
            )
            .build()
    }

    @GraphQlExceptionHandler
    fun handleException(ex: ConstraintViolationException, env: DataFetchingEnvironment): GraphQLError {
        return GraphqlErrorBuilder.newError(env)
            .errorType(ErrorType.BAD_REQUEST)
            .message(ex.message)
            .extensions(mapOf(
                "constraintViolations" to ex.constraintViolations.map {
                    it.propertyPath.toString() to it.message
                }
            ))
            .build()
    }
}
