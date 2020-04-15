package com.currency.data

import com.currency.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiClient {

    @GET("android/latest")
    fun getCurrencyRates(@Query("base") baseCurrency: String): Observable<CurrencyRatesResponseModel>
}