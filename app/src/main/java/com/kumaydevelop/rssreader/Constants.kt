package com.kumaydevelop.rssreader

class Constants {
    enum class DisplayCount(val count: Int) {
        TEN(10),
        TWENTY(20),
        THIRTY(30),
        FORTY(40),
        FIFTY(50),
    }

    enum class UpdateTime(val time: Int) {
        FIFTEENMINUTES(15),
        THIRTYMINUTES(30),
        HOUR(1),
        THREEHOURS(3),
        SIXHOURS(6),
        TWENTYHOUES(12),
        DAY(24),
    }

    companion object {
        const val RSS_CONFIRM = "RssURL確認"
        const val REGISTER = "登録"
        const val SLASH = "/"
        const val DOUBLE_SLASH = "//"
        const val FROM_YEAR_TO_SECONDS = "yyyy/MM/dd HH:mm:ss"
        const val TIMEZONE_ISO = "EEE, dd MMM yyyy HH:mm:ss Z"
        const val HTTP = "http://"
        const val HTTPS = "https://"
    }

}