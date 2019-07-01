package com.hfjs.kotlin_mapuse.test

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import kotlinx.android.synthetic.main.include_recycler_view.*
import java.util.*

class TestActivity : BaseActivity(R.layout.activity_main) {
    //    private var mRecyclerView: RecyclerView? = null
    private lateinit var mAdapter: TestAdapter
    private val testBeans = ArrayList<TestBean>()

    override fun initView() {
        testBeans.add(TestBean("测试1 ", TestActivity::class.java))
        testBeans.add(TestBean("测试2 ", TestActivity::class.java))
        testBeans.add(TestBean("测试3 ", TestActivity::class.java))
//        mRecyclerView = findViewById(R.id.mRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = TestAdapter()
        mRecyclerView.adapter = mAdapter
        mAdapter.setNewData(testBeans)
    }


    override fun setListener() {
        mAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            startActivity(Intent(this, testBeans[position].getmClass()))
        }
    }
}
