package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import java.io.File


class ShowImgActivity:BaseActivity(R.layout.activity_imageshow) {
    private var iv: ImageView? = null
    override fun initView() {
//        initToolbar(intent.getIntExtra("title", 0))
        iv = findViewById<View>(R.id.iv) as ImageView
        val bitmap =
            getDiskBitmap(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.png")
        if (bitmap != null) {
            iv!!.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this@ShowImgActivity, "没有图片", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDiskBitmap(pathString: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val file = File(pathString)
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString)
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }

        return bitmap
    }
}