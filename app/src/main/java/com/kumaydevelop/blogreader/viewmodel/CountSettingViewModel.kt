package com.kumaydevelop.blogreader.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.R
import kotlinx.android.synthetic.main.activity_setting_count.*

class CountSettingViewModel: ViewModel() {

    var radioChecked = MutableLiveData<Int>()

    init{
        radioChecked.setValue(R.id.Radio10Count)
    }

    // 初期表示時に登録している件数をチェックしている状態にする
    fun setRadioChecked(displayCount : String) {
        when (displayCount) {
            Constants.DisplayCount.TEN.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio10Count)
            }
            Constants.DisplayCount.TWENTY.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio20Count)
            }
            Constants.DisplayCount.THIRTY.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio30Count)
            }
            Constants.DisplayCount.FORTY.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio40Count)
            }
            Constants.DisplayCount.FIFTY.ordinal.toString() -> {
                radioChecked.setValue(R.id.Radio50Count)
            }
        }
    }

}