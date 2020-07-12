package com.wwalks.truediplomat

import android.content.Context

import android.content.Intent
import android.os.Bundle

import com.wwalks.truediplomat.R
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.broooapps.otpedittext2.OtpEditText
import com.crashlytics.android.Crashlytics
import com.wwalks.truediplomat.translator.TranslatorActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.TimeUnit

import io.fabric.sdk.android.Fabric

class VerifyPhoneActivity : AppCompatActivity() {

    private var mVerificationId: String? = null

    var editTextCode: OtpEditText ? =null


    private var mAuth: FirebaseAuth? = null

    var languageCode: String? = null
    var languageCode1: MutableList<String> = ArrayList()
    var myPos: Int = 0

    var verify: TextView? = null
    var supp: TextView? = null


    //the callback to detect the verification status
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            Log.e("Phone auth", "" + phoneAuthCredential)
            signInWithPhoneAuthCredential(phoneAuthCredential)

            /*
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //            //in this case the code will be null
            //            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
            */
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@VerifyPhoneActivity, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)
            Log.e("Code", " sent")

            //storing the verification id that is sent to the user
            mVerificationId = s
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        Fabric.with(this, Crashlytics())

        //initializing objects
        mAuth = FirebaseAuth.getInstance()
        editTextCode = findViewById(R.id.editTextCode)
        verify = findViewById(R.id.verifyCode)
        supp = findViewById(R.id.supp)

        verify!!.typeface = MyFonts.getFont(this@VerifyPhoneActivity,
                MyFonts.GORDITALIGHT)
        supp!!.typeface = MyFonts.getFont(this@VerifyPhoneActivity,
                MyFonts.GORDITAREGULAR)
        editTextCode!!.typeface = MyFonts.getFont(this@VerifyPhoneActivity,
                MyFonts.GORDITAREGULAR)
        //editTextCode = findViewById(R.id.editTextCode);


        val intent = intent
        val mobile = intent.getStringExtra("mobile")
        mobileNumber = mobile
        Log.e("mobile confirm", mobile)
        sendVerificationCode(mobile)

        findViewById<View>(R.id.verifyLayout).setOnClickListener(View.OnClickListener {
            val code = editTextCode!!.text!!.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                editTextCode!!.error = "Enter valid code"
                editTextCode!!.requestFocus()
                return@OnClickListener
            }

            verifyVerificationCode(code)
        })

    }

    private fun sendVerificationCode(mobile: String) {
        Log.e("inside ", "block")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks)

    }


    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this@VerifyPhoneActivity) { task ->
                    if (task.isSuccessful) {
                        //verification successful we will start the profile activity
                        val cx = this@VerifyPhoneActivity

                        val intent = intent
                        TranslatorActivity.setSharedPrefs(cx, "myID", mobileNumber!!)
                        TranslatorActivity.setSharedPrefs(cx, "loginID", "phone")
                        TranslatorActivity.setSharedPrefs(cx, "fcmToken", mAuth!!.uid!!)

                        languageSet()


                    } else {

                        //verification unsuccessful.. display an error message
                        var message = "Something is wrong, we will fix it soon..."

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }

                        val snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG)
                        snackbar.setAction("Dismiss") { }
                        snackbar.show()
                    }
                }
    }

    fun languageSet() {
        val cr = this

        languageCode1.add("af")
        languageCode1.add("sq")
        languageCode1.add("ar")
        languageCode1.add("az")
        languageCode1.add("eu")
        languageCode1.add("bn")
        languageCode1.add("bs")
        languageCode1.add("ca")
        languageCode1.add("ceb")
        languageCode1.add("zh")
        languageCode1.add("zh-TW")
        languageCode1.add("co")
        languageCode1.add("hr")
        languageCode1.add("cs")
        languageCode1.add("da")
        languageCode1.add("nl")
        languageCode1.add("en")
        languageCode1.add("eo")
        languageCode1.add("et")
        languageCode1.add("fil")
        languageCode1.add("fi")
        languageCode1.add("fr")
        languageCode1.add("fy")
        languageCode1.add("gl")
        languageCode1.add("de")
        languageCode1.add("el")
        languageCode1.add("ht")
        languageCode1.add("ha")
        languageCode1.add("haw")
        languageCode1.add("hi")
        languageCode1.add("hu")
        languageCode1.add("is")
        languageCode1.add("id")
        languageCode1.add("ig")
        languageCode1.add("it")
        languageCode1.add("ja")
        languageCode1.add("jw")
        languageCode1.add("km")
        languageCode1.add("ko")
        languageCode1.add("ku")
        languageCode1.add("la")
        languageCode1.add("lv")
        languageCode1.add("lt")
        languageCode1.add("lb")
        languageCode1.add("mg")
        languageCode1.add("ms")
        languageCode1.add("mt")
        languageCode1.add("mi")
        languageCode1.add("ne")
        languageCode1.add("no")
        languageCode1.add("ny")
        languageCode1.add("pl")
        languageCode1.add("pt")
        languageCode1.add("ro")
        languageCode1.add("ru")
        languageCode1.add("sm")
        languageCode1.add("gd")
        languageCode1.add("sr")
        languageCode1.add("st")
        languageCode1.add("sn")
        languageCode1.add("si")
        languageCode1.add("sk")
        languageCode1.add("sl")
        languageCode1.add("so")
        languageCode1.add("es")
        languageCode1.add("su")
        languageCode1.add("sw")
        languageCode1.add("sv")
        languageCode1.add("tl")
        languageCode1.add("ta")
        languageCode1.add("th")
        languageCode1.add("tr")
        languageCode1.add("uk")
        languageCode1.add("uz")
        languageCode1.add("vi")
        languageCode1.add("cy")
        languageCode1.add("xh")
        languageCode1.add("yo")
        languageCode1.add("zu")

        Log.e("My lang ------>", Locale.getDefault().toString())
        languageCode = Locale.getDefault().toString()
        if (languageCode == "zh_CN") {
            //do nothing
            languageCode = "zh"
        } else if (languageCode == "zh_TW" || languageCode == "ceb") {
            //do nothing

        } else {
            languageCode = languageCode!!.substring(0, 2)

        }

        for (i in languageCode1.indices) {
            if (languageCode == languageCode1[i]) {
                myPos = i
                Log.e("MyPos$i", languageCode)

                TranslatorActivity.setSharedPrefs(cr, "fromPos", "" + myPos)
                setLanguageReviewBackend()

            }
        }

        val intent = Intent(this@VerifyPhoneActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }

    fun setLanguageReviewBackend() {

        val cr = this
        TranslatorActivity.setSharedPrefs(cr, "fromReviewLang", "" + Splash.languageName[myPos])
        TranslatorActivity.setSharedPrefs(cr, "toReviewLang", "" + Splash.languageName[8])
        TranslatorActivity.setSharedPrefs(cr, "reviewCount", "0")
        TranslatorActivity.setSharedPrefs(cr, "submitReview5", "n")
        TranslatorActivity.setSharedPrefs(cr, "submitReview10", "n")
        TranslatorActivity.setSharedPrefs(cr, "alreadyRated", "n")


    }

    companion object {
        var mobileNumber: String? =null
    }


}