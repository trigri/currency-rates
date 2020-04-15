package com.currency.data.currency.model

import com.currency.data.MappedModel

data class CurrencyRateModel(val baseCurrency: String?, val rates: Map<String, Float>?) :
    MappedModel()

data class CurrencyModel(val currency: String?, var rate: Float = 1f)