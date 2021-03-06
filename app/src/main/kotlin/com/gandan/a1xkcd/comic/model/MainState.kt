package com.gandan.a1xkcd.comic.model

import com.gandan.a1xkcd.comic.model.MainMsg.*
import com.gandan.a1xkcd.comic.model.MainState.*

sealed class MainState {
    object Initial : MainState()
    class ShowComic(val totalPages: Int) : MainState()
    object Empty : MainState()
    object Refresh : MainState()
    class Error(val error: Throwable) : MainState()
}

sealed class MainMsg {
    class ErrorMsg(val error: Throwable) : MainMsg()
    object LoadingMsg : MainMsg()
    class SuccessMsg(val totalPages: Int) : MainMsg()
}

fun reduce(state: MainState, msg: MainMsg): MainState {
    if (msg is ErrorMsg) return Error(msg.error)

    return when (state) {
        Initial -> Refresh
        is ShowComic -> when (msg) {
            LoadingMsg -> Refresh
            else -> throw UnsupportedOperationException("Forget to implement transition state $state with $msg ?")
        }
        is Refresh -> when (msg) {
            is SuccessMsg -> if (msg.totalPages > 0) ShowComic(msg.totalPages) else Empty
            else -> throw UnsupportedOperationException("Forget to implement transition state $state with $msg ?")
        }
        is Error -> when (msg) {
            is SuccessMsg -> ShowComic(msg.totalPages)
            is ErrorMsg -> Error(msg.error)
            else -> state
        }
        else -> throw UnsupportedOperationException("Forget to implement transition state $state with $msg ?")
    }
}
