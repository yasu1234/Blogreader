package com.kumaydevelop.blogreader

import com.kumaydevelop.blogreader.Entity.BlogEntity
import com.kumaydevelop.blogreader.General.Util
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Test

class RxjavaTest {

    /**
     * ブログのデータが取得できていること
     */
    @Test
    fun get_rssData() {
        val util = Util
        val urlList = ArrayList<String>()
        urlList.add("https://kumaskun.hatenablog.com/")
        urlList.add("rss")
        val retrofit = util.createRetrofit(urlList)
        val testObserver = TestObserver<BlogEntity>()
        retrofit.subscribe(testObserver)
        testObserver.awaitTerminalEvent()
        // 完了を確認
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        val blog = testObserver.values()[0]
        Assert.assertEquals(blog.title, "くま's Tech系Blog")
        Assert.assertNotNull(blog.articleEntities)
    }

    /**
     * URLが正しくなく、ブログのデータが取得できないこと
     */
    @Test
    fun not_get_rssData() {
        val util = Util
        val urlList = ArrayList<String>()
        urlList.add("https://hogehoge/")
        urlList.add("rss")
        val retrofit = util.createRetrofit(urlList)
        val testObserver = TestObserver<BlogEntity>()
        retrofit.subscribe(testObserver)
        testObserver.awaitTerminalEvent()

        Assert.assertEquals(testObserver.values().size, 0)
    }
}