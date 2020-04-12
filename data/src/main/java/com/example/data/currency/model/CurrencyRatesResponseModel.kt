package com.example.data.currency.model

import com.example.data.MappedModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRatesResponseModel(
    val baseCurrency: String? = null,
    val rates: Map<String, Float>? = null
)