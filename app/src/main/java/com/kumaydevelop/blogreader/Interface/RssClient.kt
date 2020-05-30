package com.kumaydevelop.blogreader.Interface

import com.kumaydevelop.blogreader.Entity.BlogEntity
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface RssClient {

    @GET("{rssPath}")
    fun get(@Path("rssPath") path: String) : Observable<BlogEntity>

    @GET("{rssPath}")
    suspend fun getRss(@Path("rssPath") path: String) : BlogEntity
}