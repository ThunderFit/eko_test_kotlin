package com.example.demo.exception

import org.springframework.graphql.execution.ErrorType

class CurrencyNotFoundException(currencyCode: String) : AbstractException (
    "currency.not.found",
    ErrorType.NOT_FOUND,
    mapOf("currencyCode" to currencyCode),
    "Currency $currencyCode not found",
)