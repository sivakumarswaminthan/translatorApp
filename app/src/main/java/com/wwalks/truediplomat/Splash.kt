package com.wwalks.truediplomat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import com.wwalks.truediplomat.translator.TranslatorActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.ApiInterface
import com.wwalks.truediplomat.API.CloudLanguagesBean
import com.wwalks.truediplomat.API.FCMDataResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList
import java.util.Locale

class Splash : Activity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported")
            } else {
                Log.v("TTS", "onInit succeeded")


            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
         //To change body of created functions use File | Settings | File Templates.
    }

    private var mLastLocation: Location? = null
    // Google client to interact with Google APIClient
    private var mGoogleApiClient: GoogleApiClient? = null
    var support: TextView? = null
    var id: String? = null

    //String[] image = {"https://images.unsplash.com/photo-1486293973458-85593d43818c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=600&h=900&fit=crop&ixid=eyJhcHBfaWQiOjF9&s=c6271f19dd66a5679c9c4ba019b6b266","https://images.unsplash.com/photo-1489914099268-1dad649f76bf?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=600&h=900&fit=crop&ixid=eyJhcHBfaWQiOjF9&s=bc65e3b6e624c84fad2aa83725f27800","https://images.unsplash.com/photo-1481018766939-e811776a906a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=600&h=900&fit=crop&ixid=eyJhcHBfaWQiOjF9&s=bf66e4be0533a5a1a703ea47a08ae247"};

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        //        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.splash)

        if(cloudLanguageName.size==0) {
            getCloudLanguageList()
        }

        if(languageName.size==0) {

            languageName.add("Afrikaans")
            languageCode.add("af")
            languageName.add("Albanian")
            languageCode.add("sq")
            languageName.add("Arabic")
            languageCode.add("ar")
            languageName.add("Azerbaijani")
            languageCode.add("az")
            languageName.add("Basque")
            languageCode.add("eu")
            languageName.add("Bengali")
            languageCode.add("bn")
            languageName.add("Bosnian")
            languageCode.add("bs")
            languageName.add("Catalan")
            languageCode.add("ca")
            languageName.add("Cebuano")
            languageCode.add("ceb")
            languageName.add("Chinese Simplified")
            languageCode.add("zh")
            languageName.add("Chinese Traditional")
            languageCode.add("zh-TW")
            //languageName.add("Chinese Cantonese");
            //languageCode.add("yue_HK");//zh_HK - speech input;  yue_HK - speech output
            languageName.add("Corsican")
            languageCode.add("co")
            languageName.add("Croatian")
            languageCode.add("hr")
            languageName.add("Czech")
            languageCode.add("cs")
            languageName.add("Danish")
            languageCode.add("da")
            languageName.add("Dutch")
            languageCode.add("nl")
            languageName.add("English")
            languageCode.add("en")
            languageName.add("Esperanto")
            languageCode.add("eo")
            languageName.add("Estonian")
            languageCode.add("et")
            languageName.add("Filipino")
            languageCode.add("fil")
            languageName.add("Finnish")
            languageCode.add("fi")
            languageName.add("French")
            languageCode.add("fr")
            languageName.add("Frisian")
            languageCode.add("fy")
            languageName.add("Galician")
            languageCode.add("gl")
            languageName.add("German")
            languageCode.add("de")
            languageName.add("Greek")
            languageCode.add("el")
            languageName.add("Haitian Creole")
            languageCode.add("ht")
            languageName.add("Hausa")
            languageCode.add("ha")
            languageName.add("Hawaiian")
            languageCode.add("haw")
            languageName.add("Hindi")
            languageCode.add("hi")
            languageName.add("Hungarian")
            languageCode.add("hu")
            languageName.add("Icelandic")
            languageCode.add("is")
            languageName.add("Indonesian")
            languageCode.add("id")
            languageName.add("Irish")
            languageCode.add("ga")
            languageName.add("Italian")
            languageCode.add("it")
            languageName.add("Japanese")
            languageCode.add("ja")
            languageName.add("Javanese")
            languageCode.add("jw")
            languageName.add("Khmer")
            languageCode.add("km")
            languageName.add("Korean")
            languageCode.add("ko")
            languageName.add("Kurdish")
            languageCode.add("ku")
            languageName.add("Latin")
            languageCode.add("la")
            languageName.add("Latvian")
            languageCode.add("lv")
            languageName.add("Lithuanian")
            languageCode.add("lt")
            languageName.add("Luxembourgish")
            languageCode.add("lb")
            languageName.add("Malagasy")
            languageCode.add("mg")
            languageName.add("Malay")
            languageCode.add("ms")
            languageName.add("Maltese")
            languageCode.add("mt")
            languageName.add("Maori")
            languageCode.add("mi")
            languageName.add("Nepali")
            languageCode.add("ne")
            languageName.add("Norwegian")
            languageCode.add("no")
            languageName.add("Nyanja (Chichewa)")
            languageCode.add("ny")
            languageName.add("Polish")
            languageCode.add("pl")
            languageName.add("Portuguese")
            languageCode.add("pt")
            languageName.add("Romanian")
            languageCode.add("ro")
            languageName.add("Russian")
            languageCode.add("ru")
            languageName.add("Samoan")
            languageCode.add("sm")
            languageName.add("Scots Gaelic")
            languageCode.add("gd")
            languageName.add("Serbian")
            languageCode.add("sr")
            languageName.add("Sesotho")
            languageCode.add("st")
            languageName.add("Shona")
            languageCode.add("sn")
            languageName.add("Sinhala (Sinhalese)")
            languageCode.add("si")
            languageName.add("Slovak")
            languageCode.add("sk")
            languageName.add("Slovenian")
            languageCode.add("sl")
            languageName.add("Somali")
            languageCode.add("so")
            languageName.add("Spanish")
            languageCode.add("es")
            languageName.add("Sundanese")
            languageCode.add("su")
            languageName.add("Swahili")
            languageCode.add("sw")
            languageName.add("Swedish")
            languageCode.add("sv")
            languageName.add("Tagalog (Filipino)")
            languageCode.add("tl")
            languageName.add("Tamil")
            languageCode.add("ta")
            languageName.add("Thai")
            languageCode.add("th")
            languageName.add("Turkish")
            languageCode.add("tr")
            languageName.add("Ukrainian")
            languageCode.add("uk")
            languageName.add("Uzbek")
            languageCode.add("uz")
            languageName.add("Vietnamese")
            languageCode.add("vi")
            languageName.add("Welsh")
            languageCode.add("cy")
            languageName.add("Xhosa")
            languageCode.add("xh")
            languageName.add("Yoruba")
            languageCode.add("yo")
            languageName.add("Zulu")
            languageCode.add("zu")
        }


        support = findViewById<TextView>(R.id.textSupp)
        support!!.typeface = MyFonts.getFont(this@Splash,
                MyFonts.GORDITALIGHT)


        val ct = this
        //tts = new TextToSpeech(ct ,this,"com.google.android.tts");
        //tts = TextToSpeech(ct, this)
        tts = TextToSpeech(ct, this, "com.google.android.tts")
        /*
        tts = TextToSpeech(ct, TextToSpeech.OnInitListener { status ->
            Log.e("Available", "" + tts.availableLanguages.toString())

            if (status == TextToSpeech.SUCCESS) {

                val result = tts.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported")
                } else {
                    Log.v("TTS", "onInit succeeded")


                }

            } else {
                Log.e("TTS", "Initilization Failed!")
            }
        })

*/


        id = TranslatorActivity.getSharedPrefs(ct, "loginID", "")

        //startActivity(new Intent(Splash.this, MainActivity.class).putExtra("previous", "Splash"));
        //TranslatorActivity.setSharedPrefs(this, "fromPos", "8");


        if (id!!.length > 1) {
            Handler().postDelayed({
                startActivity(Intent(this@Splash, MainActivity::class.java).putExtra("previous", "Splash"))
                finish()
            }, 1500)

        } else {
            Handler().postDelayed({
                startActivity(Intent(this@Splash, LoginTypeActivity::class.java).putExtra("previous", "Splash"))
                //startActivity(Intent(this@Splash, MainActivity::class.java).putExtra("previous", "Splash"))
                finish()
            }, 1500)
        }


    }


    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
    }

    private fun checkPlayServices(): Boolean {
        val resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                Toast.makeText(applicationContext,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show()
                finish()
            }
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onResume() {
        super.onResume()

        checkPlayServices()
    }

    /**
     * Google api callback methods
     */
    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.errorCode)
    }

    override fun onConnected(arg0: Bundle?) {

        // Once connected with google api, get the location
        displayLocation()
    }

    /**
     * Method to display the location on UI
     */
    private fun displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLastLocation != null) {
            val latitude = mLastLocation!!.latitude
            val longitude = mLastLocation!!.longitude

            Log.e(TAG, "$latitude, $longitude")
            val gcd = Geocoder(baseContext, Locale.getDefault())
            val addresses: List<Address>?
            var cityName: String? = null
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1)
                if (addresses != null) {
                    if (addresses.size > 0)
                        cityName = addresses[0].locality
                    println("City Name: " + cityName!!)
                }
            } catch (e: Exception) {
            }

        } else {
            Log.e(TAG, "(Couldn't get the location. Make sure location is enabled on the device)")
        }
    }

    override fun onConnectionSuspended(arg0: Int) {
        mGoogleApiClient!!.connect()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        System.exit(0)
    }

    companion object {
        // LogCat tag
        private val TAG = MainActivity::class.java!!.getSimpleName()
        private val PLAY_SERVICES_RESOLUTION_REQUEST = 1000

        var languageName: MutableList<String> = ArrayList()
        var languageCode: MutableList<String> = ArrayList()

        var cloudLanguageName: MutableList<String> = ArrayList()
        var cloudLanguageCode: MutableList<String> = ArrayList()


        var tts: TextToSpeech? = null
    }

    fun getCloudLanguageList() {

        val service = APIClient.getClient(this).create<ApiInterface>(ApiInterface::class.java)



        val allData = service.getCloudLanguageList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())


        /*
        val allData = service.setMyReview("phone", "123456", stars, reviewMsg)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

         */

        allData.subscribe(object : Observer<List<CloudLanguagesBean>> {
            override fun onSubscribe(d: Disposable) {

                //donut_progress.setProgress(75);
            }

            override fun onNext(overallDataList: List<CloudLanguagesBean>) {

                Log.e("RESPONSE ---->" + overallDataList.size,  "---" + overallDataList[0].getName().toString())


                for (i in overallDataList.indices) {
                    cloudLanguageCode.add(overallDataList[i].getCode()!!.toString())
                    cloudLanguageName.add(overallDataList[i].getName()!!.toString())
                }


            }

            override fun onError(e: Throwable) {
                Toast.makeText(this@Splash, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

            }

            override fun onComplete() {

            }
        })


    }



}