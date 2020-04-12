package com.example.test.di.module

import com.example.test.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}