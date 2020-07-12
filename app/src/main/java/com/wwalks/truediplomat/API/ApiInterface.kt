package com.wwalks.truediplomat.API

import com.wwalks.truediplomat.translator.FCMDataResponseMedia
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("feedback.php")
    fun setMyReview(@Query("type") type: String, @Query("id") id: String, @Query("stars") stars: String, @Query("remarks") remarks: String): Observable<FCMDataResponse>

    @POST("feedback.php")
    fun setMyReviewInternal(@Query("type") type: String, @Query("id") id: String, @Query("stars") stars: String, @Query("remarks") remarks: String, @Query("from") fromLang: String, @Query("to") toLang: String): Observable<FCMDataResponse>

    @GET("cloudLanguages.php")
    fun getCloudLanguageList(): Observable<List<CloudLanguagesBean>>

    @POST("cloudtts.php")
    abstract fun getMyAudio(@Query("text") text: String, @Query("code") language: String, @Query("userid") userid: String): Observable<FCMDataResponseMedia>

}
