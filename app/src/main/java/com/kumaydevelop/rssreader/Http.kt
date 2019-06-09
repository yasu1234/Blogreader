package com.kumaydevelop.rssreader

import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getHttp(url:String): InputStream? {
    val con = URL(url).openConnection() as HttpURLConnection

    con.apply {
        requestMethod  = "GET"
        connectTimeout = 3000
        readTimeout = 5000
        instanceFollowRedirects = true
    }

    con.connect()

    if (con.responseCode in 200..209) {
        return BufferedInputStream(con.inputStream)
    }

    return null
}