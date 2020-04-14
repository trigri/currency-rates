package com.example.data.currency.model

import com.example.data.MappedModel

data class CurrencyRateModel(val baseCurrency: String?, val rates: Map<String, Float>?) :
    MappedModel()

data class CurrencyModel(val currency: String?, var rate: Float = 1f)