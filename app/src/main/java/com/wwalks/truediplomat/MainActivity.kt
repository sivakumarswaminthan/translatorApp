package com.wwalks.truediplomat

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.crashlytics.android.Crashlytics

import com.stepstone.apprating.AppRatingDialog
import com.wwalks.truediplomat.translator.TranslatorActivity
import com.wwalks.truediplomat.translator.TranslatorFragment

import java.util.Arrays
import java.util.Locale

import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fabric.with(this, Crashlytics())

        val ct = this
        //MainActivity.tts = TextToSpeech(ct, this)
        MainActivity.tts = TextToSpeech(ct, this, "com.google.android.tts")

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

        startActivity(Intent(this@MainActivity, TranslatorActivity::class.java).putExtra("previous", "Splash"))
        finish()

        /*
        if (checkPermission()) {
            //main logic or main code

            // . write your main code to execute, It will execute if the permission is already given.
            startActivity(new Intent(MainActivity.this, TranslatorActivity.class).putExtra("previous", "Splash"));
            finish();

        } else {
            requestPermission();
        }


         */


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    /*
    @Override
    public void onInit(int status) {
        Log.e("Available" , ""+ tts.getAvailableLanguages().toString());

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {



            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    */


    private fun checkPermission(): Boolean {

        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            false
        } else true
    }

    /*
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, TranslatorActivity.class).putExtra("previous", "Splash"));
                    finish();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });



                        }
                    }
                }
                break;
        }
    }


     */


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    companion object {

        var tts: TextToSpeech? = null
        private val PERMISSION_REQUEST_CODE = 200
    }


}
