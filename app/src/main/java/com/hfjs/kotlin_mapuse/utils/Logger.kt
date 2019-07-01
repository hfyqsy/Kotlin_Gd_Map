package com.hfjs.kotlin_mapuse.utils

import android.util.Log

object Logger {

    private fun getFunctionName(): String? {
        val sts = Thread.currentThread().stackTrace ?: return null

        for (st in sts) {
            if (st.isNativeMethod) {
                continue
            }

            if (st.className == Thread::class.java.name) {
                continue
            }

            if (st.className == this.javaClass.name) {
                continue
            }

            return ("[" + Thread.currentThread().name + "(" + Thread.currentThread().id
                    + "): " + st.fileName + ":" + st.lineNumber + "]")
        }

        return null
    }

    private fun print(type: Int, any: Any, TAG: String = "Logger-->  ") {
        when (type) {
            Log.VERBOSE -> Log.v(TAG, getLogString(any))
            Log.INFO -> Log.i(TAG, getLogString(any))
            Log.DEBUG -> Log.d(TAG, getLogString(any))
            Log.WARN -> Log.w(TAG, getLogString(any))
            Log.ERROR -> Log.e(TAG, getLogString(any))
        }
    }

    private fun getLogString(any: Any): String {
        return getFunctionName() + any.toString()
    }

    fun e(any: Any) {
        print(Log.ERROR, any)
    }

    fun w(any: Any) {
        print(Log.WARN, any)
    }

    fun i(any: Any) {
        print(Log.INFO, any)
    }

    fun d(any: Any) {
        print(Log.DEBUG, any)
    }

    fun v(any: Any) {
        print(Log.VERBOSE, any)
    }


}