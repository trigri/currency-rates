package com.example.data

import com.example.data.currency.model.CurrencyRatesResponseModel
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiClient {

    @GET("android/latest")
    fun getCurrencyRates(@Query("base") baseCurrency: String): Observable<CurrencyRatesResponseModel>
}