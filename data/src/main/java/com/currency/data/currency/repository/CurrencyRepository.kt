package com.currency.data.currency.repository

import com.currency.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable

interface CurrencyRepository {
    fun getCurrencyRates(baseCurrency: String): Observable<CurrencyRatesResponseModel>
}