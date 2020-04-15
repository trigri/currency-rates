package com.currency.data.currency.usecase

import com.currency.data.UseCase
import com.currency.data.currency.model.CurrencyRateModel
import com.currency.data.currency.model.toMappedModel
import com.currency.data.currency.repository.CurrencyRepository
import io.reactivex.Observable
import javax.inject.Inject

class CurrencyRatesUseCase @Inject constructor(private val currencyRepository: CurrencyRepository) :
    UseCase<CurrencyRatesUseCase.Arg, CurrencyRateModel> {

    data class Arg(val baseCurrency: String) : UseCase.Args()

    override fun get(args: Arg): Observable<CurrencyRateModel> {
        return currencyRepository.getCurrencyRates(args.baseCurrency)
            .map { currencyRateResponse ->
                currencyRateResponse.toMappedModel()
            }
    }
}