package com.example.demo.exception

import org.springframework.graphql.execution.ErrorType

class BadInputException(inputField: String) : CurrencyException (
    "bad.input",
    ErrorType.BAD_REQUEST,
    mapOf("inputField" to inputField),
    "Bad input $inputField value",
)
