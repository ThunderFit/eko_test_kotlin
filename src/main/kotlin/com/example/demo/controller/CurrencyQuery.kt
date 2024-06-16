package com.example.demo.controller

import com.example.demo.exception.CurrencyNotFoundException
import com.example.demo.exception.CurrencyIdUndefinedException
import com.example.demo.exception.BadInputException
import com.example.demo.model.Currency
import com.example.demo.dto.ConvertRequest
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import com.example.demo.http.client.CoinBaseClient


@Controller
class CurrencyQuery() {
    @QueryMapping
    fun convert(@Argument input: ConvertRequest): Array<Currency> {

        if (input.currencies.size == 0) {
            throw BadInputException("currencies")
        }

        val coinbaseClient = CoinBaseClient()
        val coinbaseCurrencies = coinbaseClient.getCurrencies()
        val currencyIds = coinbaseCurrencies.map { it.id }

        if (!currencyIds.contains(input.id)) {
            throw CurrencyIdUndefinedException("input.id")
        }
        if (!currencyIds.containsAll(input.currencies)) {
            throw CurrencyIdUndefinedException("currencies")
        }

        var coinbaseCurrency = coinbaseClient.getRates(input.id)

        var currencies = arrayOf<Currency>()

        input.currencies.forEach({
            var rate = coinbaseCurrency.rates[it]?.toFloat()
            //TODO: must be null
            if (rate !== null) {
                currencies += Currency(it, input.price * rate)
            }
        })

        return currencies
    }
}
