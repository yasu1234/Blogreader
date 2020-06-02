package com.kumaydevelop.blogreader

import com.kumaydevelop.blogreader.General.Util
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class UtilTest {

    /**
     * 正しく分割されていること
     */
    @Test
    fun splitUrl_Normal001() {
        val util = Util
        val url = "https://hogehoge/fugafuga"
        val calendar = util.splitUrl(url)
        assertTrue(calendar.size == 2)
        assertEquals(calendar.get(0), "https://hogehoge/")
        assertEquals(calendar.get(1), "fugafuga")
    }

    /**
     * 正しく分割されていること(ドメイン以降が存在しない)
     */
    @Test
    fun splitUrl_Normal002() {
        val util = Util
        val url = "http://hogehoge/"
        val calendar = util.splitUrl(url)
        assertTrue(calendar.size == 2)
        assertEquals(calendar.get(0), "http://hogehoge/")
        assertEquals(calendar.get(1), "")
    }

    /**
     * 分割できず、例外が発生すること
     */
    @Test(expected = IndexOutOfBoundsException::class)
    fun splitUrl_Abnormal001() {
        val util = Util
        val url = "http:hogehoge"
        val calendar = util.splitUrl(url)
    }

    /**
     * 実行するRetrofitが取得できること
     */
    @Test
    fun createRetrofit_Normal001() {
        val util = Util
        val urlList = ArrayList<String>()
        urlList.add("http://hogehoge/")
        urlList.add("fugafuga")
        val retrofit = util.createRetrofit(urlList)
        assertNotNull(retrofit)
    }

    /**
     * 15分が取得できること
     */
    @Test
    fun setUpdateTime_Normal001() {
        val util = Util
        val code = "0"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.MINUTES.toMillis(15))
    }

    /**
     * 30分が取得できること
     */
    @Test
    fun SetUpdateTime_Normal002() {
        val util = Util
        val code = "1"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.MINUTES.toMillis(30))
    }

    /**
     * 1時間が取得できること
     */
    @Test
    fun setUpdateTime_Normal003() {
        val util = Util
        val code = "2"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(1))
    }

    /**
     * 3時間が取得できること
     */
    @Test
    fun setUpdateTime_Normal004() {
        val util = Util
        val code = "3"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(3))
    }

    /**
     * 6時間が取得できること
     */
    @Test
    fun setUpdateTime_Normal005() {
        val util = Util
        val code = "4"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(6))
    }

    /**
     * 12時間が取得できること
     */
    @Test
    fun setUpdateTime_Normal006() {
        val util = Util
        val code = "5"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(12))
    }

    /**
     * 24時間が取得できること
     */
    @Test
    fun setUpdateTime_Normal007() {
        val util = Util
        val code = "6"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(24))
    }

    /**
     * 存在しないコードが引数で渡った場合、1時間で設定される
     */
    @Test
    fun setUpdateTime_Abnormal001() {
        val util = Util
        val code = "10"
        val calendar = util.setUpdateTime(code)
        assertEquals(calendar, TimeUnit.HOURS.toMillis(1))
    }
}