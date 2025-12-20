package com.upendra.movieplus.utils

import android.text.Editable

fun Editable.toStringTrimmed(): String {
    return this.toString().trim()
}