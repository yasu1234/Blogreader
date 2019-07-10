package com.kumaydevelop.rssreader

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kumaydevelop.rssreader.Activity.SiteAddActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SiteAddActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(SiteAddActivity::class.java)

    /**
     * 初期表示のときボタンのラベルが「RSSURL確認」であること
     */
    @Test
    fun initButtonLabel() {
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).check(ViewAssertions.matches(ViewMatchers.withText("RSSURL確認")))
    }

    /**
     * URLが入力されていないと「URLを入力してください」のメッセージが表示されること
     */
    @Test
    fun requireInput() {
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        onView(withText("URLを入力してください")).check(matches(isDisplayed()))
    }

    /**
     * URLの形式が違うと「URL形式に誤りがあります」のメッセージが表示されること(https)
     */
    @Test
    fun irregularUrl_https() {
        Espresso.onView(ViewMatchers.withId(R.id.urlText)).perform(replaceText("https://"))
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        onView(withText("URL形式に誤りがあります")).check(matches(isDisplayed()))
    }

    /**
     * URLの形式が違うと「URL形式に誤りがあります」のメッセージが表示されること(http)
     */
    @Test
    fun irregularUrl_http() {
        Espresso.onView(ViewMatchers.withId(R.id.urlText)).perform(replaceText("http://"))
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        onView(withText("URL形式に誤りがあります")).check(matches(isDisplayed()))
    }

    /**
     * URLを入力すると、RSS用のURLが表示されること
     */
    @Test
    fun displayRssUrl() {
        Espresso.onView(ViewMatchers.withId(R.id.urlText)).perform(replaceText("https://kumaskun.hatenablog.com/"))
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        Thread.sleep(5000)
        onView(withId(R.id.rssText)).check(matches(withText("https://kumaskun.hatenablog.com/rss")))
        onView(ViewMatchers.withId(R.id.confirmButton)).check(matches(withText("登録")))
    }

    /**
     * RSSに対応していない場合のテスト
     */
    @Test
    fun noRssUrl() {
        Espresso.onView(ViewMatchers.withId(R.id.urlText)).perform(replaceText("https://hogehoge.jp"))
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        Thread.sleep(5000)
        onView(withText("データを取得できませんでした。")).check(matches(isDisplayed()))
    }

    /**
     * URLを入力して、ブログを登録するところまでの確認
     */
    @Test
    fun registerRss() {
        Espresso.onView(ViewMatchers.withId(R.id.urlText)).perform(replaceText("https://kumaskun.hatenablog.com/"))
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.confirmButton)).perform(click())
        Thread.sleep(5000)
        onView(withText("くま's Tech系Blogを登録しますか?")).check(matches(isDisplayed()))
    }
}