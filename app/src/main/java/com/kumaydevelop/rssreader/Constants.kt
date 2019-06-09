package com.kumaydevelop.rssreader

class Constants {
    enum class DisplayCount(val code: String, val count: Int) {
        TEN("0",10),
        TWENTY("1",20),
        THIRTY("2",30),
        FORTY("3",40),
        FIFTY("4",50),
    }

    companion object {
        const val NOT_USE_GET_ARTICLES = 0
    }

}