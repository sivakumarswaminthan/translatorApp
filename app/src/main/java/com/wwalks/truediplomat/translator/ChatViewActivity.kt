package com.wwalks.truediplomat.translator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity

import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.crashlytics.android.Crashlytics
import com.wwalks.truediplomat.MyFonts
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import com.google.gson.Gson
import com.jaeger.library.StatusBarUtil
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.APIClient1
import com.wwalks.truediplomat.API.ApiInterface
import com.wwalks.truediplomat.R
import com.wwalks.truediplomat.Splash

import java.util.ArrayList
import java.util.Locale

import io.fabric.sdk.android.Fabric
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class ChatViewActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    var chatNumber: Int = 0
    var back: ImageView? = null
    var btnLanguage1: ImageView? = null
    var btnLanguage2: ImageView? = null
    var conversedList: ListView? = null
    var fromLanguage: TextView? = null
    var toLanguage: TextView? = null
    var iconFromLanguage: TextView? = null
    var iconToLanguage: TextView? = null
    var title: TextView? = null
    var language1Layout: RelativeLayout? = null
    var language2Layout: RelativeLayout? = null
    var languageFirstLayout: RelativeLayout? = null
    var languageSecondLayout: RelativeLayout? = null
    var inputText: String? = null
    var outputText: String? = null
    private val REQ_CODE_SPEECH_INPUT = 100
    internal var conversationValue: ArrayList<TranslationBean>? = ArrayList()

    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getSupportActionBar().hide();
        StatusBarUtil.setTransparent(this@ChatViewActivity)

        setContentView(R.layout.conversation_view)
        Fabric.with(this, Crashlytics())

        chatNumber = Integer.parseInt(intent.getStringExtra("number"))

        language1Layout = findViewById<View>(R.id.lang1Layout) as RelativeLayout
        language2Layout = findViewById<View>(R.id.lang2Layout) as RelativeLayout
        languageFirstLayout = findViewById<View>(R.id.language1Layout) as RelativeLayout
        languageSecondLayout = findViewById<View>(R.id.language2Layout) as RelativeLayout
        myProgress = findViewById<View>(R.id.my_progressBar1) as ProgressBar

        conversedList = findViewById<View>(R.id.conversedList) as ListView
        back = findViewById<View>(R.id.backButton2) as ImageView
        btnLanguage1 = findViewById<View>(R.id.btnLanguage1) as ImageView
        btnLanguage2 = findViewById<View>(R.id.btnLanguage2) as ImageView
        title = findViewById<View>(R.id.title2) as TextView
        fromLanguage = findViewById<View>(R.id.fromValue1) as TextView
        toLanguage = findViewById<View>(R.id.toValue1) as TextView
        iconFromLanguage = findViewById<View>(R.id.language1) as TextView
        iconToLanguage = findViewById<View>(R.id.language2) as TextView

        val ct = this
        tts = TextToSpeech(ct, this, "com.google.android.tts")


        conversationValue = TranslatorActivity.getTranslationList(this@ChatViewActivity)

        fromLanguage!!.text = conversationValue!![chatNumber].fromLanguageName
        toLanguage!!.text = conversationValue!![chatNumber].toLanguageName
        iconFromLanguage!!.text = conversationValue!![chatNumber].fromLanguageName
        iconToLanguage!!.text = conversationValue!![chatNumber].toLanguageName

        fromLanguage!!.typeface = MyFonts.getFont(this@ChatViewActivity,
                MyFonts.GORDITALIGHT)
        toLanguage!!.typeface = MyFonts.getFont(this@ChatViewActivity,
                MyFonts.GORDITALIGHT)
        iconFromLanguage!!.typeface = MyFonts.getFont(this@ChatViewActivity,
                MyFonts.GORDITALIGHT)
        iconToLanguage!!.typeface = MyFonts.getFont(this@ChatViewActivity,
                MyFonts.GORDITALIGHT)
        title!!.typeface = MyFonts.getFont(this@ChatViewActivity,
                MyFonts.GORDITAREGULAR)


        val conversationAdapter = ConversationListAdapter(this@ChatViewActivity, conversationValue!!, chatNumber, 1, conversationValue!![chatNumber].fromLanguageCode!!, conversationValue!![chatNumber].toLanguageCode!!)
        conversedList!!.adapter = conversationAdapter
        conversationAdapter.notifyDataSetChanged()


        language1Layout!!.setOnClickListener {
            chatSide = 1
            promptSpeechInput()
        }

        language2Layout!!.setOnClickListener {
            chatSide = 2
            promptSpeechInput()
        }

        btnLanguage1!!.setOnClickListener {
            chatSide = 1
            promptSpeechInput()
        }

        btnLanguage2!!.setOnClickListener {
            chatSide = 2
            promptSpeechInput()
        }

        languageFirstLayout!!.setOnClickListener {
            chatSide = 1
            promptSpeechInput()
        }

        languageSecondLayout!!.setOnClickListener {
            chatSide = 2
            promptSpeechInput()
        }
        iconFromLanguage!!.setOnClickListener {
            chatSide = 1
            promptSpeechInput()
        }

        iconToLanguage!!.setOnClickListener {
            chatSide = 2
            promptSpeechInput()
        }

        back!!.setOnClickListener {
            val intent = Intent(this@ChatViewActivity, TranslatorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("previous", "chatPage")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            this@ChatViewActivity.startActivity(intent)
        }


    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        if (chatSide == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, conversationValue!![chatNumber].fromLanguageCode)
        } else if (chatSide == 2) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, conversationValue!![chatNumber].toLanguageCode)
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    for (i in result.indices) {
                        Log.e("input speech result", result[i].toString())
                    }

                    inputText = result[0]

                    if (chatSide == 1) {
                        translateText(1)
                    } else if (chatSide == 2) {
                        translateText(2)
                    }

                }
            }
        }

    }


    private fun translateText(chatSide: Int) {
        Log.e("Entering", "Old Dragon")
        val inputText1 = inputText

        if (inputText1!!.length == 0) {
            Toast.makeText(this@ChatViewActivity,
                    getString(R.string.noInputTranslate),
                    Toast.LENGTH_SHORT).show()
        } else {
            //translate(inputText);

            val translate = Translate3()
            translate.execute(inputText1, "" + chatSide, conversationValue!![chatNumber].chatName)

        }
    }


    internal inner class Translate3 : AsyncTask<String, Void, String>() {
        var chatside: String? = null
        var filename: String? = null

        override fun onPreExecute() {

            myProgress!!.visibility = View.VISIBLE
            myProgress!!.isIndeterminate = true
        }


        override fun doInBackground(vararg params: String): String {

            val text = params[0]
            chatside = params[1]
            filename = params[2]//text to translate
            val options = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyDIrmrFeWn2ztX75dTAtbRly9IQlmNBH-M")
                    .build()
            //Translate translate = TranslateOptions.getDefaultInstance().getService();
            val translate = options.service

            val translation: Translation

            if (chatside == "1") {
                translation = translate.translate(
                        text,
                        com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(conversationValue!![chatNumber].fromLanguageCode),
                        Translate.TranslateOption.targetLanguage(conversationValue!![chatNumber].toLanguageCode))

            } else {
                translation = translate.translate(
                        text,
                        com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(conversationValue!![chatNumber].toLanguageCode),
                        Translate.TranslateOption.targetLanguage(conversationValue!![chatNumber].fromLanguageCode))
            }




            return translation.translatedText
        }

        //this method will run after doInBackground execution
        override fun onPostExecute(result: String) {
            myProgress!!.visibility = View.GONE
            System.out.printf("Translation: %s%n", result)
            outputText = result
            listenSpeechOutput(outputText!!)
            savingTranslations(conversationValue!![chatNumber].chatName!!, "chat", "" + chatSide, inputText!!, outputText!!)

        }
    }


    fun savingTranslations(fileName: String, chatType: String, messageType: String, fromText: String, toText: String) {

        val addConversation = TranslationBean()
        val conversations = ArrayList<TranslationBean.AnswersBean>()
        Log.e("Inside save block", fileName)

        val translatorList1 = TranslatorActivity.getTranslationList(this@ChatViewActivity)

        addConversation.chatName = translatorList1!![chatNumber].chatName
        addConversation.date = translatorList1[chatNumber].date
        addConversation.time = translatorList1[chatNumber].time
        addConversation.fromLanguageName = translatorList1[chatNumber].fromLanguageName
        addConversation.toLanguageName = translatorList1[chatNumber].toLanguageName
        addConversation.fromLanguageCode = translatorList1[chatNumber].fromLanguageCode
        addConversation.toLanguageCode = translatorList1[chatNumber].toLanguageCode
        addConversation.typeOfChat = translatorList1[chatNumber].typeOfChat
        val answersBean = TranslationBean.AnswersBean()
        answersBean.fromText = fromText
        answersBean.toText = toText
        answersBean.typeOfMessage = messageType
        conversations.add(answersBean)

        var bean = ArrayList<TranslationBean.AnswersBean>()
        //val bean = translatorList1[chatNumber].answers
        bean = translatorList1[chatNumber].answers as ArrayList<TranslationBean.AnswersBean>

        bean.add(answersBean)
        addConversation.answers = bean

        val gson = Gson()
        Log.e("Parsed Value", gson.toJson(addConversation))

        val translatorList = TranslatorActivity.getTranslationList(this@ChatViewActivity)
        translatorList!![chatNumber] = addConversation

        TranslatorActivity.setSharedPrefs(this@ChatViewActivity, "translatorList", gson.toJson(translatorList))
        Log.e("Overall translator List", gson.toJson(translatorList))

        val conversationAdapter = ConversationListAdapter(this@ChatViewActivity, translatorList, chatNumber, 1, conversationValue!![chatNumber].fromLanguageCode!!, conversationValue!![chatNumber].toLanguageCode!!)
        conversedList!!.adapter = conversationAdapter
        conversationAdapter.notifyDataSetChanged()
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

    private fun listenSpeechOutput(outputText: String) {


        val text = outputText
        val fromLangCode = conversationValue!![chatNumber].fromLanguageCode
        val toLangCode = conversationValue!![chatNumber].toLanguageCode

        if(chatSide == 0 || chatSide == 1) {
            val cloudVoice = checkLanguage(toLangCode!!)
            if(cloudVoice==true) {

                callCloudVoice(text, toLangCode)
            } else {
                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                Log.e("Translating Lang ---->", toLangCode)
                Log.e("Translating Code ---->", toLangCode)
                Log.e("Dragon-1", text)


                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(toLangCode)


                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(toLangCode)
            }
        } else  {
            val cloudVoice = checkLanguage(fromLangCode!!)
            if(cloudVoice==true) {
                callCloudVoice(text, fromLangCode)
            } else {
                val bundle = Bundle()
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                Log.e("Dragon-2", text + "  " + fromLangCode)

                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(fromLangCode)

                TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                TranslatorActivity.tts!!.language = Locale.forLanguageTag(fromLangCode)

            }
        }


    }

    companion object {

        var chatSide = 0

        var tts: TextToSpeech? = null

        var myProgress: ProgressBar? = null
    }

    fun callCloudVoice(text: String, language: String) {

        val service = APIClient1.getClient(this@ChatViewActivity).create(ApiInterface::class.java)


        val id = ""+TranslatorActivity.getSharedPrefs(this@ChatViewActivity, "myID", "")
        val type = ""+TranslatorActivity.getSharedPrefs(this@ChatViewActivity, "loginID", "")

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
                Toast.makeText(this@ChatViewActivity, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

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
                mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener { mediaPlayer ->
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                })

                mediaPlayer!!.prepare()
                prepared = true

            } catch (e: Exception) {
                //Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false
            }

            return prepared
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)



            mediaPlayer!!.start()

        }

        override fun onPreExecute() {
            super.onPreExecute()


        }
    }

    private fun checkLanguage(myLang: String): Boolean {
        var result: Boolean = true
        for (i in Splash.cloudLanguageName.indices) {
            if (myLang == Splash.cloudLanguageCode[i].toString()) {
                result = true

                break
            } else {
                result = false
            }

        }

        return result

    }



}
