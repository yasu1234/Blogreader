package com.kumaydevelop.rssreader.Activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import com.kumaydevelop.rssreader.*
import com.kumaydevelop.rssreader.Model.BlogModel
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.add_site.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.util.concurrent.TimeUnit

class SiteAddActivity: AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_site)
        realm = Realm.getDefaultInstance()

        confirmButton.setOnClickListener {
            if (urlText.text.toString().isNullOrBlank()) {
                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                dialog.title = "URLを入力してください"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)
            } else {
                val args: Bundle = Bundle().also { it.putString("url",  urlText.text.toString())}
                supportLoaderManager.initLoader(0, args, getRssUrlCallback)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // LoaderCallbacksの関数をオーバーライドして、処理を変更した「オブジェクト」を作成する
    private val getRssUrlCallback : LoaderManager.LoaderCallbacks<Feed> = object : LoaderManager.LoaderCallbacks<Feed> {

        override fun onLoaderReset(loader: Loader<Feed>?) {
        }

        override fun onLoadFinished(loader: Loader<Feed>?, data: Feed?) {
            if (data?.feedUrl.isNullOrBlank()) {
                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                dialog.title = "Rss2.0形式に対応していません"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)
            } else {
                val args: Bundle = Bundle().also { it.putString("rssUrl",  data?.feedUrl)}
                supportLoaderManager.initLoader(1, args, getArticleCallbacks)
            }
        }

        override fun onCreateLoader(id: Int, args: Bundle?) = getRssUrl(this@SiteAddActivity, args!!.getString("url"))

    }

    private val getArticleCallbacks : LoaderManager.LoaderCallbacks<Rss> = object : LoaderManager.LoaderCallbacks<Rss> {

        override fun onLoaderReset(loader: Loader<Rss>?) {
        }

        override fun onLoadFinished(loader: Loader<Rss>?, data: Rss?) {
            if (data!!.title.isNullOrBlank()) {

                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                dialog.title = "Rss2.0形式に対応していません"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)
            } else {
                alert(data.title + "を登録しますか?") {
                    yesButton {
                        register(data)
                        createJob()
                        finish()
                    }
                    noButton { }
                }.show()
            }
        }
        override fun onCreateLoader(id: Int, args: Bundle?) = RssLoader(this@SiteAddActivity, args!!.getString("rssUrl"), Constants.NOT_USE_GET_ARTICLES)

    }

    // ブログの情報を登録する
    fun register(data: Rss) {
        realm.executeTransaction {
            val maxId = realm.where<BlogModel>().max("id")
            // 登録のときのIdは+1した状態にする
            val nextId = (maxId?.toLong() ?: 0L) + 1
            val blogData = realm.createObject<BlogModel>(nextId)
            blogData.title = data.title
            blogData.url = data.rssUrl
            blogData.lastUpdate = data.date
        }
    }

    // ブログ更新確認のジョブを作成する
    fun createJob() {
        createChannel(this)

        val fetchJob = JobInfo.Builder(
                1,
                ComponentName(this, PollingJob::class.java))
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()

        getSystemService(JobScheduler::class.java).schedule(fetchJob)
    }
}


