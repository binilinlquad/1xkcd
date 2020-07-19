package com.gandan.a1xkcd.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AlertDialog

class PageGoToButtonHandler(
    private val context: Context,
    totalPages: Int,
    private val scroller: (Int) -> Unit
) : GoToButtonHandler {
    private var pages: Array<String>
    private val style = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        android.R.style.ThemeOverlay_Material_Dialog_Alert
    } else {
        android.R.style.Theme_Material_Light_Dialog_NoActionBar
    }

    init {
        if (totalPages < 1) throw IllegalArgumentException("Total page should bigger than 0 but $totalPages")

        pages = (totalPages downTo 1).map { "$it" }.toTypedArray()
    }

    override fun onClick() {
        AlertDialog.Builder(context, style)
            .setItems(pages) { dialog: DialogInterface, whichPage: Int ->
                scroller(whichPage)
                dialog.dismiss()
            }
            .show()
    }

}