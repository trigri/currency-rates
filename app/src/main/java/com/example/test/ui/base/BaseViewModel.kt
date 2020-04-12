package com.example.test.ui.base

import androidx.lifecycle.ViewModel
import com.example.test.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

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

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}