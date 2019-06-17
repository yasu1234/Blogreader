package com.kumaydevelop.rssreader.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class SettingModel: RealmObject() {
    @PrimaryKey
    var id : Long = 1
    var displayCountCode: String = ""
    var updateTimeCode: String = ""
    var updateDate : Date = Date()
}

