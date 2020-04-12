package com.example.data.currency.usecase

import com.example.data.UseCase
import com.example.data.currency.model.CurrencyModel
import com.example.data.currency.model.CurrencyRateModel
import com.example.data.currency.repository.CurrencyRepository
import io.reactivex.Observable
import javax.inject.Inject

class CurrencyRatesUseCase @Inject constructor(private val currencyRepository: CurrencyRepository) :
    UseCase<CurrencyRatesUseCase.Arg, CurrencyRateModel> {

    data class Arg(val baseCurrency: String) : UseCase.Args()

    override fun get(args: Arg): Observable<CurrencyRateModel> {
        return currencyRepository.getCurrencyRates(args.baseCurrency)
            .map { currencyRateResponse ->
                val list = currencyRateResponse.rates?.toList()?.map { pair ->
                    val (currency, rate) = pair
                    CurrencyModel(currency, rate)
                }
                CurrencyRateModel(currencyRateResponse.baseCurrency, list)
            }
    }
}