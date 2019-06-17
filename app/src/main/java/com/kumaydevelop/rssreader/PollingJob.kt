package com.kumaydevelop.rssreader

import android.app.job.JobParameters
import android.app.job.JobService
import com.kumaydevelop.rssreader.Model.BlogModel
import com.kumaydevelop.rssreader.Model.SettingModel
import io.realm.Realm

class PollingJob() : JobService() {

    private lateinit var realm: Realm

    override fun onStartJob(params: JobParameters?): Boolean {
        realm = Realm.getDefaultInstance()

        val blogs = realm.copyFromRealm(realm.where(BlogModel::class.java).findAll())!!
        val setting = realm.copyFromRealm(realm.where(SettingModel::class.java).findFirst())!!

        val count = Constants.DisplayCount.values().filter { it.code == setting.displayCountCode }.map { it.count }.get(0)
        if (blogs.size != 0) {
            for (blog in blogs) {
            Thread {
                    val response = getHttp(blog.url)

                    if (response != null) {
                        val rss = parseRss(response, blog.url, count)

                        if (blog.lastUpdate.after(rss.date)) {
                            realm.executeTransactionAsync {
                                blog.lastUpdate = rss.date
                            }
                            notifyUpdate(this, blog)
                        }
                    }
                }.start()
            }
        }
        realm.close()

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}