package com.hfjs.kotlin_mapuse.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

class MainEntity : MultiItemEntity {
    var title: Int = 0

    var type: Int = 1

    constructor(name: Int) {
        this.title = name
        this.type = 0
    }

    constructor(name: Int, clazz: Class<*>) {
        this.title = name
        this.clazz = clazz
    }

    companion object {
        val TYPE_TITLE = 0
        val TYPE_CONTENT = 1
    }

    override fun getItemType(): Int {
        return type
    }

    private var clazz: Class<*>? = null
    fun getClazz(): Class<*>? {
        return clazz
    }

    fun setClazz(clazz: Class<*>) {
        this.clazz = clazz
    }
}