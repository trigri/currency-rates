package com.currency.data.currency.usecase

import com.currency.data.currency.model.CurrencyRateModel
import com.currency.data.currency.model.CurrencyRatesResponseModel
import com.currency.data.currency.repository.CurrencyRepository
import com.currency.data.rx.SchedulerProvider
import com.currency.data.rx.TrampolineSchedulerProvider
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CurrencyRatesUseCaseTest {

    private val currency = "EUR"

    private lateinit var currencyRatesUseCase: CurrencyRatesUseCase

    private lateinit var schedulerProvider: SchedulerProvider

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()
        currencyRatesUseCase = CurrencyRatesUseCase(currencyRepository)
    }


    @Test
    fun `WHEN valid currency is passed as argument THEN return success`() {
        val testObserver = TestObserver<CurrencyRateModel>()

        whenever(currencyRepository.getCurrencyRates(currency))
            .thenReturn(Observable.just(getCurrencyRatesResponseModel(currency)))

        currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(currency))
            .subscribeOn(schedulerProvider.io())
            .subscribe(testObserver)

        testObserver.assertValue(getCurrencyRateModel(currency))
    }

    @Test
    fun `WHEN invalid currency is passed as argument THEN return error`() {
        val invalidCurrency = ""
        val testObserver = TestObserver<CurrencyRateModel>()
        val error = Throwable()

        whenever(currencyRepository.getCurrencyRates(invalidCurrency))
            .thenReturn(Observable.error(error))

        currencyRatesUseCase.get(CurrencyRatesUseCase.Arg(invalidCurrency))
            .subscribeOn(schedulerProvider.io())
            .subscribe(testObserver)

        testObserver.assertError(error)
    }

    private fun getCurrencyRateModel(currency: String): CurrencyRateModel {
        return CurrencyRateModel(currency, getRatesMap())
    }

    private fun getCurrencyRatesResponseModel(currency: String): CurrencyRatesResponseModel {
        return CurrencyRatesResponseModel(currency, getRatesMap())
    }

    private fun getRatesMap(): Map<String, Float> {
        return hashMapOf("BRL" to 4.71f, "AUD" to 4.71f)
    }
}