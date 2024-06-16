package com.example.demo.model

import kotlinx.serialization.Serializable

@Serializable
data class CoinbaseCurrency(
    val id: String,
)

@Serializable
data class CoinbaseCurrenciesResponse(
    val data: List<CoinbaseCurrency>,
)

@Serializable
data class CoinbaseCurrencyRates(
    val rates: Map<String, String>
)

@Serializable
data class CoinbaseCurrencyRatesResponse(
    val data: CoinbaseCurrencyRates,
)
