package com.example.vanatherm.apiInteraction

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response

class ResponseBodyToClass {
    /**
     * Returns object in given class
     * @param response API response
     * @param toClass class we want our response body turn to
     */
    fun <T : Any> responseBodyToClass(response: Response<T>, toClass: Class<T>): T {
        val gson = Gson()
        try {
            val jsonBody = gson.toJson(response.body())
            return gson.fromJson(jsonBody.toString(), toClass)
        } catch (e: Exception) {
            Log.d("API", e.message.toString())
        }
        return gson.fromJson("", toClass)

    }
}