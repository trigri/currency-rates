package com.example.data.currency.repository

import com.example.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable

interface CurrencyRepository {
    fun getCurrencyRates(baseCurrency: String): Observable<CurrencyRatesResponseModel>
}