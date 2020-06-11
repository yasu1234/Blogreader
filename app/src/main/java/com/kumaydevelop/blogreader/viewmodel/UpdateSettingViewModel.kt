package com.kumaydevelop.blogreader.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.R
import kotlinx.android.synthetic.main.activity_setting_count.*

class UpdateSettingViewModel: ViewModel() {

    var radioChecked = MutableLiveData<Int>()

    init{
        radioChecked.setValue(R.id.Radio15minutes)
    }

    // 初期表示時に登録している件数をチェックしている状態にする
    fun setRadioChecked(updateTimeCode : String) {
        when (updateTimeCode) {
            Constants.UpdateTime.FIFTEENMINUTES.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio15minutes)
            }
            Constants.UpdateTime.THIRTYMINUTES.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio30minutes)
            }
            Constants.UpdateTime.HOUR.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio1hour)
            }
            Constants.UpdateTime.THREEHOURS.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio3hours)
            }
            Constants.UpdateTime.SIXHOURS.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio6hours)
            }
            Constants.UpdateTime.TWENTYHOUES.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio12hours)
            }
            Constants.UpdateTime.DAY.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio24hours)
            }
        }
    }

}