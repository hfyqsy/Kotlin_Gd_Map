package com.hfjs.kotlin_mapuse.ui.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.entity.MainEntity

class MainAdapter(data: MutableList<MainEntity>) : BaseMultiItemQuickAdapter<MainEntity, BaseViewHolder>(data) {

    init {
        addItemType(MainEntity.TYPE_TITLE, R.layout.item_text)
        addItemType(MainEntity.TYPE_CONTENT, R.layout.item_content)
    }
    override fun convert(helper: BaseViewHolder, item: MainEntity) {
        when(helper.itemViewType){
            MainEntity.TYPE_TITLE->helper.setText(R.id.mMainTitle, item.title)
            MainEntity.TYPE_CONTENT-> helper.apply {
                setText(R.id.content_title, item.title)
                setText(R.id.content_class,item.getClazz()!!.name)
                addOnClickListener(R.id.ll_content)
            }
        }

    }

}