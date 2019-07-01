package com.hfjs.kotlin_mapuse.test

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R

class TestAdapter : BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.item_text) {

    override fun convert(helper: BaseViewHolder, item: TestBean) {
        helper.setText(R.id.mMainTitle, item.name)
    }
}
