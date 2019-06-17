package com.kumaydevelop.rssreader

class Constants {
    enum class DisplayCount(val code: String, val count: Int) {
        TEN("0",10),
        TWENTY("1",20),
        THIRTY("2",30),
        FORTY("3",40),
        FIFTY("4",50),
    }

    enum class UpdateTime(val code: String, val time: Int) {
        FIFTEENMINUTES("0",15),
        THIRTYMINUTES("1",30),
        HOUR("2",1),
        THREEHOURS("3",3),
        SIXHOURS("4",6),
        TWENTYHOUES("5",12),
        DAY("6",24),
    }

    companion object {
        const val NOT_USE_GET_ARTICLES = 0
    }

}