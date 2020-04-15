package com.currency.rates.di

import com.currency.rates.CurrencyRateApp
import com.currency.rates.di.module.ActivityModule
import com.currency.rates.di.module.AppModule
import com.currency.rates.di.module.RepositoryModule
import com.currency.rates.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        AppModule::class,
        RepositoryModule::class]
)
interface AppComponent : AndroidInjector<CurrencyRateApp> {

    override fun inject(instance: CurrencyRateApp?)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: CurrencyRateApp): Builder

        fun build(): AppComponent
    }
}