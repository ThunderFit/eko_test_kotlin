package com.example.demo.http.client

import com.example.demo.model.CoinbaseCurrenciesResponse
import com.example.demo.model.CoinbaseCurrencyRatesResponse
import kotlinx.serialization.json.Json
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal

@Component
class CoinBaseClient(
    webClientBuilder: WebClient.Builder,
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val webClient = webClientBuilder
        .baseUrl("https://api.coinbase.com/v2/")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .codecs {
            it.defaultCodecs().kotlinSerializationJsonEncoder(KotlinSerializationJsonEncoder(json))
            it.defaultCodecs().kotlinSerializationJsonDecoder(KotlinSerializationJsonDecoder(json))
        }
        .build()

    suspend fun getRates(code: String): Map<String, BigDecimal> {
        return webClient.get()
            .uri {
                it.path("exchange-rates")
                    .queryParam("currency", code)
                    .build()
            }
            .retrieve()
            .awaitBody<CoinbaseCurrencyRatesResponse>()
            .data.rates
            .let {
                buildMap {
                    it.forEach { (k, v) ->
                        put(k, BigDecimal(v))
                    }
                }
            }
    }

    suspend fun getCurrencies(): Set<String> {
        return webClient.get()
            .uri("currencies")
            .retrieve()
            .awaitBody<CoinbaseCurrenciesResponse>()
            .data
            .let { items ->
                buildSet {
                    items.forEach {
                        add(it.id)
                    }
                }
            }
    }
}
