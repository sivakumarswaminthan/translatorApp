package com.wwalks.truediplomat.translator

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.crashlytics.android.Crashlytics
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.ApiInterface
import com.wwalks.truediplomat.API.FCMDataResponse
import com.wwalks.truediplomat.MainActivity
import com.wwalks.truediplomat.R
import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.Splash
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import com.google.gson.Gson
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem


import org.apache.commons.lang3.StringEscapeUtils

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Locale

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import io.fabric.sdk.android.Fabric
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import android.app.Activity.RESULT_OK
import android.media.MediaPlayer
//import com.wwalks.truediplomat.translator.TranslatorActivity.mFirebaseAnalytics
import com.facebook.FacebookSdk.getApplicationContext
import com.wwalks.truediplomat.API.APIClient1
import com.wwalks.truediplomat.translator.TranslatorActivity.Companion.mFirebaseAnalytics

/**
 * Created by DELL on 3/4/2018.
 */

class TranslatorFragment : Fragment(), TextToSpeech.OnInitListener, AdapterView.OnItemClickListener, RatingDialogListener {

    private var txtSpeechInput: TextView? = null
    private var txtTranslationOutput: TextView? = null
    private var fromValue: TextView? = null
    private var toValue: TextView? = null
    private var fromText: TextView? = null
    private var toText: TextView? = null
    private var listenText: TextView? = null
    private var speakText: TextView? = null
    private var language1TextView: TextView? = null
    private var language2TextView: TextView? = null
    private var fromValue1: TextView? = null
    private var toValue1: TextView? = null
    private var fromText1: TextView? = null
    private var toText1: TextView? = null
    private val REQ_CODE_SPEECH_INPUT = 100
    var swap: ImageView? = null
    var speakIcon: ImageView? = null
    var listenIcon: ImageView? = null
    var btnLanguage1: ImageView? = null
    var btnLanguage2: ImageView? = null
    var newChat: ImageView? = null
    var convosImage: ImageView? = null
    var onthegoImage: ImageView? = null

    var topLeft: RelativeLayout? = null
    var topRight: RelativeLayout? = null
    var topLeft1: RelativeLayout? = null
    var topRight1: RelativeLayout? = null
    var language1Layout: RelativeLayout? = null
    var language2Layout: RelativeLayout? = null

    var centerLayoutOnTheGo: RelativeLayout? = null
    var centerLayoutConversation: RelativeLayout? = null
    var speakLayout1: RelativeLayout? = null
    var listenLayout1: RelativeLayout? = null
    var languageFirstLayout: RelativeLayout? = null
    var languageSecondLayout: RelativeLayout? = null

    var outputText: String? = null
    var inputText: String? = null
    var fileName: String? = null
    var bundle = Bundle()

    var mediaPlayer: MediaPlayer? = null


    val currentDateAndTime: Array<String>
        get() {

            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy")
            val df1 = SimpleDateFormat("hh:mm:ss.SSS")

            val formattedDate = arrayOf(df.format(c), df1.format(c))


            Log.e("Formatted Date", formattedDate[0])
            Log.e("Formatted Date", formattedDate[1])
            return formattedDate

        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val p = FragmentPagerItem.getPosition(arguments)

        Log.e("Convo Num", "" + TranslatorActivity.convoNum)
        /*
        if(TranslatorActivity.convoNum==2 && p==0) {
            convoText = "1";
        } else {
            convoText = "0";
        }
*/

        if (TranslatorActivity.convoNum == 2 && p == 0) {
            convoText = "1"
        } else if (TranslatorActivity.convoNum == 1 && p == 0) {
            convoText = "2"
        } else {
            convoText = "0"
        }



        if (p == 1) {
            Log.e("My porter", " $p")
            return inflater.inflate(R.layout.activity_translate, container, false)
        } else if (p == 0) {
            Log.e("My porter", " $p")
            return inflater.inflate(R.layout.activity_translate_chats, container, false)
        } else {
            Log.e("My porter", " $p")
            return inflater.inflate(R.layout.history_translation, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Fabric.with(context, Crashlytics())


        val position = FragmentPagerItem.getPosition(arguments)

        if (position == 1) {


            //create file name in every conversation

            val dateTime = currentDateAndTime
            fileName = dateTime[0] + " " + dateTime[1]

            txtSpeechInput = view.findViewById<View>(R.id.txtSpeechInput) as TextView

            txtTranslationOutput = view.findViewById<View>(R.id.txtTranslationOutput) as TextView

            swap = view.findViewById<View>(R.id.swapLanguage) as ImageView
            listenIcon = view.findViewById<View>(R.id.btnListen) as ImageView
            speakIcon = view.findViewById<View>(R.id.btnSpeak) as ImageView
            topLeft = view.findViewById<View>(R.id.leftPart) as RelativeLayout
            topRight = view.findViewById<View>(R.id.rightPart) as RelativeLayout
            fromValue = view.findViewById<View>(R.id.fromValue) as TextView
            toValue = view.findViewById<View>(R.id.toValue) as TextView
            fromText = view.findViewById<View>(R.id.fromText) as TextView
            toText = view.findViewById<View>(R.id.toText) as TextView
            listenText = view.findViewById<View>(R.id.listenText) as TextView
            speakText = view.findViewById<View>(R.id.speakText) as TextView

            centerLayoutOnTheGo = view.findViewById<View>(R.id.centerTutorOnTheGo) as RelativeLayout
            onthegoImage = view.findViewById<View>(R.id.onthegoImage) as ImageView

            speakLayout1 = view.findViewById<View>(R.id.speakLayout1) as RelativeLayout
            listenLayout1 = view.findViewById<View>(R.id.listenLayout1) as RelativeLayout

            fromValue!!.text = Splash.languageName[TranslatorActivity.fromPos]
            toValue!!.text = Splash.languageName[TranslatorActivity.toPos]

            txtSpeechInput!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)
            txtTranslationOutput!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)

            fromValue!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)
            fromText!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)
            toValue!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)
            toText!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)

            listenText!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAREGULAR)
            speakText!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAREGULAR)

            chatSide = 0

            //onthegoImage.setImageAlpha(80);

            speakText!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                promptSpeechInput()
                centerLayoutOnTheGo!!.visibility = View.GONE
                //translateText();
            }

            speakLayout1!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                promptSpeechInput()
                centerLayoutOnTheGo!!.visibility = View.GONE
                //translateText();
            }



            listenText!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                listenSpeechOutput()
            }

            listenLayout1!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                listenSpeechOutput()
            }

            speakIcon!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                promptSpeechInput()
                centerLayoutOnTheGo!!.visibility = View.GONE
            }

            listenIcon!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                listenSpeechOutput()
            }

            swap!!.setOnClickListener {
                chatSide = 0
                TranslatorActivity.fName = "XXX"
                val from = TranslatorActivity.fromPos
                val to = TranslatorActivity.toPos
                TranslatorActivity.fromPos = to
                TranslatorActivity.toPos = from

                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + from)
                intent.putExtra("from", "" + to)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "1")

                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            val ap = activity
            var xyz = ArrayList<String>()
            xyz = Splash.languageName as ArrayList<String>
            Log.e("ap", xyz[8])


            val spinnerDialog1: SpinnerDialog
            spinnerDialog1 = SpinnerDialog(ap, xyz, "Select your output language", "Close")
            spinnerDialog1.setCancellable(true) // for cancellable
            spinnerDialog1.setShowKeyboard(false)// for open keyboard by default

            spinnerDialog1.bindOnSpinerListener { item, position ->
                TranslatorActivity.toPos = position
                TranslatorFragment.dummyTo = position
                toValue!!.text = item

                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + dummyTo)
                intent.putExtra("from", "" + dummyFrom)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "1")
                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }


            topRight!!.setOnClickListener {
                TranslatorActivity.fName = "XXX"
                spinnerDialog1.showSpinerDialog()
            }


            val spinnerDialog: SpinnerDialog
            spinnerDialog = SpinnerDialog(ap, xyz, "Select your input language", "Close")
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default

            spinnerDialog.bindOnSpinerListener { item, position ->
                TranslatorActivity.fromPos = position
                TranslatorFragment.dummyFrom = position
                TranslatorActivity.setSharedPrefs(context, "fromPos", "" + position)
                fromValue!!.text = item

                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + dummyTo)
                intent.putExtra("from", "" + dummyFrom)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "1")
                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }


            topLeft!!.setOnClickListener {
                TranslatorActivity.fName = "XXX"

                spinnerDialog.showSpinerDialog()
            }


        }
        if (position == 0) {

            language1Layout = view.findViewById<View>(R.id.lang1Layout) as RelativeLayout
            language2Layout = view.findViewById<View>(R.id.lang2Layout) as RelativeLayout

            languageFirstLayout = view.findViewById<View>(R.id.language1Layout) as RelativeLayout
            languageSecondLayout = view.findViewById<View>(R.id.language2Layout) as RelativeLayout

            centerLayoutConversation = view.findViewById<View>(R.id.centerTutor) as RelativeLayout

            newChat = view.findViewById<View>(R.id.newChat) as ImageView
            convosImage = view.findViewById<View>(R.id.convosImage) as ImageView
            topLeft1 = view.findViewById<View>(R.id.leftPart1) as RelativeLayout
            topRight1 = view.findViewById<View>(R.id.rightPart1) as RelativeLayout

            fromValue1 = view.findViewById<View>(R.id.fromValue1) as TextView
            toValue1 = view.findViewById<View>(R.id.toValue1) as TextView

            fromText1 = view.findViewById<View>(R.id.fromText1) as TextView
            toText1 = view.findViewById<View>(R.id.toText1) as TextView

            language1TextView = view.findViewById<View>(R.id.language1) as TextView
            language2TextView = view.findViewById<View>(R.id.language2) as TextView

            btnLanguage1 = view.findViewById<View>(R.id.btnLanguage1) as ImageView
            btnLanguage2 = view.findViewById<View>(R.id.btnLanguage2) as ImageView


            conversationList = view.findViewById<View>(R.id.conversationList) as ListView

            if (alreadyConversing == 0) {

                val dateTime1 = currentDateAndTime
                if (TranslatorActivity.fName == "XXX") {
                    TranslatorActivity.fName = dateTime1[0] + " " + dateTime1[1]
                    Log.e("Maatikiche", "maatikiche")
                    conversationList!!.adapter = null

                }
            }

            Log.e("FName", TranslatorActivity.fName)

            //convosImage.setImageAlpha(80);

            if (convoText == "0") {
                //Coming to Conversation from OnTheGo
                Log.e("Major", "0")
                Log.e("After history", "via on the go")

                //centerLayoutConversation.setVisibility(View.VISIBLE);

            } else if (convoText == "2") {
                //Coming to Conversation from OnTheGo - after executing OnTheGo
                Log.e("Major", "0")
                //centerLayoutConversation.setVisibility(View.VISIBLE);

            } else if (convoText == "1") {
                //coming back to Conversation from History
                Log.e("Major", "1")
                //centerLayoutConversation.setVisibility(View.GONE);
                var conversationValue: ArrayList<TranslationBean>? = ArrayList()
                val cf = context
                conversationValue = TranslatorActivity.getTranslationList(cf!!)

                val myPos = conversationValue!!.size

                if (alreadyConversing == 1) {
                    if (myPos > 0) {
                        Log.e("Entering", "adapter")
                        val conversationAdapter = ConversationListAdapter(activity!!, conversationValue!!, 0, 0, Splash.languageCode[TranslatorActivity.fromPos], Splash.languageCode[TranslatorActivity.toPos])
                        conversationList!!.adapter = conversationAdapter
                        conversationAdapter.notifyDataSetChanged()
                    } else {

                    }
                }


            } else {
                //Coming to Conversation directly
                Log.e("Major", "100")
                //centerLayoutConversation.setVisibility(View.VISIBLE);

            }


            fromValue1!!.text = Splash.languageName[TranslatorActivity.fromPos]
            toValue1!!.text = Splash.languageName[TranslatorActivity.toPos]
            language1TextView!!.text = Splash.languageName[TranslatorActivity.fromPos]
            language2TextView!!.text = Splash.languageName[TranslatorActivity.toPos]

            fromValue1!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)
            fromText1!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)
            toValue1!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITAMEDIUM)
            toText1!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)

            language1TextView!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)
            language2TextView!!.typeface = MyFonts.getFont(context!!,
                    MyFonts.GORDITALIGHT)
            val ap = activity
            var xyz = ArrayList<String>()
            xyz = Splash.languageName as ArrayList<String>
            Log.e("ap", xyz[8])


            val spinnerDialog1: SpinnerDialog
            spinnerDialog1 = SpinnerDialog(ap, xyz, "Select your output language", "Close")
            spinnerDialog1.setCancellable(true) // for cancellable
            spinnerDialog1.setShowKeyboard(false)// for open keyboard by default

            spinnerDialog1.bindOnSpinerListener { item, position ->
                TranslatorActivity.toPos = position
                TranslatorFragment.dummyTo = position
                toValue1!!.text = item

                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + dummyTo)
                intent.putExtra("from", "" + dummyFrom)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "0")
                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }


            topRight1!!.setOnClickListener {
                TranslatorActivity.fName = "XXX"
                spinnerDialog1.showSpinerDialog()
            }

            newChat!!.setOnClickListener {
                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + dummyTo)
                intent.putExtra("from", "" + dummyFrom)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "0")
                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }


            val spinnerDialog: SpinnerDialog
            spinnerDialog = SpinnerDialog(ap, xyz, "Select your input language", "Close")
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default

            spinnerDialog.bindOnSpinerListener { item, position ->
                TranslatorActivity.fromPos = position
                TranslatorFragment.dummyFrom = position
                TranslatorActivity.setSharedPrefs(context, "fromPos", "" + position)
                fromValue1!!.text = item


                val intent = Intent(activity, TranslatorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("to", "" + dummyTo)
                intent.putExtra("from", "" + dummyFrom)
                intent.putExtra("previous", "myLoader")
                intent.putExtra("tabPos", "0")
                activity!!.startActivity(intent)
                (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }


            topLeft1!!.setOnClickListener {
                TranslatorActivity.fName = "XXX"

                spinnerDialog.showSpinerDialog()
            }

            language1Layout!!.setOnClickListener {
                chatSide = 1
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            language2Layout!!.setOnClickListener {
                chatSide = 2
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            languageFirstLayout!!.setOnClickListener {
                chatSide = 1
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            languageSecondLayout!!.setOnClickListener {
                chatSide = 2
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            language1TextView!!.setOnClickListener {
                chatSide = 1
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            language2TextView!!.setOnClickListener {
                chatSide = 2
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            btnLanguage1!!.setOnClickListener {
                chatSide = 1
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }

            btnLanguage2!!.setOnClickListener {
                chatSide = 2
                convosImage!!.visibility = View.GONE
                centerLayoutConversation!!.visibility = View.GONE
                promptSpeechInput()
            }


        }
        if (position == 2) {
            historyList = view.findViewById<View>(R.id.historyList) as ListView

            val translatorList1 = TranslatorActivity.getTranslationList(context!!)


            val gridAdapter = HistoryList(activity!!, translatorList1!!)
            historyList!!.adapter = gridAdapter


        }

    }


    override fun onInit(status: Int) {


        Log.e("Available", "" + TranslatorActivity.tts!!.availableLanguages.toString())

        if (status == TextToSpeech.SUCCESS) {

            val result = TranslatorActivity.tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported")
            } else {


            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
    /**
     * @Override
     * public void onStop() {
     *
     *
     * //Close the Text to Speech Library
     * super.onStop();
     * if (tts != null) {
     *
     * tts.stop();
     * tts.shutdown();
     * Log.d("TTS", "TTS Destroyed");
     * }
     * super.onDestroy();
     * }
     */

    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        if (chatSide == 0 || chatSide == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Splash.languageCode[TranslatorActivity.fromPos])
        } else if (chatSide == 2) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Splash.languageCode[TranslatorActivity.toPos])
        }

        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }

    }


    /**
     * Translate text
     */
    private fun translateText(chatSide: Int) {
        Log.e("Entering", "Old Dragon")
        val inputText1: String
        if (chatSide == 0) {
            inputText1 = txtSpeechInput!!.text.toString()
        } else {
            inputText1 = inputText!!
        }
        if (inputText1.length == 0) {
            Toast.makeText(activity,
                    getString(R.string.noInputTranslate),
                    Toast.LENGTH_SHORT).show()
        } else {
            //translate(inputText);

            if (chatSide == 0) {
                val translate = Translate1()
                translate.execute(inputText1)
            } else {
                val translate = Translate2()
                translate.execute(inputText1, "" + chatSide, TranslatorActivity.fName)
            }


        }
    }

    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    for (i in result.indices) {
                        Log.e("input speech result", result[i].toString())
                    }

                    inputText = result[0]

                    if (chatSide == 0) {
                        txtSpeechInput!!.text = result[0]
                        translateText(0)
                    } else if (chatSide == 1) {
                        translateText(1)
                        val bundle = Bundle()
                        bundle.putString("from_language_name", Splash.languageName[TranslatorActivity.fromPos])
                        bundle.putString("to_language_name", Splash.languageName[TranslatorActivity.toPos])
                        bundle.putString("translate_type", "Conversations")
                        mFirebaseAnalytics!!.logEvent("share_image", bundle)
                    } else if (chatSide == 2) {
                        translateText(2)
                    }


                } else {
                    Log.e("Cool person", "$resultCode, $data")
                }
            }
        }

    }

    private fun listenSpeechOutput() {


        val text = txtTranslationOutput!!.text.toString()

        var cloudVoice: Boolean = true
        if(chatSide == 0 || chatSide == 1) {
            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.toPos])
            if(cloudVoice==true) {
                callCloudVoice(text, Splash.languageCode[TranslatorActivity.toPos])
            } else {
                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
                Log.e("Dragon", text + "  " + Splash.languageCode[TranslatorActivity.toPos])
            }
        } else {
            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.fromPos])
            if(cloudVoice==true) {
                callCloudVoice(text, Splash.languageCode[TranslatorActivity.fromPos])
            } else {
                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)


                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
                Log.e("Dragon", text + "  " + Splash.languageCode[TranslatorActivity.fromPos])
            }
        }


        /**
        if(cloudVoice==true) {
            if(chatSide == 0 || chatSide == 1) {
                callCloudVoice(text, Splash.languageCode[TranslatorActivity.toPos])
            } else {
                callCloudVoice(text, Splash.languageCode[TranslatorActivity.fromPos])
            }
        } else {
            if (chatSide == 0 || chatSide == 1) {

                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
                Log.e("Dragon", text + "  " + Splash.languageCode[TranslatorActivity.toPos])

            } else {

                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)


                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
                Log.e("Dragon", text + "  " + Splash.languageCode[TranslatorActivity.fromPos])

            }


        }
        **/





    }




    fun languageSelect(position: String, tab: String) {

        Log.e("Pos ------->", position)
        val li = LayoutInflater.from(activity)
        val dialogView = li.inflate(R.layout.show_languages, null)

        val alertDialogBuilder = AlertDialog.Builder(activity)
        // set title
        if (position == "1") {
            alertDialogBuilder.setTitle("Set input language")
        } else {
            alertDialogBuilder.setTitle("Set output language")
        }
        // set custom dialog icon
        alertDialogBuilder.setIcon(R.mipmap.wwalks)
        // set custom_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView)

        val languageList = dialogView
                .findViewById<View>(R.id.languageList) as RecyclerView


        val language = RecyclerViewDataAdapter12(activity!!, Splash.languageName, position, languageList)
        languageList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        languageList.adapter = language
        language.onItemClickListener(this)



        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK"
                ) { dialog, id ->
                    if (tab == "chat") {

                        val intent = Intent(activity, TranslatorActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                        intent.putExtra("to", "" + dummyTo)
                        intent.putExtra("from", "" + dummyFrom)
                        intent.putExtra("previous", "myLoader")
                        intent.putExtra("tabPos", "1")
                        activity!!.startActivity(intent)
                        (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

                    } else {

                        val intent = Intent(activity, TranslatorActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                        intent.putExtra("to", "" + dummyTo)
                        intent.putExtra("from", "" + dummyFrom)
                        intent.putExtra("previous", "myLoader")
                        intent.putExtra("tabPos", "0")
                        activity!!.startActivity(intent)
                        (activity as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

                    }
                }


        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()

    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.e("Clicked item is", "" + position)
    }

    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {
        Log.e("Inside neutral clicked", "Already Clicked")

        val ct = context
        TranslatorActivity.setSharedPrefs(ct, "alreadyRated", "y")

    }

    override fun onPositiveButtonClicked(i: Int, s: String) {

        val cx = context

        val stars = "" + i

        Log.e("Inside positive clicked", "Thanks")

        val id = TranslatorActivity.getSharedPrefs(cx, "myID", "")
        val type = TranslatorActivity.getSharedPrefs(cx, "loginID", "")

        updateServer(type!!, id!!, stars, s)

    }


    internal inner class Translate1 : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {

            TranslatorActivity.pg!!.visibility = View.VISIBLE
            TranslatorActivity.pg!!.isIndeterminate = true
        }


        override fun doInBackground(vararg params: String): String {


            val text = params[0] //text to translate
            val options = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyDIrmrFeWn2ztX75dTAtbRly9IQlmNBH-M")
                    .build()
            //Translate translate = TranslateOptions.getDefaultInstance().getService();
            val bundle = Bundle()
            bundle.putString("from_language_name", Splash.languageName[TranslatorActivity.fromPos])
            bundle.putString("to_language_name", Splash.languageName[TranslatorActivity.toPos])
            bundle.putString("translate_type", "On the Go")
            mFirebaseAnalytics!!.logEvent("share_image", bundle)

            if (Splash.languageCode[TranslatorActivity.fromPos] == Splash.languageCode[TranslatorActivity.toPos]) {

                return text
            } else {
                val translate = options.service
                val translation = translate.translate(
                        text,
                        com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(Splash.languageCode[TranslatorActivity.fromPos]),
                        Translate.TranslateOption.targetLanguage(Splash.languageCode[TranslatorActivity.toPos]))


                return translation.translatedText
            }
        }

        //this method will run after doInBackground execution
        override fun onPostExecute(result: String) {
            TranslatorActivity.pg!!.visibility = View.GONE
            System.out.printf("Translation: %s%n", result)

            alreadyConversing = 0
            Log.e("Text trans", StringEscapeUtils.unescapeHtml4(result))

            outputText = StringEscapeUtils.unescapeHtml4(result)
            txtTranslationOutput!!.text = outputText


            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.toPos])
            if(cloudVoice==true) {
                callCloudVoice(outputText!!, Splash.languageCode[TranslatorActivity.toPos] )
            } else {
                listenSpeechOutput()
            }

            savingTranslations(fileName!!, "immediate", "0", inputText!!, outputText)



            TranslatorActivity.fName = "XXX"


        }


    }

    internal inner class Translate2 : AsyncTask<String, Void, String>() {

        var chatside: String? = null
        var filename: String? = null

        override fun onPreExecute() {

            TranslatorActivity.pg!!.visibility = View.VISIBLE
            TranslatorActivity.pg!!.isIndeterminate = true
        }

        override fun doInBackground(vararg params: String): String {


            val text = params[0] //text to translate
            chatside = params[1]
            filename = params[2]
            val options = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyDIrmrFeWn2ztX75dTAtbRly9IQlmNBH-M")
                    .build()
            //Translate translate = TranslateOptions.getDefaultInstance().getService();


            if (Splash.languageCode[TranslatorActivity.fromPos] == Splash.languageCode[TranslatorActivity.toPos]) {

                return text
            } else {
                val translate = options.service
                val translation: Translation
                if (chatside == "1") {
                    translation = translate.translate(
                            text,
                            com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(Splash.languageCode[TranslatorActivity.fromPos]),
                            Translate.TranslateOption.targetLanguage(Splash.languageCode[TranslatorActivity.toPos]))

                } else {
                    translation = translate.translate(
                            text,
                            com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(Splash.languageCode[TranslatorActivity.toPos]),
                            Translate.TranslateOption.targetLanguage(Splash.languageCode[TranslatorActivity.fromPos]))
                }
                return translation.translatedText
            }
        }

        //this method will run after doInBackground execution
        override fun onPostExecute(result: String) {
            TranslatorActivity.pg!!.visibility = View.GONE
            System.out.printf("Translation: %s%n", StringEscapeUtils.unescapeHtml4(result))

            outputText = StringEscapeUtils.unescapeHtml4(result)
            if (chatside == "1") {
                //createFile(outputText, Splash.languageCode.get(TranslatorActivity.fromPos),Splash.languageCode.get(TranslatorActivity.toPos));
            } else {
                //createFile(outputText, Splash.languageCode.get(TranslatorActivity.toPos),Splash.languageCode.get(TranslatorActivity.fromPos));
            }

            Log.e("Output String", outputText)
            alreadyConversing = 1



            savingTranslations(filename!!, "chat", chatside!!, inputText!!, outputText)


        }


    }

    fun savingTranslations(fileName: String, chatType: String, messageType: String, fromText: String, toText: String?) {
        var fileName = fileName

        val addConversation = TranslationBean()
        val conversations = ArrayList<TranslationBean.AnswersBean>()
        val date: String
        val time: String
        Log.e("Inside save block", fileName)

        if (chatType == "immediate") {

            date = fileName.substring(0, 11)
            Log.e("Date", date)
            time = fileName.substring(13, 17)
            Log.e("Time", time)


        } else {
            /**
             * if fileName exists use the old date
             * else create new fileName and use that data
             */
            if (fileName == "XXX") {
                val fileNameArray = currentDateAndTime
                TranslatorActivity.fName = fileNameArray[0] + " " + fileNameArray[1]
                fileName = TranslatorActivity.fName
                date = fileName.substring(0, 11)
                Log.e("Date", date)
                time = fileName.substring(13, 17)
                Log.e("Time", time)
            } else {
                date = fileName.substring(0, 11)
                Log.e("Date", date)
                time = fileName.substring(13, 17)
                Log.e("Time", time)
            }

        }

        val translatorList1 = TranslatorActivity.getTranslationList(context!!)


        if (chatType == "chat") {
            var value = 10000000

            alreadyConversing = 1
            for (i in translatorList1!!.indices) {
                if (TranslatorActivity.fName == translatorList1[i].chatName) {

                    value = i
                    break
                }

            }

            if (value == 10000000) {
                //newFile

                addConversation.chatName = fileName
                addConversation.date = date
                addConversation.time = time
                addConversation.fromLanguageName = Splash.languageName[TranslatorActivity.fromPos]
                addConversation.toLanguageName = Splash.languageName[TranslatorActivity.toPos]
                addConversation.fromLanguageCode = Splash.languageCode[TranslatorActivity.fromPos]
                addConversation.toLanguageCode = Splash.languageCode[TranslatorActivity.toPos]
                addConversation.typeOfChat = chatType

                val answersBean = TranslationBean.AnswersBean()
                answersBean.fromText = fromText
                answersBean.toText = toText
                answersBean.typeOfMessage = messageType
                conversations.add(answersBean)

                addConversation.answers = conversations


            } else {
                //copy file
                addConversation.chatName = translatorList1[value].chatName
                addConversation.date = translatorList1[value].date
                addConversation.time = translatorList1[value].time
                addConversation.fromLanguageName = translatorList1[value].fromLanguageName
                addConversation.toLanguageName = translatorList1[value].toLanguageName
                addConversation.fromLanguageCode = translatorList1[value].fromLanguageCode
                addConversation.toLanguageCode = translatorList1[value].toLanguageCode
                addConversation.typeOfChat = translatorList1[value].typeOfChat
                val answersBean = TranslationBean.AnswersBean()
                answersBean.fromText = fromText
                answersBean.toText = toText
                answersBean.typeOfMessage = messageType
                conversations.add(answersBean)

/*
                val bean = translatorList1[value].answers
                bean!!.add(answersBean)
                addConversation.answers = bean


 */

                var bean = ArrayList<TranslationBean.AnswersBean>()
                bean = translatorList1[value].answers as ArrayList<TranslationBean.AnswersBean>

                bean.add(answersBean)
                addConversation.answers = bean
                /**
                 * call Automatic Review Handler
                 */
                Log.e("Chat size", "" + translatorList1[value].answers!!.size)
                if (translatorList1[value].answers!!.size == 5 || translatorList1[value].answers!!.size == 10) {


                    automaticReviewHandler(1, translatorList1[value].answers!!.size)
                }


            }
        } else if (chatType == "immediate") {

            alreadyConversing = 0
            addConversation.chatName = fileName
            addConversation.date = date
            addConversation.time = time
            addConversation.fromLanguageName = Splash.languageName[TranslatorActivity.fromPos]
            addConversation.toLanguageName = Splash.languageName[TranslatorActivity.toPos]
            addConversation.fromLanguageCode = Splash.languageCode[TranslatorActivity.fromPos]
            addConversation.toLanguageCode = Splash.languageCode[TranslatorActivity.toPos]
            addConversation.typeOfChat = chatType

            val answersBean = TranslationBean.AnswersBean()
            answersBean.fromText = fromText
            answersBean.toText = toText
            answersBean.typeOfMessage = messageType
            conversations.add(answersBean)

            addConversation.answers = conversations

            /**
             * call Automatic Review Handler
             */
            automaticReviewHandler(0, 0)

        }


        val gson = Gson()

        val translatorList = TranslatorActivity.getTranslationList(context!!)

        var isExist = 0
        for (i in translatorList!!.indices) {
            if (TranslatorActivity.fName == translatorList[i].chatName) {

                translatorList[i] = addConversation
                isExist++
                break
            }
        }
        if (isExist == 0)
            translatorList.add(0, addConversation)


        TranslatorActivity.setSharedPrefs(context, "translatorList", gson.toJson(translatorList))

        if (chatType == "chat") {
            var value = 10000000

            for (i in translatorList.indices) {
                if (TranslatorActivity.fName == translatorList[i].chatName) {

                    value = i
                    break
                }
            }

            if (value == 10000000) {
                Log.e("Poda", "punnaku")
                value = translatorList.size


            }

            Log.e("value", "" + value)


            val conversationAdapter = ConversationListAdapter(activity!!, translatorList, value, 0, Splash.languageCode[TranslatorActivity.fromPos], Splash.languageCode[TranslatorActivity.toPos])
            conversationList!!.adapter = conversationAdapter
            conversationAdapter.notifyDataSetChanged()
        }

        Log.e("international", toText)
        //tts.speak(toText, TextToSpeech.QUEUE_FLUSH, null);

        var cloudVoice: Boolean = true
        if(chatSide == 0 || chatSide == 1) {
            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.toPos])

            if(cloudVoice==true) {
                callCloudVoice(toText!!, Splash.languageCode[TranslatorActivity.toPos] )
            } else {
                //speaker(toText, Splash.languageCode[TranslatorActivity.toPos])
                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                TranslatorActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
                TranslatorActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
            }

        } else {
            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.fromPos])
            if(cloudVoice==true) {
                callCloudVoice(toText!!, Splash.languageCode[TranslatorActivity.fromPos] )
            } else {
                //speaker(toText, Splash.languageCode[TranslatorActivity.toPos])

                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                MainActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                MainActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
                MainActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                MainActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
            }
        }


    }

    fun speaker(toText: String?, s: String) {

        if (chatSide == 0 || chatSide == 1) {
            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.toPos])


            if(cloudVoice==true) {
                callCloudVoice(toText!!, Splash.languageCode[TranslatorActivity.toPos] )
            } else {
                Log.e("tts translate",toText!! +"---" + Splash.languageCode[TranslatorActivity.toPos])
                //speaker(toText!!, Splash.languageCode[TranslatorActivity.toPos])

                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                TranslatorActivity.tts!!.speak(toText!!, TextToSpeech.QUEUE_FLUSH, bundle, null)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
                TranslatorActivity.tts!!.speak(toText!!, TextToSpeech.QUEUE_FLUSH, bundle, null)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
            }



        } else {

            val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.fromPos])

            if(cloudVoice==true) {
                callCloudVoice(toText!!, Splash.languageCode[TranslatorActivity.fromPos] )
            } else {
                //speaker(toText, Splash.languageCode[TranslatorActivity.fromPos])

                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                MainActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                MainActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
                MainActivity.tts!!.speak(toText, TextToSpeech.QUEUE_FLUSH, bundle, null)
                MainActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
            }



        }
    }

    fun automaticReviewHandler(type: Int, localCount: Int) {

        /**
         * 0 - immediate - ask only if its count is 5 or 10 overall in history
         * 1- chat - ask the chat count if 5 or 10 irrespective of the no of items in history
         */
        val fromLang: String?
        val toLang: String?
        val countString: String?
        val status5String: String?
        val status10String: String?
        val alreadyRated: String?
        var counterValue: Int

        val ct = context


        Log.e("entering review handler", "$type, $localCount")

        fromLang = TranslatorActivity.getSharedPrefs(ct, "fromReviewLang", "")
        toLang = TranslatorActivity.getSharedPrefs(ct, "toReviewLang", "")
        countString = TranslatorActivity.getSharedPrefs(ct, "reviewCount", "")
        status5String = TranslatorActivity.getSharedPrefs(ct, "submitReview5", "")
        status10String = TranslatorActivity.getSharedPrefs(ct, "submitReview10", "")
        alreadyRated = TranslatorActivity.getSharedPrefs(ct, "alreadyRated", "")

        counterValue = Integer.parseInt(countString!!)





        if (type == 0) {

            /**
             * increment counter Value
             * check for 5 only
             */
            counterValue = counterValue + 1
            TranslatorActivity.setSharedPrefs(ct, "reviewCount", "" + counterValue)

            if (counterValue == 5) {

                if (status5String!!.equals("n", ignoreCase = true)) {
                    //Ask Review
                    //Check if alreadyRated Exists
                    if (alreadyRated!!.equals("n", ignoreCase = true)) {
                        //show Dialog
                        showDialog(5)

                    }


                }

            } else if (counterValue == 10) {

                if (status5String!!.equals("n", ignoreCase = true) && status10String!!.equals("n", ignoreCase = true)) {
                    //Ask Review
                    //Check if alreadyRated Exists
                    if (alreadyRated!!.equals("n", ignoreCase = true)) {
                        //show Dialog
                        showDialog(10)

                    }


                }
            }
        } else if (type == 1) {

            Log.e("Checker inside", "$status5String, $status10String, $alreadyRated")

            if (status5String!!.equals("n", ignoreCase = true) && status10String!!.equals("n", ignoreCase = true)) {
                //Ask Review
                //Check if alreadyRated Exists
                if (alreadyRated!!.equals("n", ignoreCase = true)) {
                    //show Dialog
                    Log.e("inside ", "checks")
                    showDialog(localCount)

                }


            }


        }

    }

    fun showDialog(count: Int) {

        reviewCountValue = count

        AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Already Rated")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this application")
                .setDescription("Kindly share your Feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.text_yellow)
                .setNoteDescriptionTextColor(R.color.lightgrey)
                .setTitleTextColor(R.color.greyish)
                .setDescriptionTextColor(R.color.lightgrey)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.lightgrey)
                .setCommentTextColor(R.color.lightgrey)
                .setCommentBackgroundColor(R.color.iltogreyish)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(activity!!)
                .setTargetFragment(this, 0) // only if listener is implemented by fragment
                .show()


    }


    fun updateServer(type: String, id: String, stars: String, reviewMsg: String) {

        val cx = context

        Log.e("Inside server call", "Retrofit")

        val service = APIClient.getClient(cx!!).create<ApiInterface>(ApiInterface::class.java!!)


        val allData = service.setMyReviewInternal(type, id, stars, reviewMsg, Splash.languageName[TranslatorActivity.fromPos], Splash.languageName[TranslatorActivity.toPos])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        allData.subscribe(object : Observer<FCMDataResponse> {
            override fun onSubscribe(d: Disposable) {

                //donut_progress.setProgress(75);
            }

            override fun onNext(overallDataList: FCMDataResponse) {


                print("RESPONSE ---->" + overallDataList.status + "---" + overallDataList.message)
                Toast.makeText(cx, overallDataList.status + "---" + overallDataList.message, Toast.LENGTH_LONG)
                Log.e("Status", overallDataList.status)
                Log.e("Message", "" + overallDataList.message)

                if (overallDataList.status == "true") {

                    if (reviewCountValue == 5) {

                        TranslatorActivity.setSharedPrefs(cx, "submitReview5", "y")

                    } else if (reviewCountValue == 10) {

                        TranslatorActivity.setSharedPrefs(cx, "submitReview10", "y")
                    }


                    val alertDialog: LottieAlertDialog
                    alertDialog = LottieAlertDialog.Builder(cx, DialogTypes.TYPE_SUCCESS)
                            .setTitle("Wwalks Translator")
                            .setDescription("Thanks for your feedback!")
                            .setPositiveText("Ok")
                            .setPositiveButtonColor(Color.parseColor("#F99E84"))
                            .setPositiveTextColor(Color.parseColor("#FFFFFF"))


                            .setPositiveListener(object : ClickListener {

                                override fun onClick(dialog: LottieAlertDialog) {
                                    dialog.dismiss()
                                    //update Count

                                }

                            })
                            .build()

                    alertDialog.show()

                }
            }

            override fun onError(e: Throwable) {
                val cx = context
                Toast.makeText(cx, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

            }

            override fun onComplete() {

            }
        })


    }

    companion object {

        var convoText = "100"
        var alreadyConversing = 0


        //public static List<String> languageName = new ArrayList<String>();
        //public static List<String> languageCode = new ArrayList<String>();

        var dummyFrom: Int = 0
        var dummyTo = 9

        var historyList: ListView? = null
        var conversationList: ListView? = null

        var chatSide = 0

        var reviewCountValue = 0


        fun speakerFunction(text: String, languageCode: String) {
            val bundle = Bundle()
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)


            Splash.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            Splash.tts!!.language = Locale.forLanguageTag(languageCode)
        }
    }

    /*

    public void createFile(String outputText, String fromLog, String outputLang) {

        String f = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator +"WwalksTranslator.csv";
        String values = outputText + " *** " + fromLog +" *** " +outputLang;
        File file = new File(f);
        if(!file.exists()){
            try {
                file.createNewFile();
                FileWriter writer = (new FileWriter (file));

                writer.write(values);
                System.out.println(values);
                writer.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {


            try {
                //file.delete();
                //file.createNewFile();
                //FileWriter writer = (new FileWriter (file));

                FileWriter fw = new FileWriter(f, true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("\n" + values);

                //writer.write(values);
                System.out.println(values);
                bw.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

     */

    fun callCloudVoice(text: String, language: String) {

        val ct = context

        val service = APIClient1.getClient(ct!!).create(ApiInterface::class.java)

        val id = ""+TranslatorActivity.getSharedPrefs(ct, "myID", "")
        val type = ""+TranslatorActivity.getSharedPrefs(ct, "loginID", "")

        val allData = service.getMyAudio(text, language, id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        allData.subscribe(object : Observer<FCMDataResponseMedia> {
            override fun onSubscribe(d: Disposable) {

                //donut_progress.setProgress(75);
            }

            override fun onNext(overallDataList: FCMDataResponseMedia) {

                //System.out.print("RESPONSE ---->" + overallDataList.getStatus() +"---" + overallDataList.getUrl()  );
                Log.e("Status", overallDataList.status)
                Log.e("Message", "" + overallDataList.url)

                if (overallDataList.status.equals("true")) {

                    listenSpeechCloudOutput(overallDataList.url!!)

                }
            }

            override fun onError(e: Throwable) {
                Toast.makeText(context!!, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

            }

            override fun onComplete() {

            }
        })

    }

    fun listenSpeechCloudOutput(url: String) {

        Log.e("Audio URL", url)
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        Player().execute(url)

    }

    internal inner class Player : AsyncTask<String, Void, Boolean>() {



        override fun doInBackground(vararg strings: String): Boolean? {
            var prepared: Boolean? = false

            try {
                mediaPlayer!!.setDataSource(strings[0])

                mediaPlayer!!.setOnCompletionListener{
                    mediaPlayer!!.stop()
                    mediaPlayer!!.reset()
                }

                mediaPlayer!!.prepare()
                prepared = true

            } catch (e: Exception) {
                //Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false
            }

            return prepared
        }

        override fun onPostExecute(aBoolean: Boolean) {
            super.onPostExecute(aBoolean)

            mediaPlayer!!.start()

        }


        override fun onPreExecute() {
            super.onPreExecute()


        }
    }

    private fun checkLanguage(myLang: String): Boolean {
        Log.e("lang code", myLang)
        var result: Boolean = false
        for (i in Splash.cloudLanguageName.indices) {
            if (myLang == Splash.cloudLanguageCode[i].toString()) {
                result = true

                break
            }

        }

        return result

    }




}
