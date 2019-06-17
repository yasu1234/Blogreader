package com.kumaydevelop.rssreader.Activity

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
import com.kumaydevelop.rssreader.Model.BlogModel
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.R
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.startActivity
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
                settingData.displayCountCode = "4"
                settingData.updateTimeCode = "2"
                settingData.updateDate = Date()
            }

            count = 50

        } else {
            val setting = realm.where<SettingModel>().findAll()
            count = Constants.DisplayCount.values().filter { it.code == setting.get(0)!!.displayCountCode }.map { it.count }.get(0)
        }


        val listView = findViewById<ListView>(R.id.blogList)
        listView.adapter = BlogAdapter(blogs)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        listView.setOnItemClickListener { parent, view, position, id ->
            val title = view.findViewById<TextView>(android.R.id.text1)
            val rssData = realm.where<BlogModel>().equalTo("title", title.text.toString()).findFirst()
            startActivity<ListArticlesActivity>("RSSURL_KEY" to rssData!!.url, "DISPLAYCOUNT" to count)
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
            R.id.nav_camera -> {
                val intent = Intent(this, SiteAddActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_gallery -> {
                val intent = Intent(this, CountSettingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_slideshow -> {
                val intent = Intent(this, UpdateSettingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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

