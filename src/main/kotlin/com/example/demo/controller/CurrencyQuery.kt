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
        // todo: Для конкурентного вычисления полей принято возвращать `CompletableFuture`, Mono/Flux или делать
        //  метод `suspend`.
        // todo: Принято использовать интерфейс List, а не какую-то его конкретную реализацию или массив.

        if (input.currencies.size == 0) {
            throw BadInputException("currencies")
        }

        // fixme: CoinBaseClient стоит сделать Bean-ом и инжектить его в контролер.
        val coinbaseClient = CoinBaseClient()
        val coinbaseCurrencies = coinbaseClient.getCurrencies()
        val currencyIds = coinbaseCurrencies.map { it.id }

        if (!currencyIds.contains(input.id)) {
            throw CurrencyIdUndefinedException("input.id")
        }
        if (!currencyIds.containsAll(input.currencies)) {
            throw CurrencyIdUndefinedException("currencies")
        }

        // fixme: coinbaseCurrency можно сделать val, она не меняется.
        var coinbaseCurrency = coinbaseClient.getRates(input.id)

        // todo: Есть более элегантные и иммутабельные способы создать список.
        var currencies = arrayOf<Currency>()

        input.currencies.forEach({
            // fixme: rate можно сделать val, она не меняется.
            var rate = coinbaseCurrency.rates[it]?.toFloat()
            // todo: Оператор `!==` сравнивает указатели на объекты, т.е. содержится ли в переменных один и тот же
            //       инстанс объекта. В данном случае это не критично, т.к. идет сравнение с null, но в других случаях
            //       стоит использовать `!=`.
            //TODO: must be null
            if (rate !== null) {
                // fixme: Т.к. массивы иммутабельны по определению, эта операция будет каждый раз создавать новый массив
                //        копируя в него содержимое "предыдущей версии", что не оптимально.
                currencies += Currency(it, input.price * rate)
            }
        })

        return currencies
    }
}
