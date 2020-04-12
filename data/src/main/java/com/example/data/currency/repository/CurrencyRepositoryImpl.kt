package com.example.data.currency.repository

import com.example.data.ApiClient
import com.example.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(private val api: ApiClient) :
    CurrencyRepository {

    override fun getCurrencyRates(baseCurrency: String): Observable<CurrencyRatesResponseModel> {
        return api.getCurrencyRates(baseCurrency)
    }
}