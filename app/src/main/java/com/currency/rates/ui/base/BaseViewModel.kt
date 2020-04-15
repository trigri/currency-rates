package com.currency.rates.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.currency.data.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

open class BaseViewModel(val schedulerProvider: SchedulerProvider) : ViewModel() {

    val TAG = this::class.java.simpleName

    private var compositeDisposable = CompositeDisposable()

    protected fun Disposable.addToDisposable() = compositeDisposable.add(this)

    protected fun reInitDisposableIfNeeded() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    protected fun dispose() {
        compositeDisposable.dispose()
    }

    protected fun getError(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                Log.e(TAG, "errorBody ===>$errorBody")
                if (errorBody?.startsWith("{") == true && errorBody.endsWith("}")) {
                    val jsonError = JSONObject(errorBody.orEmpty())
                    if (jsonError.has("status_message")) {
                        jsonError.getString("status_message")
                    } else {
                        "Please try again."
                    }
                } else {
                    errorBody.toString()
                }
            }
            is SocketTimeoutException -> "Request timeout error."
            is IOException -> "Check your network connection."
            else -> "Unknown error occurred."
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}