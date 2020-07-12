package com.wwalks.truediplomat.translator


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.APIClient1
import com.wwalks.truediplomat.API.ApiInterface

import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.R
import com.wwalks.truediplomat.Splash
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList
import java.util.Locale

class HistoryList(var context: Context, internal var translatorList: ArrayList<TranslationBean>) : BaseAdapter() {

    var mediaPlayer: MediaPlayer? = null

    override fun getCount(): Int {
        return translatorList.size
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

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(
                    R.layout.faq_list_item, null)
            holder = ViewHolder()

            holder.textView = convertView!!.findViewById<View>(R.id.text) as TextView
            holder.textView1 = convertView.findViewById<View>(R.id.text2) as TextView
            holder.date = convertView.findViewById<View>(R.id.date) as TextView
            holder.time = convertView.findViewById<View>(R.id.time) as TextView
            holder.delete = convertView.findViewById<View>(R.id.deleteConversation) as ImageView


            holder.textView!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.textView1!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.date!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)
            holder.time!!.typeface = MyFonts.getFont(context,
                    MyFonts.GORDITALIGHT)

            holder.cardLayout = convertView.findViewById<View>(R.id.cardLayout2) as CardView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val bean = ArrayList<TranslationBean.AnswersBean>()
        holder.textView!!.text = translatorList[position].answers!![0].fromText
        holder.textView1!!.text = translatorList[position].answers!![0].toText
        holder.date!!.text = translatorList[position].date
        holder.time!!.text = translatorList[position].time


        holder.textView!!.typeface = MyFonts.getFont(context,
                MyFonts.GORDITAMEDIUM)
        holder.textView1!!.typeface = MyFonts.getFont(context,
                MyFonts.GORDITAMEDIUM)
        holder.date!!.typeface = MyFonts.getFont(context,
                MyFonts.GORDITAMEDIUM)
        holder.time!!.typeface = MyFonts.getFont(context,
                MyFonts.GORDITAMEDIUM)

        holder.delete!!.setOnClickListener {
            TranslatorActivity.deleteConversation(context, translatorList, position)
            notifyDataSetChanged()
        }


        holder.cardLayout!!.setOnClickListener {
            if (translatorList[position].typeOfChat == "chat") {

                val intent = Intent(context, ChatViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("chat", "chat")
                intent.putExtra("number", "" + position)

                context.startActivity(intent)


            } else if (translatorList[position].typeOfChat == "immediate") {


                val text = translatorList[position].answers!![0].toText!!.toString()

                val cloudVoice = checkLanguage(translatorList[position].toLanguageCode!!)

                if(cloudVoice==true) {
                    callCloudVoice(text,translatorList[position].toLanguageCode!!)
                } else {
                    val bundle = Bundle()
                    bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                    Splash.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                    Splash.tts!!.language = Locale.forLanguageTag(translatorList[position].toLanguageCode)

                    Splash.tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, text)
                    Splash.tts!!.language = Locale.forLanguageTag(translatorList[position].toLanguageCode)
                }



            }
        }



        return convertView
    }


    internal inner class ViewHolder {
        var textView: TextView? = null
        var textView1: TextView? = null
        var date: TextView? = null
        var time: TextView? = null
        var imageView: ImageView? = null
        var checkSymbol: ImageView? = null
        var delete: ImageView? = null
        var layout: RelativeLayout? = null
        var cardLayout: CardView? = null
        var outer: RelativeLayout? = null
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



