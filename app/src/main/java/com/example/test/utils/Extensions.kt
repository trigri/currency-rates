package com.example.test.utils

import android.content.Context
import android.text.InputFilter
import android.widget.EditText

fun String?.getFlagResource(context: Context): Int {
    val flag = "flag_${this?.toLowerCase()}"
    return context.resources.getIdentifier(flag, "drawable", context.packageName)
}


fun Float?.round(decimals: Int = 2): String {
    return this?.let {
        if (this != 0f) {
            "%.${decimals}f".format(this)
        } else {
            "0"
        }
    } ?: "0"
}

fun EditText.maxLength(length: Int) {
    filters = arrayOf(InputFilter.LengthFilter(length))
}

fun <T> MutableList<T>.clearAndAddAll(list: List<T>) {
    clear()
    addAll(list)
}