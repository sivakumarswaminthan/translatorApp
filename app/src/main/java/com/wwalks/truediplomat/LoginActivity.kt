package com.wwalks.truediplomat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.crashlytics.android.Crashlytics
import com.wwalks.truediplomat.R

import io.fabric.sdk.android.Fabric
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    private var editTextMobile: EditText? = null
    private var countryCode: EditText? = null
    var welcome: TextView? = null
    var support: TextView? = null
    var sms: TextView? = null
    var carrier: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Fabric.with(this, Crashlytics())

        editTextMobile = findViewById(R.id.editTextMobile) as EditText
        countryCode = findViewById(R.id.countryCode) as EditText
        welcome = findViewById<TextView>(R.id.welcomeText) as TextView
        support = findViewById<TextView>(R.id.supportText) as TextView
        sms = findViewById<TextView>(R.id.sms) as TextView
        carrier = findViewById<TextView>(R.id.carrier) as TextView

        welcome!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITALIGHT)
        support!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITAREGULAR)
        sms!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITAREGULAR)
        carrier!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITAREGULAR)
        editTextMobile!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITAREGULAR)
        countryCode!!.typeface = MyFonts.getFont(this@LoginActivity,
                MyFonts.GORDITAREGULAR)

        findViewById<View>(R.id.btnLanguage1).setOnClickListener(View.OnClickListener {
            var mobile = editTextMobile!!.text.toString().trim { it <= ' ' }
            val cc = countryCode!!.text.toString().trim { it <= ' ' }

            if (mobile.isEmpty() || mobile.length < 5) {
                editTextMobile!!.error = "Enter a valid mobile"
                editTextMobile!!.requestFocus()
                return@OnClickListener
            }

            if (cc.isEmpty()) {
                countryCode!!.error = "Enter your country code"
                countryCode!!.requestFocus()
                return@OnClickListener
            }
            Log.e("phone", cc + mobile)
            mobile = cc + mobile
            val intent = Intent(this@LoginActivity, VerifyPhoneActivity::class.java)
            intent.putExtra("mobile", mobile)
            intent.putExtra("cc", cc)
            startActivity(intent)
        })

        findViewById<View>(R.id.loginLayout).setOnClickListener(View.OnClickListener {
            var mobile = editTextMobile!!.text.toString().trim { it <= ' ' }
            val cc = countryCode!!.text.toString().trim { it <= ' ' }

            if (mobile.isEmpty() || mobile.length < 5) {
                editTextMobile!!.error = "Enter a valid mobile"
                editTextMobile!!.requestFocus()
                return@OnClickListener
            }

            if (cc.isEmpty()) {
                countryCode!!.error = "Enter your country code"
                countryCode!!.requestFocus()
                return@OnClickListener
            }
            Log.e("phone", cc + mobile)
            mobile = cc + mobile
            val intent = Intent(this@LoginActivity, VerifyPhoneActivity::class.java)
            intent.putExtra("mobile", mobile)
            intent.putExtra("cc", cc)
            startActivity(intent)
        })


    }
}
