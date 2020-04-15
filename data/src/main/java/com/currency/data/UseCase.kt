package com.currency.data

import io.reactivex.Observable

interface UseCase<A : UseCase.Args, R : MappedModel> {
    fun get(args: A): Observable<R>

    open class Args
}