package com.gandan.a1xkcd.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.gandan.a1xkcd.R

class DisabledGoToButtonHandler(private val context: Context) : GoToButtonHandler {

    override fun onClick() {
        AlertDialog.Builder(context)
            .setMessage(R.string.goto_disable)
            .show()
    }
}