package com.wwalks.truediplomat.translator

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.cardview.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.APIClient1
import com.wwalks.truediplomat.API.ApiInterface

import com.wwalks.truediplomat.MainActivity
import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.R
import com.wwalks.truediplomat.Splash
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList
import java.util.Locale


class ConversationListAdapter(var context: Context, internal var translatorList: ArrayList<TranslationBean>, var value: Int, var spacer: Int, var from: String, var to: String) : BaseAdapter() {
    var bundle = Bundle()
    var mediaPlayer: MediaPlayer? = null

    override fun getCount(): Int {
        return translatorList[value].answers!!.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        //tts = new TextToSpeech(context, this);
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(
                    R.layout.chat_list_item, null)
            holder = ViewHolder()

            holder.textView = convertView!!.findViewById<View>(R.id.text) as TextView
            holder.textView1 = convertView.findViewById<View>(R.id.text2) as TextView
            holder.cardLayout1 = convertView.findViewById<View>(R.id.cardLayout2) as CardView

            holder.textView11 = convertView.findViewById<View>(R.id.text11) as TextView
            holder.textView21 = convertView.findViewById<View>(R.id.text21) as TextView
            holder.cardLayout2 = convertView.findViewById<View>(R.id.cardLayout3) as CardView

            holder.textView!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView1!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView11!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView21!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)

            holder.textView!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView1!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView11!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView21!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)




            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }


        Log.e("Value: $value", "Position: $position")
        if (translatorList[value].answers!![position].typeOfMessage == "1") {
            holder.cardLayout1!!.visibility = View.VISIBLE
            holder.cardLayout2!!.visibility = View.GONE
            holder.textView!!.text = translatorList[value].answers!![position].fromText
            holder.textView1!!.text = translatorList[value].answers!![position].toText


            holder.textView!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITAMEDIUM)
            holder.textView1!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITAMEDIUM)

            holder.cardLayout1!!.setOnClickListener {
                //speak function enabled

                val text = translatorList[value].answers!![position].toText!!.toString()
                Log.e("Translating Lang ---->", Splash.languageName[TranslatorActivity.toPos])
                Log.e("Translating Code ---->", Splash.languageCode[TranslatorActivity.toPos])

                if (spacer == 1) {

                    val cloudVoice = checkLanguage(to)

                    if(cloudVoice==true) {
                        callCloudVoice(text,to)
                    } else {
                        val bundle = Bundle()
                        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                        TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null)
                        TranslatorActivity.tts!!.language = Locale.forLanguageTag(to)
                    }



                } else {

                    val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.toPos])


                    if(cloudVoice==true) {
                        callCloudVoice(text,Splash.languageCode[TranslatorActivity.toPos])
                    } else {
                        val bundle = Bundle()
                        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                        TranslatorActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null)
                        TranslatorActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.toPos])
                    }


                }
            }
        } else if (translatorList[value].answers!![position].typeOfMessage == "2") {


            holder.cardLayout1!!.visibility = View.GONE
            holder.cardLayout2!!.visibility = View.VISIBLE

            holder.textView11!!.text = translatorList[value].answers!![position].fromText
            holder.textView21!!.text = translatorList[value].answers!![position].toText


            holder.textView11!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITAMEDIUM)
            holder.textView21!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITAMEDIUM)


            holder.cardLayout2!!.setOnClickListener {
                //speak function enabled
                val text = translatorList[value].answers!![position].toText!!.toString()

                Log.e("Translating Lang ---->", Splash.languageName[TranslatorActivity.fromPos])
                Log.e("Translating Code ---->", Splash.languageCode[TranslatorActivity.fromPos])

                if (spacer == 1) {

                    val cloudVoice = checkLanguage(from)

                    if(cloudVoice==true) {
                        callCloudVoice(text,from)
                    } else {
                        val bundle = Bundle()
                        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                        MainActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null)
                        MainActivity.tts!!.language = Locale.forLanguageTag(from)
                    }



                } else {
                    val cloudVoice = checkLanguage(Splash.languageCode[TranslatorActivity.fromPos])


                    if(cloudVoice==true) {
                        callCloudVoice(text,Splash.languageCode[TranslatorActivity.fromPos])
                    } else {
                        val bundle = Bundle()
                        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)

                        MainActivity.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, null)
                        MainActivity.tts!!.language = Locale.forLanguageTag(Splash.languageCode[TranslatorActivity.fromPos])
                    }


                }
            }
        }



        return convertView
    }


    internal inner class ViewHolder {
        var textView: TextView? = null
        var textView1: TextView? = null
        var textView11: TextView? = null
        var textView21: TextView? = null
        var cardLayout1: CardView? = null
        var cardLayout2: CardView? = null

    }


    fun callCloudVoice(text: String, language: String) {

        val service = APIClient1.getClient(context).create(ApiInterface::class.java)


        val id = ""+TranslatorActivity.getSharedPrefs(context, "myID", "")
        val type = ""+TranslatorActivity.getSharedPrefs(context, "loginID", "")
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
                Toast.makeText(context, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

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