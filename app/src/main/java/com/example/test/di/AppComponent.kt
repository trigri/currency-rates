package com.example.test.di

import com.example.test.TestApp
import com.example.test.di.module.ActivityModule
import com.example.test.di.module.AppModule
import com.example.test.di.module.RepositoryModule
import com.example.test.di.module.ViewModelModule
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
interface AppComponent : AndroidInjector<TestApp> {

    override fun inject(instance: TestApp?)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: TestApp): Builder

        fun build(): AppComponent
    }
}