package com.kumaydevelop.blogreader.General

import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.Entity.BlogEntity
import com.kumaydevelop.blogreader.Interface.RssClient
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

object Util {

    // URLをドメインまでとそれ以降に分ける
    fun splitUrl(url: String) : List<String> {
        val urlList: MutableList<String> = mutableListOf()
        val fullUrl = url.split(Constants.DOUBLE_SLASH)
        val domain = fullUrl.get(1).split(Constants.SLASH).get(0)
        val path = fullUrl.get(1).split(Constants.SLASH).get(1)

        urlList.add(fullUrl.get(0) + Constants.DOUBLE_SLASH + domain + Constants.SLASH)
        urlList.add(path)

        return urlList
    }

    // Retrofitの実行
    fun createRetrofit(url: List<String>): Observable<BlogEntity> {
        // Retrofitの設定を行う
        val retrofit = Retrofit.Builder()
                .baseUrl(url.get(0))
                .client(OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        // xmlの要素を取得する処理
        val response = retrofit.create(RssClient::class.java).get(url.get(1))

        return response
    }

    // 更新頻度の設定
    fun setUpdateTime(updateTimeCode : String) : Long {
        when (updateTimeCode) {
            Constants.UpdateTime.FIFTEENMINUTES.ordinal.toString() -> {
                return TimeUnit.MINUTES.toMillis(15)
            }
            Constants.UpdateTime.THIRTYMINUTES.ordinal.toString() -> {
                return TimeUnit.MINUTES.toMillis(30)
            }
            Constants.UpdateTime.HOUR.ordinal.toString() -> {
                return TimeUnit.HOURS.toMillis(1)
            }
            Constants.UpdateTime.THREEHOURS.ordinal.toString() -> {
                return TimeUnit.HOURS.toMillis(3)
            }
            Constants.UpdateTime.SIXHOURS.ordinal.toString() -> {
                return TimeUnit.HOURS.toMillis(6)
            }
            Constants.UpdateTime.TWENTYHOUES.ordinal.toString() -> {
                return TimeUnit.HOURS.toMillis(12)
            }
            Constants.UpdateTime.DAY.ordinal.toString() -> {
                return TimeUnit.HOURS.toMillis(24)
            }
        }
        // 設定ミスの場合は1時間で設定
        return TimeUnit.HOURS.toMillis(1)
    }
}