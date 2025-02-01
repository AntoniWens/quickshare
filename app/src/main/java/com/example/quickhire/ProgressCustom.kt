package com.example.quickhire

import android.app.Dialog
import android.content.Context

class ProgressCustom(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.loading)
    }
}