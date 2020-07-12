package com.wwalks.truediplomat.translator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.core.app.ShareCompat

//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.ShareCompat;
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.crashlytics.android.Crashlytics
import com.wwalks.truediplomat.API.APIClient
import com.wwalks.truediplomat.API.FCMDataResponse
import com.wwalks.truediplomat.MyFonts
import com.wwalks.truediplomat.PrivacyActivity
import com.wwalks.truediplomat.R
import com.wwalks.truediplomat.Splash
import com.jaeger.library.StatusBarUtil
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener

import java.util.ArrayList
import java.util.Arrays

import `in`.galaxyofandroid.spinerdialog.OnSpinerItemClick
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import com.google.protobuf.TextFormat.print
import com.wwalks.truediplomat.API.ApiInterface
import io.fabric.sdk.android.Fabric
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
//import com.wwalks.truediplomat.API.ApiInterface as ApiInterface

class SettingsPage : FragmentActivity(), RatingDialogListener {

    var back: ImageView ? = null
    var editPref: ImageView? = null
    var title: TextView? = null
    var privacyPolicyText: TextView? = null
    var prefLangaugeText: TextView? = null
    var myLanguage: TextView? = null
    var ratingText: TextView? = null
    var shareText: TextView? = null
    var shareCarrier: TextView? = null
    var ratingCarrier: TextView? = null
    var emailText: TextView? = null
    var emailCarrier: TextView? = null
    var privacyPolicyLayout: RelativeLayout? = null
    var ratingLayout: RelativeLayout? = null
    var shareLayout: RelativeLayout? = null
    var emailLayout: RelativeLayout? = null
    var cr: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getSupportActionBar().hide();
        StatusBarUtil.setTransparent(this@SettingsPage)

        setContentView(R.layout.settings)
        Fabric.with(this, Crashlytics())

        cr = this

        back = findViewById<View>(R.id.backButton2) as ImageView
        editPref = findViewById<View>(R.id.editButton) as ImageView

        title = findViewById<View>(R.id.title2) as TextView
        myLanguage = findViewById<View>(R.id.prefLangTitle) as TextView
        prefLangaugeText = findViewById<View>(R.id.carrier) as TextView
        privacyPolicyText = findViewById<View>(R.id.privacyPolicyTitle) as TextView
        ratingText = findViewById<View>(R.id.ratingText) as TextView
        shareText = findViewById<View>(R.id.shareText) as TextView
        shareCarrier = findViewById<View>(R.id.shareCarrier) as TextView
        //ratingCarrier = (TextView) findViewById(R.id.ratingText);
        emailText = findViewById<View>(R.id.emailText) as TextView
        emailCarrier = findViewById<View>(R.id.emailCarrier) as TextView

        privacyPolicyLayout = findViewById<View>(R.id.privacyPolicyLayout) as RelativeLayout
        ratingLayout = findViewById<View>(R.id.ratingLayout) as RelativeLayout
        shareLayout = findViewById<View>(R.id.shareLayout) as RelativeLayout
        emailLayout = findViewById<View>(R.id.emailLayout) as RelativeLayout


        title!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITALIGHT)

        shareCarrier!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITALIGHT)
        //ratingCarrier.setTypeface(MyFonts.getFont(SettingsPage.this,
        //        MyFonts.GORDITALIGHT));
        emailCarrier!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITALIGHT)
        prefLangaugeText!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITALIGHT)

        privacyPolicyText!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITAMEDIUM)
        ratingText!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITAMEDIUM)
        myLanguage!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITAMEDIUM)
        shareText!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITAMEDIUM)
        emailText!!.typeface = MyFonts.getFont(this@SettingsPage,
                MyFonts.GORDITAMEDIUM)

        val pos = Integer.parseInt(TranslatorActivity.getSharedPrefs(this@SettingsPage, "fromPos", "")!!)
        //int pos = 15;
        prefLangaugeText!!.text = Splash.languageName[pos].toString()


        back!!.setOnClickListener {
            val intent = Intent(this@SettingsPage, TranslatorActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("previous", "chatPage")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            this@SettingsPage.startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        var xyz = ArrayList<String>()
        xyz = Splash.languageName as ArrayList<String>

        val spinnerDialog: SpinnerDialog
        spinnerDialog = SpinnerDialog(this@SettingsPage, xyz, "Select your input language", "Close")
        spinnerDialog.setCancellable(true) // for cancellable
        spinnerDialog.setShowKeyboard(false)// for open keyboard by default

        spinnerDialog.bindOnSpinerListener { item, position ->
            Toast.makeText(this@SettingsPage, "$item  $position", Toast.LENGTH_SHORT).show()
            TranslatorActivity.fromPos = position
            TranslatorFragment.dummyFrom = position

            TranslatorActivity.setSharedPrefs(this@SettingsPage, "fromPos", "" + position)

            prefLangaugeText!!.text = item


            val intent = Intent(this@SettingsPage, SettingsPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            this@SettingsPage.startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        editPref!!.setOnClickListener {
            spinnerDialog.showSpinerDialog()
            //post complete Selection
        }

        privacyPolicyLayout!!.setOnClickListener { privacyPolicy() }

        privacyPolicyText!!.setOnClickListener { privacyPolicy() }

        ratingLayout!!.setOnClickListener { rateAndReview() }

        ratingText!!.setOnClickListener { rateAndReview() }

        shareLayout!!.setOnClickListener { sharing() }

        shareText!!.setOnClickListener { sharing() }

        emailText!!.setOnClickListener { mailing() }

        emailLayout!!.setOnClickListener { mailing() }


    }

    fun sharing() {
        ShareCompat.IntentBuilder.from(this@SettingsPage)
                .setType("text/plain")
                .setChooserTitle("Share Wwalks App with")
                .setText("http://play.google.com/store/apps/details?id=$packageName")
                .startChooser()
    }

    fun privacyPolicy() {
        val intent = Intent(this@SettingsPage, PrivacyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra("previous", "chatPage")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        this@SettingsPage.startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun rateAndReview() {

        val cp = this@SettingsPage

        AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
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
                .create(cp)
                //.setTargetFragment(this, "Rater") // only if listener is implemented by fragment
                .show()

        /*
        String package1 = "com.example.truediplomat";
        Log.e("My Package", getPackageName());

        try {
            Uri uri = Uri.parse("market://details?id="+getPackageName()+"");
            Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goMarket);
        }catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()+"");
            Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goMarket);
        }
        */

    }

    fun mailing() {
        //
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("david@wwalks.com")) //
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Wwalks Translator")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(intent, ""))

    }

    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {

    }

    override fun onPositiveButtonClicked(i: Int, s: String) {


        val stars = "" + i


        val id = ""+TranslatorActivity.getSharedPrefs(this@SettingsPage, "myID", "")
        //String fcm = TranslatorActivity.getSharedPrefs(SettingsPage.this, "fcmToken", "");
        val type = ""+TranslatorActivity.getSharedPrefs(this@SettingsPage, "loginID", "")

        updateServer(type, id, stars, s)


    }

    fun updateServer(comment: String, id1: String, stars: String, reviewMsg: String) {


        val service = APIClient.getClient(this).create<ApiInterface>(ApiInterface::class.java)



        val allData = service.setMyReview(comment, id1, stars, reviewMsg)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())


        /*
        val allData = service.setMyReview("phone", "123456", stars, reviewMsg)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

         */

        allData.subscribe(object : Observer<FCMDataResponse> {
            override fun onSubscribe(d: Disposable) {

                //donut_progress.setProgress(75);
            }

            override fun onNext(overallDataList: FCMDataResponse) {

                Log.e("RESPONSE ---->" + overallDataList.status,  "---" + overallDataList.message)
                Toast.makeText(this@SettingsPage, overallDataList.status + "---" + overallDataList.message, Toast.LENGTH_LONG)
                Log.e("Status", overallDataList.status)
                Log.e("Message", "" + overallDataList.message)

                if (overallDataList.status == "true") {

                    val alertDialog: LottieAlertDialog
                    alertDialog = LottieAlertDialog.Builder(this@SettingsPage, DialogTypes.TYPE_SUCCESS)
                            .setTitle("Wwalks Translator")
                            .setDescription("Thanks for your feedback!")
                            .setPositiveText("Ok")
                            .setPositiveButtonColor(Color.parseColor("#F99E84"))
                            .setPositiveTextColor(Color.parseColor("#FFFFFF"))


                            .setPositiveListener(object : ClickListener {

                                override fun onClick(dialog: LottieAlertDialog) {
                                    dialog.dismiss()
                                }

                            })
                            .build()

                    alertDialog.show()

                }
            }

            override fun onError(e: Throwable) {
                Toast.makeText(this@SettingsPage, "Something went wrong " + e.message, Toast.LENGTH_LONG).show()

            }

            override fun onComplete() {

            }
        })


    }
}
