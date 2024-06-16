package com.example.demo.http.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import com.example.demo.model.CoinbaseCurrenciesResponse
import com.example.demo.model.CoinbaseCurrencyRatesResponse
import com.example.demo.model.CoinbaseCurrencyRates
import com.example.demo.model.CoinbaseCurrency

class CoinBaseClient () {
    // fixme: `client` должен быть приватным в данном случае.
    val client = HttpClient.newBuilder().build()

    fun getRates(code: String) : CoinbaseCurrencyRates {
        val response = this.client.send(
            this.getRequest("https://api.coinbase.com/v2/exchange-rates?currency=$code"),
            HttpResponse.BodyHandlers.ofString()
        )

        return Json.decodeFromString<CoinbaseCurrencyRatesResponse>(response.body()).data
    }

    fun getCurrencies() : List<CoinbaseCurrency> {
        val response = this.client.send(
            this.getRequest("https://api.coinbase.com/v2/currencies"),
            HttpResponse.BodyHandlers.ofString()
        )

        return Json.decodeFromString<CoinbaseCurrenciesResponse>(response.body()).data
    }

    // fixme: `getRequest` тоже должен быть приватным.
    fun getRequest(uri: String) : HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build()
    }
}
