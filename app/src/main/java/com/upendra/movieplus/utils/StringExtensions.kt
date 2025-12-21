package com.upendra.movieplus.utils

fun CharSequence?.toStringTrimmed(): String {
    return this?.toString()?.trim() ?: ""
}
