package com.gandan.a1xkcd.ui

interface FetchListener {
    fun onError(error: Throwable)

    fun onSuccess(totalPages: Int)

    // simple creator
    companion object {
        fun create(
            onSuccess: (Int) -> Unit,
            onError: (Throwable) -> Unit
        ): FetchListener {
            return object : FetchListener {
                override fun onError(error: Throwable) {
                    onError(error)
                }

                override fun onSuccess(totalPages: Int) {
                    onSuccess(totalPages)
                }

            }
        }
    }
}
