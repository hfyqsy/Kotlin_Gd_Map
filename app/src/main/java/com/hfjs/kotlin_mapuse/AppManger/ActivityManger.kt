package com.hfjs.kotlin_mapuse.AppManger

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.util.*

class ActivityManger {

    private object Holder {
        val mActivities = Stack<Activity>()
        val INSTANCE = ActivityManger()
    }


    companion object {
        fun getInstance(): ActivityManger {
            return Holder.INSTANCE
        }
    }

    fun addActivity(activity: Activity) {
        Holder.mActivities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        hideSoftKeyBoard(activity)
        Holder.mActivities.remove(activity)
    }

    fun removeAllActivity() {
        for (activity: Activity in Holder.mActivities) {
            hideSoftKeyBoard(activity)
            activity.finish()
        }
        Holder.mActivities.clear()
    }

    fun <T : Activity> hasActivity(tClass: Class<T>): Boolean {
        for (activity in Holder.mActivities) {
            if (tClass.name == activity.javaClass.getName()) {
                return !activity.isDestroyed() || !activity.isFinishing()
            }
        }
        return false
    }

    fun hideSoftKeyBoard(activity: Activity) {
        val localView = activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (localView != null) {
            imm.hideSoftInputFromWindow(localView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}


