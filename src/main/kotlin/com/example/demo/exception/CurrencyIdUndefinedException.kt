package com.example.demo.exception

import org.springframework.graphql.execution.ErrorType

class CurrencyIdUndefinedException(inputField: String) : CurrencyException (
    "currency.id.undefined",
    ErrorType.BAD_REQUEST,
    mapOf("inputField" to inputField),
    "Currency in $inputField undefined",
)
