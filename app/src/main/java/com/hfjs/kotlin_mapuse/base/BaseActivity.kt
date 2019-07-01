package com.hfjs.kotlin_mapuse.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.gyf.immersionbar.ktx.immersionBar
import com.hfjs.kotlin_mapuse.AppManger.ActivityManger
import com.hfjs.kotlin_mapuse.R
import kotlinx.android.synthetic.main.include_toolbar.*

open class BaseActivity(@LayoutRes open val layoutRes: Int) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManger.getInstance().addActivity(this)
        setContentView(layoutRes)
        initImmersionBar()
        initView(savedInstanceState)
        initView()
        initData()
        setListener()
    }

    /**
     * 设置监听事件
     */
    protected open fun setListener() {

    }

    /**
     * 初始化标题栏状态栏
     */
    protected open fun initImmersionBar() {
        immersionBar {
            if (mToolbar!=null) titleBar(mToolbar)
        }
    }

    /**
     * 初始化界面
     */
    protected open fun initView() {}

    /**
     * 初始化界面
     */
    protected open fun initView(bundle: Bundle?) {}

    /**
     * 初始化列表
     */
    protected open fun initRecycleView() {}

    /**
     * 加载数据
     */
    protected open fun initData() {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 跳转Activity
     */
    protected fun startActivity(clazz: Class<*>?) {
        startActivity(Intent(this, clazz))
    }

    /**
     * 跳转Activity
     */
    protected fun startIntentActivity(clazz: Class<*>?, titleId: Int) {
        startActivity(Intent(this, clazz).putExtra("title", titleId))
    }

    /**
     * 设置标题
     */
    protected fun initToolbar(title: String) {
        mToolbar!!.title = ""
        mToolbarTitle!!.text = title
        setSupportActionBar(mToolbar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_white)
    }

    /**
     * 设置标题
     */
    protected fun initToolbar(titleId: Int, backImgId: Int) {
        mToolbar!!.title = ""
        mToolbarTitle!!.text = getString(titleId)
        setSupportActionBar(mToolbar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(backImgId)
    }

    /**
     * 设置标题
     */
    protected fun initToolbar(titleId: Int) {
        initToolbar(getString(titleId))
    }

    /**
     * 销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        ActivityManger.getInstance().removeActivity(this)
    }
}