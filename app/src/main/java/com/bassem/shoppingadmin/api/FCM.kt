package com.bassem.shoppingadmin.api

import com.bassem.shoppingadmin.api.CONSTANTS.Companion.token
import com.bassem.shoppingadmin.ui.orders.Tracking
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface FCM {

    @Headers("Authorization:$token","Content-Type:application/json")
    @POST("send")
    fun sendNotification(@Body map: HashMap<String, String>): Call<JsonObject>

    companion object {
        val BASE_URL = "https://fcm.googleapis.com/fcm/"
        fun create(): FCM {
            val api = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
                BASE_URL
            ).build()
            return api.create(FCM::class.java)
        }

    }
}