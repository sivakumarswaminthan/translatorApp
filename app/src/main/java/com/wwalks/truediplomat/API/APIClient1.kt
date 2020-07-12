package com.wwalks.truediplomat.API

import android.content.Context

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient1 {
    private var retrofit: Retrofit? = null
    private val REQUEST_TIMEOUT = 60
    private var okHttpClient: OkHttpClient? = null
    //var BASE_URL = "http://172.104.94.239/truediplomat/"
    var BASE_URL = "http://jirafff.com/wwalks_translator/"


    public var gson: Gson? = null

    private val token: String
        get() {

            val mUser = FirebaseAuth.getInstance().currentUser

            try {
                val task = mUser!!.getIdToken(false)
                val tokenResult = Tasks.await(task)
                val idToken = tokenResult.token
                return idToken ?: ""

            } catch (e: ExecutionException) {
                return ""
            } catch (e: InterruptedException) {
                return ""
            }

        }

    fun getClient(context: Context): Retrofit {


        if (okHttpClient == null)
            initOkHttp(context)



        if (retrofit == null) {


            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient!!)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        }
        return retrofit!!
    }


    private fun initOkHttp(context: Context) {
        val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + "PODA")


            val request = requestBuilder.build()
            chain.proceed(request)
        }

        okHttpClient = httpClient.build()
    }

}