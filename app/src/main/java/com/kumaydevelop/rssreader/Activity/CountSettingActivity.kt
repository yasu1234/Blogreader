package com.kumaydevelop.rssreader.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import com.kumaydevelop.rssreader.Constants
import com.kumaydevelop.rssreader.Model.SettingModel
import com.kumaydevelop.rssreader.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_setting_count.*
import java.util.*



class CountSettingActivity: AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    private lateinit var realm: Realm
    var displayCount: String = "4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_count)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        realm = Realm.getDefaultInstance()

        val displayCount = realm.where<SettingModel>().findFirst()!!

        // 表示時に登録データでチェックする
        setRadioChecked(displayCount.displayCountCode)

        saveButton.setOnClickListener {
            val selectedId =radioGroup.checkedRadioButtonId
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)
            val index = radioGroup.indexOfChild(radioButton)

            realm.executeTransaction {
                val data = realm.where<SettingModel>().equalTo("id", 1L).findFirst()
                if (data != null) {
                    data.displayCountCode = index.toString()
                    data.updateDate = Date()
                }
            }

            var dialog = com.kumaydevelop.rssreader.AlertDialog()
            dialog.title = "更新しました"
            dialog.onOkClickListener = DialogInterface.OnClickListener { dialog, which ->
                finish()
            }
            dialog.show(supportFragmentManager, null)
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
    }



    // 初期表示時に登録している件数をチェックしている状態にする
    fun setRadioChecked(displayCount : String) {
        when (displayCount) {
            Constants.DisplayCount.TEN.code -> {
                radioGroup.check(R.id.Radio10Count)
            }
            Constants.DisplayCount.TWENTY.code -> {
                radioGroup.check(R.id.Radio20Count)
            }
            Constants.DisplayCount.THIRTY.code -> {
                radioGroup.check(R.id.Radio30Count)
            }
            Constants.DisplayCount.FORTY.code -> {
                radioGroup.check(R.id.Radio40Count)
            }
            Constants.DisplayCount.FIFTY.code -> {
                radioGroup.check(R.id.Radio50Count)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}