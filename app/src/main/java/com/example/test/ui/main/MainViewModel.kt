package com.example.test.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.data.currency.model.CurrencyModel
import com.example.data.currency.usecase.CurrencyRatesUseCase
import com.example.data.currency.model.CurrencyRateModel
import com.example.test.ui.base.BaseViewModel
import com.example.test.utils.rx.SchedulerProvider
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val currencyRatesUseCase: CurrencyRatesUseCase,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {


    private val _currencyRates = MutableLiveData<MutableList<CurrencyModel>>()
    val currencyRates = _currencyRates

    private lateinit var currencyModel: CurrencyRateModel
    private var baseCurrency: String = "EUR"
    private var amount: Float = 1f
    private var currencyList = mutableListOf<CurrencyModel>()

    fun onResume() {
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
        Observable.timer(1, TimeUnit.SECONDS)
            .repeat()
            .flatMap {
                currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(baseCurrency))
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ currencyRateModel ->
                Log.e(TAG, currencyRateModel.toString())
                handleResponse(currencyRateModel)
            }, { throwable ->
                Log.e(TAG, "getCurrencyRates", throwable)
            }).addToDisposable()
    }

    private fun handleResponse(currencyRateModel: CurrencyRateModel) {
        synchronized(currencyList) {
            currencyRateModel.currencyList
            currencyModel = currencyRateModel
            currencyModel.currencyList?.let { newCurrencyList ->
                newCurrencyList.forEachIndexed { index, newCurrencyItem ->
                    if (currencyList.size < index + 1) {
                        newCurrencyItem.currency
                    }
                }
                currencyList.clear()
                currencyList.addAll(newCurrencyList)
                currencyList.add(0, CurrencyModel(baseCurrency))
                calculateRate(amount)
            }
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

    fun onBaseCurrencyChanged(baseCurrency: String?, amount: Float) {
        this.amount = amount
        this.baseCurrency = baseCurrency ?: "EUR"
        dispose()
        reInitDisposableIfNeeded()
        getCurrencyRates()
    }
}