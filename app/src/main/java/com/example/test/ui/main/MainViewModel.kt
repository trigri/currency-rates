package com.example.test.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.data.currency.model.CurrencyModel
import com.example.data.currency.model.CurrencyRateModel
import com.example.data.currency.usecase.CurrencyRatesUseCase
import com.example.test.ui.base.BaseViewModel
import com.example.test.utils.clearAndAddAll
import com.example.test.utils.rx.SchedulerProvider
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DELAY = 1L

class MainViewModel @Inject constructor(
    private val currencyRatesUseCase: CurrencyRatesUseCase,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private val _currencyRates = MutableLiveData<MutableList<CurrencyModel>>()
    val currencyRates: LiveData<MutableList<CurrencyModel>> = _currencyRates

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private lateinit var currencyModel: CurrencyRateModel
    private var baseCurrency: String = "EUR"
    private var amount: Float = 1f
    private var currencyList = mutableListOf<CurrencyModel>()

    fun onResume() {
        _loading.postValue(true)
        reInitDisposableIfNeeded()
        getCurrencyRates()
    }

    fun onStop() {
        dispose()
    }

    fun onBaseAmountChanged(amount: Float) {
        this.amount = amount
        calculateRate(amount)
    }

    private fun getCurrencyRates() {
        Observable.timer(DELAY, TimeUnit.SECONDS)
            .repeat()
            .flatMap {
                currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(baseCurrency))
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ currencyRateModel ->
                _loading.postValue(false)
                Log.e(TAG, "$currencyRateModel")
                handleResponse(currencyRateModel)
            }, { throwable ->
                _loading.postValue(false)
                _error.postValue(getError(throwable))
                Log.e(TAG, "getCurrencyRates", throwable)
            }).addToDisposable()
    }

    private fun handleResponse(currencyRateModel: CurrencyRateModel) {
        synchronized(currencyList) {
            currencyModel = currencyRateModel
            val list = if (currencyList.isNullOrEmpty().not()) {
                currencyList.removeAt(0)
                currencyList.sortMapToIdenticalList(currencyModel.rates)
            } else {
                currencyModel.rates.toCurrencyList()
            }

            currencyList.clearAndAddAll(list)
            currencyList.add(0, CurrencyModel(baseCurrency))
            calculateRate(amount)
        }
    }

    private fun calculateRate(amount: Float) {
        synchronized(currencyList) {
            if (baseCurrency == currencyModel.baseCurrency) {
                currencyList.forEach {
                    if (it.currency == baseCurrency) {
                        it.rate = amount
                    } else {
                        it.rate = it.rate * amount
                    }
                }
                _currencyRates.postValue(currencyList)
            }
        }
    }

    fun onBaseCurrencyChanged(baseCurrency: String?, amount: Float, list: List<CurrencyModel>) {
        dispose()
        this.amount = amount
        this.baseCurrency = baseCurrency ?: "EUR"
        currencyList.clearAndAddAll(list)
        reInitDisposableIfNeeded()
        getCurrencyRates()
    }
}

fun Map<String, Float>?.toCurrencyList(): List<CurrencyModel> {
    return this?.toList()?.map { (currency, rate) ->
        CurrencyModel(currency, rate)
    } ?: mutableListOf()
}

fun List<CurrencyModel>.sortMapToIdenticalList(map: Map<String, Float>?): List<CurrencyModel> {
    val newList = mutableListOf<CurrencyModel>()
    forEach { localCurrencyModel ->
        map?.let {
            if (it.contains(localCurrencyModel.currency)) {
                newList.add(
                    CurrencyModel(
                        localCurrencyModel.currency, it[localCurrencyModel.currency] ?: 0f
                    )
                )
            }
        }
    }
    return newList
}