package com.currency.rates.di.module

import com.currency.data.currency.repository.CurrencyRepository
import com.currency.data.currency.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module

@Module(includes = [AppModule::class])
abstract class RepositoryModule {

    @Binds
    abstract fun bindCurrencyRepository(repository: CurrencyRepositoryImpl): CurrencyRepository
}