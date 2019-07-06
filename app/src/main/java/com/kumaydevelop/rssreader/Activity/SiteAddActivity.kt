package com.kumaydevelop.rssreader.Activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.kumaydevelop.rssreader.*
import com.kumaydevelop.rssreader.Entity.BlogEntity
import com.kumaydevelop.rssreader.Interface.RssClient
import com.kumaydevelop.rssreader.Model.BlogModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_add_site.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SiteAddActivity: AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_site)
        // RSSのURL表示欄はタップできないようにする
        rssText.isEnabled = true
        rssText.isCursorVisible = false
        rssText.isFocusable = false
        progressBar.visibility = android.widget.ProgressBar.INVISIBLE
        confirmButton.setText("RssURL確認")
        realm = Realm.getDefaultInstance()

        confirmButton.setOnClickListener {
            if (urlText.text.toString().isNullOrBlank()) {
                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                dialog.title = "URLを入力してください"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)

            } else {
                if (confirmButton.text == "RssURL確認") {
                    progressBar.visibility = android.widget.ProgressBar.VISIBLE
                    // RSSのURLの取得を行う
                    val args: Bundle = Bundle().also { it.putString("url",  urlText.text.toString())}
                    supportLoaderManager.initLoader(0, args, getRssUrlCallback)
                } else {
                    progressBar.visibility = android.widget.ProgressBar.VISIBLE
                    // rssのURLを作成(ブログによって/以下が違うため動的に作成)
                    val url = rssText.text.toString().split("//")
                    val baseUrl = url.get(1).split("/").get(0)
                    val addUrl = url.get(1).split("/").get(1)
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                            .build()
                    val retrofit = Retrofit.Builder()
                            .baseUrl(url.get(0) + "//" + baseUrl + "/")
                            .client(client)
                            .addConverterFactory(SimpleXmlConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()

                    val response = retrofit.create(RssClient::class.java).get(addUrl)

                    // 非同期で記事を取得し、登録確認アラートを表示させる
                    response.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe( {
                                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                                val blog = it
                                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                                dialog.title = it.title + "を登録しますか?"
                                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                                    register(blog!!, rssText.text.toString())
                                    createJob()
                                    finish()
                                }
                                dialog.onCancelClickListener = DialogInterface.OnClickListener { dialog, which ->
                                }
                                dialog.show(supportFragmentManager, null)
                            }, {
                                Log.e("ERROR", it.cause.toString())
                                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                                dialog.title = "データを取得できませんでした。"
                                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                                }
                                dialog.show(supportFragmentManager, null)
                            })
                }
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // フォーカスが外れたときキーボードを非表示にする(URL入力欄)
        urlText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        focusView.requestFocus()
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // LoaderCallbacksの関数をオーバーライドして、処理を変更したコールバックオブジェクトを作成する
    private val getRssUrlCallback : LoaderManager.LoaderCallbacks<Rss> = object : LoaderManager.LoaderCallbacks<Rss> {

        override fun onLoaderReset(loader: Loader<Rss>?) {
        }

        override fun onLoadFinished(loader: Loader<Rss>?, data: Rss?) {
            if (data?.feedUrl.isNullOrBlank()) {
                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                var dialog = com.kumaydevelop.rssreader.AlertDialog()
                dialog.title = "Rss2.0形式に対応していません"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)
            } else {
                rssText.setText(data!!.feedUrl)
                confirmButton.setText("登録")
                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
            }
        }

        override fun onCreateLoader(id: Int, args: Bundle?) = getRssUrl(this@SiteAddActivity, args!!.getString("url"))

    }

    // ブログの情報を登録する
    fun register(data: BlogEntity, url: String) {
        realm.executeTransaction {
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
            val formatDate = formatter.parse(data.lastBuildDate)
            val maxId = realm.where<BlogModel>().max("id")
            // 登録のときのIdは+1した状態にする
            val nextId = (maxId?.toLong() ?: 0L) + 1
            val blogData = realm.createObject<BlogModel>(nextId)
            blogData.title = data.title!!
            blogData.url = url
            blogData.lastUpdate = formatDate
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


