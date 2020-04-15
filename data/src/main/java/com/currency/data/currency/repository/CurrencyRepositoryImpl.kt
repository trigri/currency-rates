package com.currency.data.currency.repository

import com.currency.data.ApiClient
import com.currency.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(private val api: ApiClient) :
    CurrencyRepository {

    override fun getCurrencyRates(baseCurrency: String): Observable<CurrencyRatesResponseModel> {
        return api.getCurrencyRates(baseCurrency)
    }
}