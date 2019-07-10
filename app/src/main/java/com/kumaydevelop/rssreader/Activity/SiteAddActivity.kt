package com.kumaydevelop.rssreader.Activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.kumaydevelop.rssreader.Constants
import com.kumaydevelop.rssreader.Dialog.AlertDialog
import com.kumaydevelop.rssreader.Entity.BlogEntity
import com.kumaydevelop.rssreader.General.Rss
import com.kumaydevelop.rssreader.General.Util
import com.kumaydevelop.rssreader.General.getRssUrl
import com.kumaydevelop.rssreader.Model.BlogModel
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.R
import com.kumaydevelop.rssreader.Service.PollingJob
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_add_site.*
import java.text.SimpleDateFormat
import java.util.*

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
        confirmButton.setText(Constants.RSS_CONFIRM)
        realm = Realm.getDefaultInstance()
        val setting = realm.where<SettingModel>().findFirst()!!

        confirmButton.setOnClickListener {
            if (urlText.text.toString().isNullOrBlank()) {
                var dialog = AlertDialog()
                dialog.title = "URLを入力してください"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)

            } else if (urlText.text.toString().trim().length < 10
                    || !(urlText.text.toString().trim().substring(0,7) == Constants.HTTP
                    || urlText.text.toString().trim().substring(0,8) == Constants.HTTPS)) {
                var dialog = AlertDialog()
                dialog.title = "URL形式に誤りがあります"
                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                }
                dialog.show(supportFragmentManager, null)
            } else {
                // RSSのURLを取得時の処理
                if (confirmButton.text == Constants.RSS_CONFIRM) {
                    progressBar.visibility = android.widget.ProgressBar.VISIBLE
                    // RSSのURLの取得を行う
                    val args: Bundle = Bundle().also { it.putString("url",  urlText.text.toString())}
                    // 誤ったURL入力後に別のURLを確認できるようにrestartLoaderを使う
                    supportLoaderManager.restartLoader(0, args, getRssUrlCallback)
                } else {
                    progressBar.visibility = android.widget.ProgressBar.VISIBLE
                    // rssのURLを作成(ブログによって/以下が違うため動的に作成)
                    val splitedUrl = Util.splitUrl(rssText.text.toString().trim())

                    val response = Util.createRetrofit(splitedUrl)

                    // 非同期で記事を取得し、登録確認アラートを表示させる
                    response.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe( {
                                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                                val blog = it
                                var dialog = AlertDialog()
                                dialog.title = it.title + "を登録しますか?"
                                dialog.cancelText = "キャンセル"
                                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                                    register(blog!!, rssText.text.toString())
                                    createJob(setting)
                                    finish()
                                }
                                dialog.onCancelClickListener = DialogInterface.OnClickListener { dialog, which ->
                                }
                                dialog.show(supportFragmentManager, null)
                            }, {
                                Log.e("ERROR", it.cause.toString())
                                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                                var dialog = AlertDialog()
                                dialog.title = "データを取得できませんでした。"
                                dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                                }
                                dialog.show(supportFragmentManager, null)
                            })
                }
            }
        }

        // ブログ一覧に戻る
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
                val handler = Handler()
                handler.post {
                    var dialog = AlertDialog()
                    dialog.title = "データを取得できませんでした。"
                    dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                    }
                    dialog.show(supportFragmentManager, null)
                }
            } else {
                // RSSのURLを取得できた場合は登録ボタンに変更する
                rssText.setText(data!!.feedUrl)
                confirmButton.setText(Constants.REGISTER)
                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
            }
        }

        override fun onCreateLoader(id: Int, args: Bundle?) = getRssUrl(this@SiteAddActivity, args!!.getString("url"))

    }

    // ブログの情報を登録する
    fun register(data: BlogEntity, url: String) {
        realm.executeTransaction {
            val formatter = SimpleDateFormat(Constants.TIMEZONE_ISO, Locale.US)
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
    fun createJob(setting : SettingModel) {

        val fetchJob = JobInfo.Builder(
                1,
                ComponentName(this, PollingJob::class.java))
                .setPeriodic(Util.setUpdateTime(setting.updateTimeCode))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()

        getSystemService(JobScheduler::class.java).schedule(fetchJob)
    }
}


