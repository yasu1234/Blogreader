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
import com.kumaydevelop.rssreader.Dialog.AlertDialog
import com.kumaydevelop.rssreader.General.Util
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.R
import com.kumaydevelop.rssreader.Service.PollingJob
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_setting_count.*
import java.util.*

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

            // 更新確認頻度を変えた状態でJobを更新
            val fetchJob = JobInfo.Builder(
                    1,
                    ComponentName(this, PollingJob::class.java))
                    .setPeriodic(Util.setUpdateTime(data.updateTimeCode))
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build()

            getSystemService(JobScheduler::class.java).schedule(fetchJob)

            var dialog = AlertDialog()
            dialog.title = "更新しました"
            dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                finish()
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
            Constants.UpdateTime.FIFTEENMINUTES.ordinal.toString() -> {
                radioGroup.check(R.id.Radio15minutes)
            }
            Constants.UpdateTime.THIRTYMINUTES.ordinal.toString() -> {
                radioGroup.check(R.id.Radio30minutes)
            }
            Constants.UpdateTime.HOUR.ordinal.toString() -> {
                radioGroup.check(R.id.Radio1hour)
            }
            Constants.UpdateTime.THREEHOURS.ordinal.toString() -> {
                radioGroup.check(R.id.Radio3hours)
            }
            Constants.UpdateTime.SIXHOURS.ordinal.toString() -> {
                radioGroup.check(R.id.Radio6hours)
            }
            Constants.UpdateTime.TWENTYHOUES.ordinal.toString() -> {
                radioGroup.check(R.id.Radio12hours)
            }
            Constants.UpdateTime.DAY.ordinal.toString() -> {
                radioGroup.check(R.id.Radio24hours)
            }
        }
    }
}