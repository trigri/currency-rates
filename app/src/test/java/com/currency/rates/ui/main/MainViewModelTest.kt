package com.currency.rates.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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

    @Mock
    lateinit var errorObserver: Observer<String>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testSchedulerProvider = TestSchedulerProvider(testScheduler)
        currencyRatesUseCase = CurrencyRatesUseCase(currencyRepository)
    }

    @Test
    fun `WHEN baseCurrency is provided THEN return list of currencies`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val currencyResponse = getCurrencyRatesResponseModel(currency)

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))

        mainViewModel.currencyRates.observeForever(observer)

        mainViewModel.onResume()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assert(mainViewModel.currencyRates.value!!.isNullOrEmpty().not())
    }

    @Test
    fun `WHEN baseCurrency is invalid THEN return error`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        whenever(currencyRepository.getCurrencyRates(""))
            .thenReturn(Observable.error(Throwable()))

        mainViewModel.onResume()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        mainViewModel.error.observeForever(errorObserver)

        assert(mainViewModel.error.value.isNullOrEmpty().not())
    }


    @Test
    fun `WHEN currency list received successfully THEN add base currency entry`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val currencyResponse = getCurrencyRatesResponseModel(currency)

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))
        mainViewModel.currencyRates.observeForever(observer)
        mainViewModel.onResume()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        assert(currencyResponse.rates?.size?.plus(1) ?: 0 == mainViewModel.currencyRates.value!!.size)
    }

    @Test
    fun `WHEN amount changed THEN base item rate does not change`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val baseAmount = 2f

        val currencyResponse = getCurrencyRatesResponseModel(currency)
        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))

        mainViewModel.currencyRates.observeForever(observer)
        mainViewModel.onResume()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        mainViewModel.currencyRates.observeForever(observer)
        assert(mainViewModel.currencyRates.value.isNullOrEmpty().not())

        mainViewModel.onBaseAmountChanged(baseAmount)

        assert(mainViewModel.currencyRates.value!![0].rate == baseAmount)
    }

    @Test
    fun `WHEN amount changed THEN update list with rates multiplied with new amount`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val baseAmount = 2f

        val currencyResponse = getCurrencyRatesResponseModel(currency)

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))

        mainViewModel.currencyRates.observeForever(observer)
        mainViewModel.onResume()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        mainViewModel.currencyRates.observeForever(observer)
        assert(mainViewModel.currencyRates.value.isNullOrEmpty().not())

        mainViewModel.onBaseAmountChanged(baseAmount)

        assert(mainViewModel.currencyRates.value!![1].rate == (baseAmount * 4.71f))
    }

    @Test
    fun `WHEN base currency changed THEN return with currency list with new base currency`() {
        mainViewModel = MainViewModel(currencyRatesUseCase, testSchedulerProvider)
        val currencyResponse = getCurrencyRatesResponseModel(currency)

        val newBaseCurrency = "CNY"

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(currencyResponse))
        whenever(currencyRepository.getCurrencyRates(newBaseCurrency))
            .thenReturn(Observable.just(currencyResponse))


        mainViewModel.currencyRates.observeForever(observer)
        mainViewModel.onResume()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        assert(mainViewModel.currencyRates.value!![0].currency == currency)

        mainViewModel.onBaseCurrencyChanged(newBaseCurrency, 3f, getCurrencyItemList())
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        assert(mainViewModel.currencyRates.value!![0].currency == newBaseCurrency)
    }

    private fun getCurrencyItemList(): List<CurrencyItem> {
        return listOf(CurrencyItem("EUR", 2f), CurrencyItem("AUD", 0.7f), CurrencyItem("BGN", 1.5f))
    }

    private fun getCurrencyRatesResponseModel(currency: String): CurrencyRatesResponseModel {
        return CurrencyRatesResponseModel(currency, hashMapOf("BRL" to 4.71f, "AUD" to 4.71f))
    }
}