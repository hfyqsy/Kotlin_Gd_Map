package com.hfjs.kotlin_mapuse.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun Context.toast(message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(message), duration).apply { show() }
}

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message.toString(), duration).apply { show() }
}

fun String.decode(): String {
    var decodeString = this
    if (decodeString.contains("&amp;")) {
        decodeString = decodeString.replace("&amp;", "&")
    }
    if (decodeString.contains("&quot;")) {
        decodeString = decodeString.replace("&quot;", "\"")
    }
    if (decodeString.contains("&gt;")) {
        decodeString = decodeString.replace("&gt;", ">")
    }
    if (decodeString.contains("&lt;")) {
        decodeString = decodeString.replace("&lt;", "<")
    }
    return decodeString
}

fun String.getSharedPreference(context: Context): SharedPreferences = context.getSharedPreferences(this, 0)