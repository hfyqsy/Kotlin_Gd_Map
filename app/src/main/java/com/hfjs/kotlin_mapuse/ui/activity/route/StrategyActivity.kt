package com.hfjs.kotlin_mapuse.ui.activity.route

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.entity.StrategyEntity
import com.hfjs.kotlin_mapuse.utils.Logger
import kotlinx.android.synthetic.main.activity_strategy.*

class StrategyActivity : BaseActivity(R.layout.activity_strategy) {
    private lateinit var strategy: StrategyEntity
    override fun initView() {
        initToolbar(intent.getIntExtra("title", 0))
        strategy = intent.getSerializableExtra("strategy") as StrategyEntity
        ivItemCon.isSelected=strategy.congestion
        ivItemCost.isSelected=strategy.cost
        ivItemAvoid.isSelected=strategy.avoidHeightSpeed
        ivItemSpeed.isSelected=strategy.heightSpeed
    }

    override fun setListener() {
        ivItemCon.setOnClickListener {
            ivItemCon.isSelected = !ivItemCon.isSelected
            strategy.congestion=ivItemCon.isSelected
        }
        ivItemCost.setOnClickListener {
            ivItemCost.isSelected = !ivItemCost.isSelected
            strategy.cost=ivItemCost.isSelected
        }
        ivItemAvoid.setOnClickListener {
            ivItemAvoid.isSelected = !ivItemAvoid.isSelected
            strategy.avoidHeightSpeed=ivItemAvoid.isSelected
        }
        ivItemSpeed.setOnClickListener {
            ivItemSpeed.isSelected = !ivItemSpeed.isSelected
            strategy.heightSpeed=ivItemSpeed.isSelected
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(1002,Intent().putExtra("strategy",strategy))
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            setResult(1002,Intent().putExtra("strategy",strategy))
        }
        return super.onOptionsItemSelected(item)
    }

}