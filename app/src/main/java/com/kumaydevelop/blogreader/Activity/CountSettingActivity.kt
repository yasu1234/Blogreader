package com.kumaydevelop.blogreader.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import com.kumaydevelop.blogreader.Constants
import androidx.lifecycle.ViewModelProviders
import com.kumaydevelop.blogreader.Dialog.AlertDialog
import com.kumaydevelop.blogreader.Model.SettingModel
import com.kumaydevelop.blogreader.R
import com.kumaydevelop.blogreader.databinding.ActivitySettingCountBinding
import com.kumaydevelop.blogreader.viewmodel.CountSettingViewModel
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
        val binding: ActivitySettingCountBinding = DataBindingUtil.setContentView(this,R.layout.activity_setting_count)
        val countSettingViewModel = ViewModelProviders.of(this).get(CountSettingViewModel::class.java)
        binding.viewModel = countSettingViewModel
        val radioGroup: RadioGroup = radioGroup
        realm = Realm.getDefaultInstance()

        val displayCount = realm.where<SettingModel>().findFirst()!!

        // 表示時に登録データでチェックする
        countSettingViewModel.setRadioChecked(displayCount.displayCountCode)

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

            val dialog = AlertDialog()
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}