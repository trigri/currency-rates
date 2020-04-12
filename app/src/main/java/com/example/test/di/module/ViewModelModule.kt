package com.example.test.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.ui.main.MainViewModel
import com.example.test.utils.ViewModelFactory
import com.example.test.utils.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ActivityModule::class])
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMyViewModel(mainViewModel: MainViewModel): ViewModel
}