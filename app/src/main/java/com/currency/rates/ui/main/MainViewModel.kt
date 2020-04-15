package com.currency.rates.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.currency.data.currency.model.CurrencyRateModel
import com.currency.data.currency.usecase.CurrencyRatesUseCase
import com.currency.rates.ui.base.BaseViewModel
import com.currency.rates.utils.clearAndAddAll
import com.currency.rates.utils.rx.SchedulerProvider
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DELAY = 1L
private const val DEFAULT_CURRENCY = "EUR"

class MainViewModel @Inject constructor(
    private val currencyRatesUseCase: CurrencyRatesUseCase,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private val _currencyRates = MutableLiveData<MutableList<CurrencyItem>>()
    val currencyRates: LiveData<MutableList<CurrencyItem>> = _currencyRates

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private lateinit var currencyModel: CurrencyRateModel
    private var baseCurrency: String = DEFAULT_CURRENCY
    private var amount: Float = 1f
    private var currencyList = mutableListOf<CurrencyItem>()

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
        Observable.timer(DELAY, TimeUnit.SECONDS, schedulerProvider.computation())
            .repeat()
            .flatMap {
                currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(baseCurrency))
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ currencyRateModel ->
                _loading.postValue(false)
                handleResponse(currencyRateModel)
            }, { throwable ->
                _loading.postValue(false)
                _error.postValue(getError(throwable))
                println(throwable.toString())
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
            currencyList.add(0, CurrencyItem(baseCurrency, amount))
            _currencyRates.postValue(currencyList)
        }
    }

    private fun calculateRate(amount: Float) {
        synchronized(currencyList) {
            if (baseCurrency == currencyModel.baseCurrency) {
                currencyList.forEach {
                    if (it.currency == baseCurrency) {
                        it.rate = amount
                    } else {
                        it.rate = it.rate.times(amount)
                    }
                }
                _currencyRates.postValue(currencyList)
            }
        }
    }

    fun onBaseCurrencyChanged(baseCurrency: String?, amount: Float, list: List<CurrencyItem>) {
        dispose()
        this.amount = amount
        this.baseCurrency = baseCurrency ?: DEFAULT_CURRENCY
        currencyList.clearAndAddAll(list)
        reInitDisposableIfNeeded()
        getCurrencyRates()
    }

    private fun Map<String, Float>?.toCurrencyList(): List<CurrencyItem> {
        return this?.toList()?.map { (currency, rate) ->
            CurrencyItem(currency, rate * amount)
        } ?: mutableListOf()
    }

    private fun List<CurrencyItem>.sortMapToIdenticalList(map: Map<String, Float>?): List<CurrencyItem> {
        val newList = mutableListOf<CurrencyItem>()
        forEach { localCurrencyModel ->
            map?.let {
                if (it.contains(localCurrencyModel.currency)) {
                    val rate = it[localCurrencyModel.currency]?.times(amount)
                    newList.add(CurrencyItem(localCurrencyModel.currency, rate ?: 0f))
                }
            }
        }
        return newList
    }

    data class CurrencyItem(val currency: String?, var rate: Float = 1f)
}