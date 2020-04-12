package com.example.data.currency.model

import com.example.data.MappedModel

data class CurrencyRateModel(
    val baseCurrency: String?, val currencyList: List<CurrencyModel>?
) : MappedModel()

data class CurrencyModel(
    val currency: String?,
    var rate: Float = 1f,
    val currencyName: String?=null
) : MappedModel()