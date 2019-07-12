package com.kumaydevelop.blogreader.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class BlogModel: RealmObject() {
    @PrimaryKey
    var id : Long = 0
    var title: String = ""
    var url: String = ""
    var lastUpdate : Date = Date()
}