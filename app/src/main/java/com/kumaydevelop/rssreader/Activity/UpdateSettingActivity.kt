package com.kumaydevelop.rssreader.Activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import com.kumaydevelop.rssreader.Constants
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.PollingJob
import com.kumaydevelop.rssreader.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_setting_count.*
import java.util.*
import java.util.concurrent.TimeUnit

class UpdateSettingActivity:  AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting_update)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        realm = Realm.getDefaultInstance()

        val setting = realm.where<SettingModel>().findFirst()!!
        // 表示時に登録データでチェックする
        setRadioChecked(setting.updateTimeCode)

        saveButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)
            val index = radioGroup.indexOfChild(radioButton)
            val data = realm.where<SettingModel>().equalTo("id", 1L).findFirst()!!
            realm.executeTransaction {
                data.updateTimeCode = index.toString()
                data.updateDate = Date()
            }

            val fetchJob = JobInfo.Builder(
                    1,
                    ComponentName(this, PollingJob::class.java))
                    .setPeriodic(setUpdateTime(data.updateTimeCode))
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build()

            getSystemService(JobScheduler::class.java).schedule(fetchJob)

            var dialog = com.kumaydevelop.rssreader.AlertDialog()
            dialog.title = "更新しました"
            dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                finish()
            }
            dialog.onCancelClickListener = DialogInterface.OnClickListener { dialog, which ->
            }
            dialog.show(supportFragmentManager, null)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // 初期表示時に登録している件数をチェックしている状態にする
    fun setRadioChecked(updateTimeCode : String) {
        when (updateTimeCode) {
            Constants.UpdateTime.FIFTEENMINUTES.code -> {
                radioGroup.check(R.id.Radio15minutes)
            }
            Constants.UpdateTime.THIRTYMINUTES.code -> {
                radioGroup.check(R.id.Radio30minutes)
            }
            Constants.UpdateTime.HOUR.code -> {
                radioGroup.check(R.id.Radio1hour)
            }
            Constants.UpdateTime.THREEHOURS.code -> {
                radioGroup.check(R.id.Radio3hours)
            }
            Constants.UpdateTime.SIXHOURS.code -> {
                radioGroup.check(R.id.Radio6hours)
            }
            Constants.UpdateTime.TWENTYHOUES.code -> {
                radioGroup.check(R.id.Radio12hours)
            }
            Constants.UpdateTime.DAY.code -> {
                radioGroup.check(R.id.Radio24hours)
            }
        }
    }

    fun setUpdateTime(updateTimeCode : String): Long {
        when (updateTimeCode) {
            Constants.UpdateTime.FIFTEENMINUTES.code -> {
                return TimeUnit.MINUTES.toMillis(15)
            }
            Constants.UpdateTime.THIRTYMINUTES.code -> {
                return TimeUnit.MINUTES.toMillis(30)
            }
            Constants.UpdateTime.HOUR.code -> {
                return TimeUnit.HOURS.toMillis(1)
            }
            Constants.UpdateTime.THREEHOURS.code -> {
                return TimeUnit.HOURS.toMillis(3)
            }
            Constants.UpdateTime.SIXHOURS.code -> {
                return TimeUnit.HOURS.toMillis(6)
            }
            Constants.UpdateTime.TWENTYHOUES.code -> {
                return TimeUnit.HOURS.toMillis(12)
            }
            Constants.UpdateTime.DAY.code -> {
                return TimeUnit.HOURS.toMillis(24)
            }
        }
        // 設定ミスの場合は1時間で設定
        return TimeUnit.HOURS.toMillis(1)
    }
}