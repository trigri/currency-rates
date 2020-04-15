package com.currency.rates.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.currency.data.currency.model.CurrencyRateModel
import com.currency.data.currency.model.CurrencyRatesResponseModel
import com.currency.data.currency.repository.CurrencyRepository
import com.currency.data.currency.usecase.CurrencyRatesUseCase
import com.currency.rates.ui.main.MainViewModel.CurrencyItem
import com.currency.rates.utils.rx.TestSchedulerProvider
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val currency = "EUR"

    private lateinit var mainViewModel: MainViewModel
    private lateinit var currencyRatesUseCase: CurrencyRatesUseCase
    private lateinit var testSchedulerProvider: TestSchedulerProvider
    private val testScheduler = TestScheduler()

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    lateinit var observer: Observer<MutableList<CurrencyItem>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testSchedulerProvider = TestSchedulerProvider(testScheduler)
        currencyRatesUseCase = CurrencyRatesUseCase(currencyRepository)
    }


    @Test
    fun getCurrencies() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val currencyRateModel = CurrencyRateModel(currency, hashMapOf("BRL" to 4.71f))
        val currencyResponse = CurrencyRatesResponseModel(currency, hashMapOf("BRL" to 4.71f))

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))

//        whenever(currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(currency)))
//            .thenReturn(Observable.just(currencyRateModel))

        mainViewModel.currencyRates.observeForever(observer)

        mainViewModel.onResume()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assert(currencyRateModel.rates?.size?.plus(1) ?: 0 == mainViewModel.currencyRates.value!!.size)
    }
}