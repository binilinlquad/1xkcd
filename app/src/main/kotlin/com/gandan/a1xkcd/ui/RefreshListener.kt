package com.gandan.a1xkcd.ui

interface RefreshListener {
    fun onError(error: Throwable)

    fun onRefreshed(totalPages: Int)

    // simple creator
    companion object {
        fun create(
            refreshHandler: (Int) -> Unit,
            errorHandler: (Throwable) -> Unit
        ): RefreshListener {
            return object : RefreshListener {
                override fun onError(error: Throwable) {
                    errorHandler(error)
                }

                override fun onRefreshed(totalPages: Int) {
                    refreshHandler(totalPages)
                }

            }
        }
    }
}
