package com.hfjs.kotlin_mapuse.test

class TestBean(name: String, mClass: Class<*>) {
    var name: String? = name
    private var mClass: Class<*>? = mClass

    fun getmClass(): Class<*>? {
        return mClass
    }

    fun setmClass(mClass: Class<*>) {
        this.mClass = mClass
    }
}
