package com.wwalks.truediplomat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric

import com.wwalks.truediplomat.translator.TranslatorActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Locale

class LoginTypeActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    var googleLayout: RelativeLayout? = null
    var facebookLayout: RelativeLayout? = null
    var otpLayout: RelativeLayout? = null
    var googleText: TextView? = null
    var facebookText: TextView? = null
    var otpText: TextView? = null
    var welcomeText: TextView? = null
    var supportText: TextView? = null

    var loginButton: LoginButton? = null
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private val serverclientid = "308038346652-e8r6n51aqsbb2inlhk1hgm9mel3v71oo.apps.googleusercontent.com"

    //private String serverclientid = "162137963033-rrf61nv59invdlm8fikntmqe7c7bn72g.apps.googleusercontent.com";
    private var googleApiClient: GoogleApiClient? = null

    var languageCode: String? = null
    var languageCode1: MutableList<String> = ArrayList()
    var myPos: Int = 0

    /*
    https://wwalks-translator.firebaseapp.com/__/auth/handler
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.setApplicationId("2196924393931970")
        FacebookSdk.sdkInitialize(this@LoginTypeActivity)

        setContentView(R.layout.activity_login_main)
        Fabric.with(this, Crashlytics())


        googleLayout = findViewById<View>(R.id.googleContainer) as RelativeLayout
        facebookLayout = findViewById<View>(R.id.facebookContainer) as RelativeLayout
        otpLayout = findViewById<View>(R.id.otpContainer) as RelativeLayout

        welcomeText = findViewById<View>(R.id.welcomeText) as TextView
        supportText = findViewById<View>(R.id.supportText) as TextView
        googleText = findViewById<View>(R.id.sign_in_text) as TextView
        facebookText = findViewById<View>(R.id.facebookText) as TextView
        otpText = findViewById<View>(R.id.otpText) as TextView





        mCallbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.buttonFacebookLogin)
        loginButton!!.setReadPermissions("email", "public_profile")


        welcomeText!!.typeface = MyFonts.getFont(this@LoginTypeActivity,
                MyFonts.GORDITALIGHT)
        supportText!!.typeface = MyFonts.getFont(this@LoginTypeActivity,
                MyFonts.GORDITAREGULAR)
        googleText!!.typeface = MyFonts.getFont(this@LoginTypeActivity,
                MyFonts.GORDITAMEDIUM)
        facebookText!!.typeface = MyFonts.getFont(this@LoginTypeActivity,
                MyFonts.GORDITAMEDIUM)
        otpText!!.typeface = MyFonts.getFont(this@LoginTypeActivity,
                MyFonts.GORDITAMEDIUM)


        findViewById<View>(R.id.otpText).setOnClickListener { otpLogin() }

        findViewById<View>(R.id.otpContainer).setOnClickListener { otpLogin() }

        findViewById<View>(R.id.googleContainer).setOnClickListener { googleLogin() }

        findViewById<View>(R.id.sign_in_text).setOnClickListener { googleLogin() }

        findViewById<View>(R.id.facebookContainer).setOnClickListener { facebookLogin() }

        findViewById<View>(R.id.facebookText).setOnClickListener { facebookLogin() }



        loginButton!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.e("FB Login", "facebook:onSuccess:$loginResult")

                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { `object`, response ->
                    Log.v("LoginActivity", response.toString())

                    // Application code
                    try {
                        val email = `object`.getString("email")

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }


                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.e("FB Login", "facebook:onCancel")
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.e("FB Login", "facebook:onError", error)
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        })

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            println("Google Login - Google: Success")
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)

        } else if (requestCode == 64206) {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager!!.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            email = account!!.email
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuthWithGoogle(credential)
        } else {
            // Google Sign In failed, update UI appropriately
            Log.e("Google Login", "Login Unsuccessful. $result")
            Toast.makeText(this, "Login Unsuccessful $result", Toast.LENGTH_SHORT).show()
        }
    }


    private fun firebaseAuthWithGoogle(credential: AuthCredential) {

        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d("Firebase Google", "signInWithCredential:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginTypeActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        val cx = this@LoginTypeActivity
                        val intent = intent

                        TranslatorActivity.setSharedPrefs(cx, "myID", email!!)

                        TranslatorActivity.setSharedPrefs(cx, "loginID", "gmail")


                        navigateToMainActivity()

                    } else {
                        Log.w("Firebase Google", "signInWithCredential" + task.exception!!.message)
                        task.exception!!.printStackTrace()
                        Toast.makeText(this@LoginTypeActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

    }


    fun facebookLogin() {
        mAuth = FirebaseAuth.getInstance()
        loginButton!!.performClick()
    }

    fun googleLogin() {

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverclientid)
                .requestEmail()
                .build()


        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()


        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, RC_SIGN_IN)


    }

    fun otpLogin() {
        val intent = Intent(this@LoginTypeActivity, LoginActivity::class.java)
        startActivity(intent)

    }


    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FB Login", "handleFacebookAccessToken:$token")
        val cape = token.token

        val credential = FacebookAuthProvider.getCredential(token.token)
        Log.e("Credential", credential.toString() + "----------" + credential.signInMethod + "#####" + credential.provider)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.e("FB Login", "signInWithCredential:success")
                println("FB Login - signInwithCreditional: Success")
                val user = mAuth!!.currentUser

                val cx = this@LoginTypeActivity
                val intent = intent

                TranslatorActivity.setSharedPrefs(cx, "myID", cape)


                TranslatorActivity.setSharedPrefs(cx, "fcmToken", mAuth!!.uid!!)
                TranslatorActivity.setSharedPrefs(cx, "loginID", "fb")

                navigateToMainActivity()

            } else {
                // If sign in fails, display a message to the user.
                Log.e("FB Login", "signInWithCredential:failure", task.exception)
                println("FB Login - signInwithCreditional: Failure")
                Toast.makeText(this@LoginTypeActivity, "Authentication failed " + task.exception + "----" + task.isSuccessful,
                        Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun navigateToMainActivity() {

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

        val intent = Intent(this@LoginTypeActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }


    fun setLanguageReviewBackend() {

        val cr = this
        print(Splash.languageName[myPos])
        TranslatorActivity.setSharedPrefs(cr, "fromReviewLang", "" + Splash.languageName[myPos])
        TranslatorActivity.setSharedPrefs(cr, "toReviewLang", "" + Splash.languageName[8])
        TranslatorActivity.setSharedPrefs(cr, "reviewCount", "0")
        TranslatorActivity.setSharedPrefs(cr, "submitReview5", "n")
        TranslatorActivity.setSharedPrefs(cr, "submitReview10", "n")
        TranslatorActivity.setSharedPrefs(cr, "alreadyRated", "n")


    }

    companion object {


        private val RC_SIGN_IN = 9001

        var email: String? = null
    }


}


