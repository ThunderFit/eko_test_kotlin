package com.example.demo.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CurrencyInput (
    @get:NotBlank
    val srcCode: String,
    @get:Positive
    val price: BigDecimal,
    @get:NotEmpty
    val dstCodes: List<String>,
)
