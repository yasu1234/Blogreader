package com.kumaydevelop.rssreader

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream

fun getHttp(url:String): InputStream? {
    val client = OkHttpClient()
    try {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (response.body() != null) {
            Log.d("DEBUG", response.body()!!.string())
            return response.body()!!.byteStream()
        }
    } catch(e: IOException) {
        return null
    }

    return null
}