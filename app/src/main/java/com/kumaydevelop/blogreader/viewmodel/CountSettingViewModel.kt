package com.kumaydevelop.blogreader.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kumaydevelop.blogreader.Constants
import com.kumaydevelop.blogreader.R
import kotlinx.android.synthetic.main.activity_setting_count.*

class CountSettingViewModel: ViewModel() {

    var radioChecked = MutableLiveData<Int>()

    init{
        radioChecked.postValue(R.id.Radio10Count)
    }

    fun setRadioChecked(displayCount : String) {
        when (displayCount) {
            Constants.DisplayCount.TEN.ordinal.toString() -> {
                radioChecked.postValue(R.id.Radio10Count)
            }
            Constants.DisplayCount.TWENTY.ordinal.toString() -> {
                radioChecked.postValue(R.id.Radio20Count)
            }
            Constants.DisplayCount.THIRTY.ordinal.toString() -> {
                radioChecked.postValue(R.id.Radio30Count)
            }
            Constants.DisplayCount.FORTY.ordinal.toString() -> {
                radioChecked.postValue(R.id.Radio40Count)
            }
            Constants.DisplayCount.FIFTY.ordinal.toString() -> {
                radioChecked.postValue(R.id.Radio50Count)
            }
        }
    }

}