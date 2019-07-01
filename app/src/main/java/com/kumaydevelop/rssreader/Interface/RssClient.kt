package com.kumaydevelop.rssreader.Interface

import com.kumaydevelop.rssreader.Entity.BlogEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path


interface RssClient {

    @GET("{rssPath}")
    fun get(@Path("rssPath") path: String) : Observable<BlogEntity>
}