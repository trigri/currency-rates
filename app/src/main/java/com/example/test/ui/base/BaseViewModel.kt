package com.example.test.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.test.utils.rx.SchedulerProvider
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
                val jsonError = JSONObject(errorBody.orEmpty())
                Log.e("BaseViewModel", "errorBody ===>$errorBody")
                if (jsonError.has("status_message")) {
                    jsonError.getString("status_message")
                } else {
                    "Please try again."
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