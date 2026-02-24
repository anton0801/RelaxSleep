package com.relaxationapp.relaxsleep.data.domain.data

import android.util.Log
import com.relaxationapp.relaxsleep.MainApplication.Companion.SLEEPING_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.relaxationapp.relaxsleep.data.domain.model.FeedMIxEntity
import com.relaxationapp.relaxsleep.data.domain.model.FeedMixParam
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.lang.Exception

interface FeedMixLabelApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun feedMixGetClient(
        @Body jsonString: JsonObject,
    ): Call<FeedMIxEntity>
}


private const val FEED_MIX_SITELI = "https://rellaxsleep.com/"

class SleepRelaxRepositoryImpl {

    suspend fun feedMixAppGetClient(
        feedMixParam: FeedMixParam,
        eggLabelConversion: MutableMap<String, Any>?
    ): FeedMIxEntity? {
        val gson = Gson()
        val api = feedMixAppAGetApi(FEED_MIX_SITELI, null)

        val eggLabelJsonObject = gson.toJsonTree(feedMixParam).asJsonObject
        eggLabelConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            eggLabelJsonObject.add(key, element)
        }
        return try {
            val eggLabelRequest: Call<FeedMIxEntity> = api.feedMixGetClient(
                jsonString = eggLabelJsonObject,
            )
            val eggLabelResult = eggLabelRequest.awaitResponse()
            if (eggLabelResult.code() == 200) {
                eggLabelResult.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(SLEEPING_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun feedMixAppAGetApi(url: String, client: OkHttpClient?): FeedMixLabelApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
