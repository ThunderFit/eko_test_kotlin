package com.example.demo.model

import kotlinx.serialization.Serializable

@Serializable
data class CoinbaseCurrency(
    val id: String,
    val name: String,
    val min_size: String
)

@Serializable
data class CoinbaseCurrenciesResponse(
    val data: List<CoinbaseCurrency>,
)


@Serializable
data class CoinbaseCurrencyRates(
    val currency: String,
    val rates: HashMap<String, String>
)

@Serializable
data class CoinbaseCurrencyRatesResponse(
    val data: CoinbaseCurrencyRates,
)
