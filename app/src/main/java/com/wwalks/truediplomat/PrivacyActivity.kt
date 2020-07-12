package com.wwalks.truediplomat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView

import com.crashlytics.android.Crashlytics

import com.jaeger.library.StatusBarUtil

import io.fabric.sdk.android.Fabric

class PrivacyActivity : AppCompatActivity() {


    var back: ImageView? = null
    var title: TextView? = null
    var privacyContents: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getSupportActionBar().hide();
        StatusBarUtil.setTransparent(this@PrivacyActivity)

        setContentView(R.layout.privacy_page)
        Fabric.with(this, Crashlytics())



        back = findViewById<View>(R.id.backButton2) as ImageView

        title = findViewById<View>(R.id.title2) as TextView
        privacyContents = findViewById<View>(R.id.prefLangTitle) as TextView

        title!!.typeface = MyFonts.getFont(this@PrivacyActivity,
                MyFonts.GORDITALIGHT)
        privacyContents!!.typeface = MyFonts.getFont(this@PrivacyActivity,
                MyFonts.GORDITALIGHT)



        back!!.setOnClickListener { finish() }


    }

}
