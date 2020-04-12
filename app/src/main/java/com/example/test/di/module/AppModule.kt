package com.example.test.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.data.ApiClient
import com.example.test.utils.ViewModelFactory
import com.example.test.utils.rx.AppSchedulerProvider
import com.example.test.utils.rx.SchedulerProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://hiring.revolut.codes/api/"

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAppApiClient(client: OkHttpClient): ApiClient {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create()).build()
            .create(ApiClient::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)// Set connection timeout
            .readTimeout(60, TimeUnit.SECONDS)// Read timeout
            .writeTimeout(60, TimeUnit.SECONDS)// Write timeout
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Singleton
    @Provides
    fun provideSchedulers(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}