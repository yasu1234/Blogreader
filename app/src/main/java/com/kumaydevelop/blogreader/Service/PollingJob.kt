package com.kumaydevelop.blogreader.Service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.util.Log
import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.General.Util
import com.kumaydevelop.blogreader.General.notifyUpdate
import com.kumaydevelop.blogreader.Model.BlogModel
import com.kumaydevelop.blogreader.Model.SettingModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

class PollingJob() : JobService() {

    private lateinit var realm: Realm

    override fun onStartJob(params: JobParameters?): Boolean {
        realm = Realm.getDefaultInstance()

        val blogs = realm.copyFromRealm(realm.where(BlogModel::class.java).findAll())!!
        val setting = realm.copyFromRealm(realm.where(SettingModel::class.java).findFirst())!!

        val preference = getSharedPreferences("job_preference", Context.MODE_PRIVATE)

        if (blogs.size != 0) {
            for (blog in blogs) {
                // rssのURLを作成(ブログによって/以下が違うため動的に作成)
                val splitedUrl = Util.splitUrl(blog.url)

                val response = Util.createRetrofit(splitedUrl)

                // 非同期で記事を取得し、最新記事があれば、通知を行う
                val dispose = response.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe( {
                            val lastFetchTime = preference.getLong("last_published_time" + blog.id.toString(), 0L)
                            val formatter = SimpleDateFormat(Constants.TIMEZONE_ISO, Locale.US)
                            val formatDate = formatter.parse(it.lastBuildDate)
                            // 最新の記事があれば通知を行う
                            if (lastFetchTime > 0 && lastFetchTime < formatDate.time) {
                                notifyUpdate(this, blog)

                                // 次回更新確認用に最終更新日時を保持する
                                preference.edit().putBoolean("isUpdate", true).apply()
                            }
                            preference.edit().putLong("last_published_time" + blog.id.toString(), formatDate.time).apply()
                        }, {
                            Log.e("ERROR", it.cause.toString())
                        })
                dispose.dispose()
                }
        }
        realm.close()

        return true
    }

    // 再スケジュールする
    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}