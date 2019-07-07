package com.kumaydevelop.rssreader.Activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import com.kumaydevelop.rssreader.Adapter.BlogAdapter
import com.kumaydevelop.rssreader.Constants
import com.kumaydevelop.rssreader.Dialog.AlertDialog
import com.kumaydevelop.rssreader.General.createChannel
import com.kumaydevelop.rssreader.Model.BlogModel
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.R
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var realm: Realm
    var count : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        realm = Realm.getDefaultInstance()
        val blogs = realm.where<BlogModel>().findAll()
        val setting = realm.where<SettingModel>().findAll()

        if (setting.size == 0) {
            realm.executeTransaction {
                // データがない場合は表示件数を50件、更新確認タイミングを1時間でデフォルト登録する
                val maxId = realm.where<SettingModel>().max("id")
                val nextId = (maxId?.toLong() ?: 0L) + 1
                val settingData = realm.createObject<SettingModel>(nextId)
                settingData.displayCountCode = Constants.DisplayCount.FIFTY.ordinal.toString()
                settingData.updateTimeCode = Constants.UpdateTime.HOUR.ordinal.toString()
                settingData.updateDate = Date()
                // 通知用のチャンネルを作成する
                createChannel(this)
            }

            count = Constants.DisplayCount.FIFTY.ordinal

        } else {
            val setting = realm.where<SettingModel>().findAll()
            count = Constants.DisplayCount.values().filter { it.ordinal.toString() == setting.get(0)!!.displayCountCode }.map { it.count }.get(0)
        }

        val preference = getSharedPreferences("job_preference", Context.MODE_PRIVATE)
        val isUpdate = preference.getBoolean("isUpdate", false)

        // 記事の更新があった場合は最終更新日時を更新して表示させる
        if (isUpdate) {
            for (blog in blogs) {
                val lastFetchTime = preference.getLong("last_published_time" + blog.id.toString(), 0L)
                if (lastFetchTime != 0L) {
                    realm.executeTransaction {
                        val date =  Date(lastFetchTime)
                        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        val date_string = sdf.format(date)
                        val formatDate = sdf.parse(date_string)
                        blog.lastUpdate = formatDate
                    }
                }
            }
            preference.edit().putBoolean("isUpdate", false).apply()
        }

        val listView = findViewById<ListView>(R.id.blogList)
        listView.adapter = BlogAdapter(blogs)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        // 記事一覧に遷移
        listView.setOnItemClickListener { parent, view, position, id ->
            val realmId = view.findViewById<TextView>(R.id.idView).text.toString()
            val rssData = realm.where<BlogModel>().equalTo("id", Integer.parseInt(realmId)).findFirst()
            startActivity<ListArticlesActivity>("RSSURL_KEY" to rssData!!.url, "DISPLAYCOUNT" to count)
        }

        // 長押しでブログ削除
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val title = view.findViewById<TextView>(R.id.titleView).text
            val realmId = view.findViewById<TextView>(R.id.idView).text.toString()
            var dialog = AlertDialog()
            dialog.title = title.toString() + "を削除しますか？"
            dialog.cancelText = "キャンセル"
            dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                val rssData = realm.where<BlogModel>().equalTo("id", Integer.parseInt(realmId)).findFirst()
                realm.executeTransaction {
                    rssData!!.deleteFromRealm()
                }
                val adapter = listView.adapter as BlogAdapter
                adapter.notifyDataSetChanged()
            }
            dialog.show(supportFragmentManager, null)

            return@setOnItemLongClickListener true

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    // ナビゲーションメニューの選択時の処理
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // ブログ追加
            R.id.nav_camera -> {
                val intent = Intent(this, SiteAddActivity::class.java)
                startActivity(intent)
            }
            // 表示件数設定
            R.id.nav_gallery -> {
                val intent = Intent(this, CountSettingActivity::class.java)
                startActivity(intent)
            }
            // 更新確認頻度設定
            R.id.nav_slideshow -> {
                val intent = Intent(this, UpdateSettingActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

