package com.wwalks.truediplomat.translator


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.crashlytics.android.Crashlytics
import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaeger.library.StatusBarUtil
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.wwalks.truediplomat.Splash

import java.util.ArrayList
import java.util.Locale

import io.fabric.sdk.android.Fabric


class TranslatorActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    internal var adapter: FragmentPagerItemAdapter? = null
    internal var viewPager: ViewPager? = null
    internal var viewPagerTab: SmartTabLayout? = null
    var settingsButton: ImageView? = null
    var myCurrentPage = 0


    var previous: String? = null
    var tabPos: String? = null
    var myPage: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        StatusBarUtil.setTransparent(this@TranslatorActivity)
        setContentView(R.layout.translator_base_main)
        Fabric.with(this, Crashlytics())
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        fName = "XXX"

        val pages = FragmentPagerItems(this)

        pages.add(FragmentPagerItem.of("Conversation", TranslatorFragment::class.java!!))
        pages.add(FragmentPagerItem.of("On the Go", TranslatorFragment::class.java!!))
        pages.add(FragmentPagerItem.of("History", TranslatorFragment::class.java!!))

        adapter = FragmentPagerItemAdapter(
                supportFragmentManager, pages)

        previous = intent.getStringExtra("previous")
        val ct = this

        fromPos = Integer.parseInt(getSharedPrefs(ct, "fromPos", "")!!)
        TranslatorFragment.dummyFrom = Integer.parseInt(getSharedPrefs(ct, "fromPos", "")!!)


/*
        fromPos = 15;
        TranslatorFragment.dummyFrom = 15;
        TranslatorActivity.setSharedPrefs(this, "fromReviewLang", "" + Splash.languageName.get(15));
        TranslatorActivity.setSharedPrefs(this, "toReviewLang", "" + Splash.languageName.get(8));
        TranslatorActivity.setSharedPrefs(this, "reviewCount", "0" );
        TranslatorActivity.setSharedPrefs(this, "submitReview5", "n" );
        TranslatorActivity.setSharedPrefs(this, "submitReview10", "n" );
        TranslatorActivity.setSharedPrefs(this, "alreadyRated", "n" );


 */





        if (previous == "myLoader") {
            fromPos = Integer.parseInt(intent.getStringExtra("from"))
            toPos = Integer.parseInt(intent.getStringExtra("to"))
            myPage = Integer.parseInt(intent.getStringExtra("tabPos"))


            setLanguageReviewBackend()


        } else if (previous == "myLoader1") {
            fromPos = Integer.parseInt(intent.getStringExtra("from"))
            toPos = Integer.parseInt(intent.getStringExtra("to"))
            myPage = Integer.parseInt(intent.getStringExtra("tabPos"))

            setLanguageReviewBackend()

        } else {

        }
        settingsButton = findViewById<View>(R.id.settingsButton) as ImageView
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        pg = findViewById<View>(R.id.my_progressBar) as ProgressBar

        viewPager!!.adapter = adapter

        viewPagerTab = findViewById<View>(R.id.viewpagertab) as SmartTabLayout
        viewPagerTab!!.setDividerColors(R.color.transparent)
        viewPagerTab!!.setViewPager(viewPager)
        if (previous == "myLoader1") {
            viewPager!!.currentItem = myPage
        } else if (previous == "chatPage") {
            viewPager!!.currentItem = 2
        } else if (previous == "myLoader") {
            viewPager!!.currentItem = myPage
        }


        tts = TextToSpeech(ct, this, "com.google.android.tts")


        previous = intent.getStringExtra("previous")




        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i2: Int) {
                //Toast.makeText(MyActivity.this, i+"  Is Selected  "+data.size(), Toast.LENGTH_SHORT).show();
            }

            override fun onPageSelected(i: Int) {
                // here you will get the position of selected page
                myCurrentPage = i
                Log.e("Page no:", "" + i)
                Log.e("ConvoNum", "" + convoNum)

                if (myCurrentPage == 2) {
                    if (convoNum == 1) {
                        //viewPager.setAdapter(adapter);

                        val translatorList1 = TranslatorActivity.getTranslationList(this@TranslatorActivity)


                        val gridAdapter = HistoryList(this@TranslatorActivity, translatorList1!!)
                        TranslatorFragment.historyList!!.adapter = gridAdapter
                        gridAdapter.notifyDataSetChanged()
                    }
                }

                if (myCurrentPage == 0) {
                    if (convoNum == 2) {
                        TranslatorFragment.convoText = "1"

                    } else if (convoNum == 1) {
                        TranslatorFragment.convoText = "2"
                        Log.e("Printer", "" + TranslatorFragment.alreadyConversing)


                        if (TranslatorFragment.alreadyConversing == 0) {
                            viewPager!!.adapter = adapter

                        }

                        if (TranslatorFragment.alreadyConversing == 1) {
                            var conversationValue: ArrayList<TranslationBean>? = ArrayList()
                            conversationValue = TranslatorActivity.getTranslationList(this@TranslatorActivity)

                            val conversationAdapter = ConversationListAdapter(this@TranslatorActivity, conversationValue!!, 0, 0, Splash.languageCode[TranslatorActivity.fromPos], Splash.languageCode[TranslatorActivity.toPos])
                            TranslatorFragment.conversationList!!.adapter = conversationAdapter
                            conversationAdapter.notifyDataSetChanged()

                        }


                    } else {
                        TranslatorFragment.convoText = "0"

                    }

                } else {
                    TranslatorFragment.convoText = "0"
                }



                convoNum = i

                val bundle = Bundle()

                if (i == 0) {
                    viewPager!!.currentItem = 0
                    bundle.putString("click_type", "Conversation")
                    bundle.putString("click_index", "0")
                    mFirebaseAnalytics!!.logEvent("Clicks", bundle)

                } else if (i == 1) {
                    bundle.putString("click_type", "On the Go")
                    bundle.putString("click_index", "1")
                    mFirebaseAnalytics!!.logEvent("Clicks", bundle)
                }


            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })

        viewPagerTab!!.setCustomTabView { container, position, adapter ->
            val inflater = LayoutInflater.from(container.context)
            val res = container.context.resources
            val tab = inflater.inflate(R.layout.layout, container, false)
            val customText = tab.findViewById<View>(R.id.custom_text) as TextView

            customText.typeface = MyFonts.getFont(this@TranslatorActivity,
                    MyFonts.GORDITALIGHT)


            tab
        }




        settingsButton!!.setOnClickListener {
            val intent = Intent(this@TranslatorActivity, SettingsPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            this@TranslatorActivity.startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


    }


    fun onPageSelected(position: Int) {

        //.instantiateItem() from until .destroyItem() is called it will be able to get the View of page.
        val page = adapter!!.getPage(position)

    }


    override fun onInit(status: Int) {
        Log.e("Available", "" + tts!!.availableLanguages.toString())

        if (status == TextToSpeech.SUCCESS) {

            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported")
            } else {


            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    fun setLanguageReviewBackend() {

        /**
         * Reset values of Languages
         * Reset Counter, reviewStatus for 5 & 10
         */

        setSharedPrefs(this, "fromReviewLang", "" + Splash.languageName[fromPos])
        setSharedPrefs(this, "toReviewLang", "" + Splash.languageName[toPos])
        setSharedPrefs(this, "reviewCount", "0")
        setSharedPrefs(this, "submitReview5", "n")
        setSharedPrefs(this, "submitReview10", "n")


    }

    companion object {

        var fromPos: Int = 0

        var toPos = 9
        var convoNum: Int = 0

        private val KEY_DEMO = "demo"
        var fName = "XXX"
        var pg: ProgressBar? = null

        var tts: TextToSpeech? = null
        var mFirebaseAnalytics: FirebaseAnalytics ? = null


        fun setSharedPrefs(c: Context?, key: String, value: String) {
            if (c != null) {
                val editor = c.getSharedPreferences("MyPref",
                        0).edit()
                editor.putString(key, value)
                editor.commit()
            }
        }

        fun getSharedPrefs(c: Context?, key: String,
                           default_value: String): String? {
            if (c == null) {
                return default_value
            } else {
                val prefs = c.getSharedPreferences("MyPref",
                        0)
                return prefs.getString(key, default_value)
            }
        }


        fun getTranslationList(context: Context): ArrayList<TranslationBean>? {
            return getAllTranslationsAnswers(context)
        }

        fun getAllTranslationsAnswers(context: Context): ArrayList<TranslationBean>? {
            val gson = Gson()
            var measurementRequests = ArrayList<TranslationBean>()
            val listType = object : TypeToken<ArrayList<TranslationBean>>() {

            }.type
            if (getSharedPrefs(context, "translatorList", "")!!.length != 0)
                measurementRequests = gson.fromJson(getSharedPrefs(context, "translatorList", ""), listType)

            return measurementRequests
        }

        fun deleteConversation(ct: Context, deletionList: ArrayList<TranslationBean>, position: Int) {

            Log.e("Overall Size before", "" + deletionList.size)
            deletionList.removeAt(position)

            val gson = Gson()
            setSharedPrefs(ct, "translatorList", gson.toJson(deletionList))

            Log.e("Overall Size after", "" + deletionList.size)


        }
    }


}