package com.paasta.hiclass

import com.google.gson.GsonBuilder
import com.paasta.hiclass.model.UserData
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitClient {
    var gson = GsonBuilder()
        .setLenient()
        .create()

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        //url 은 ngrok 사용으로 계속 달라짐.
//        .client(okHttpClient)
        .baseUrl("https://hmys-hiclass.paas-ta.org")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    val retrofitservice: RetrofitService = retrofit.create(RetrofitService::class.java)
}

//서버로 보내는 INPUT데이터
interface RetrofitService {
    //베이스 URL 을 제외한 경로
    @FormUrlEncoded
    @POST("/app_login")
    fun requestLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<UserData>
    @FormUrlEncoded
    @POST("/app_signup")
    fun requestSignUp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("role") role: String,
    ): Call<UserData>
    @Multipart
    @POST("/app_profileimg")
    fun requestAddProfileImage(
        @Part image: MultipartBody.Part
    ): Call<String>
    @FormUrlEncoded
    @POST("/app_getprofileimg")
    fun requestProfileImage(
        @Field("email") email: String,
    ): Call<String>

    @Multipart
    @POST("home/app_checkimg")
    fun requestCheckImage(
        @Part image: MultipartBody.Part
    ): Call<String>

    @FormUrlEncoded
    @POST("home/app_enter_room")
    fun requestEnterRoom(
        @Field("roomname") roomname: String,
        @Field("password") password: String
    ):Call<String>

    @FormUrlEncoded
    @POST("home/app_checkin")
    fun requestcheckin(
        @Field("email") email: String
    ): Call<String>

    @FormUrlEncoded
    @POST("home/app_attendance")
    fun requestAttendance(
        @Field("room") room: String,
        @Field("number") number: String,
        @Field("name") name: String
    ):Call<String>

    @FormUrlEncoded
    @POST("home/app_sendcount")
    fun requestsendcount(
        @Field("email") email: String,
        @Field("count") count: Int,
        @Field("nonperson") nonperson: Int,
        @Field("roomname") roomname: String
    ): Call<String>
}