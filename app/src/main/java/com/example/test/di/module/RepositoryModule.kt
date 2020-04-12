package com.example.test.di.module

import com.example.data.currency.repository.CurrencyRepository
import com.example.data.currency.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module

@Module(includes = [AppModule::class])
abstract class RepositoryModule {

    @Binds
    abstract fun bindCurrencyRepository(repository: CurrencyRepositoryImpl): CurrencyRepository
}