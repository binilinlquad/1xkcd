package com.gandan.a1xkcd.ui

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class PageGoToButtonHandler(
    private val context: Context,
    totalPages: Int,
    private val scroller: (Int) -> Unit
) : GoToButtonHandler {
    private var pages: Array<String>

    init {
        if (totalPages < 1) throw IllegalArgumentException("Total page should bigger than 0 but $totalPages")

        pages = (totalPages downTo 1).map { "$it" }.toTypedArray()
    }

    override fun onClick() {
        AlertDialog.Builder(context)
            .setItems(pages) { dialog: DialogInterface, whichPage: Int ->
                scroller(whichPage)
                dialog.dismiss()
            }
            .show()
    }

}