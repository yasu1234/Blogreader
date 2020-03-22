package com.kumaydevelop.blogreader

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.kumaydevelop.blogreader.Activity.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(MainActivity::class.java)

    /**
     * ナビゲーションが開いていること
     */
    @Test
    fun navigationMenu_open() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Thread.sleep(1500)
        onView(withId(R.id.nav_view)).check(matches(not(doesNotExist())))

    }

    /**
     * サイト追加のメニューが存在していること
     */
    @Test
    fun navigationMenu_addSite_exist() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Thread.sleep(1500)
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_camera))
        onView(ViewMatchers.withId(R.id.confirmButton)).check(matches(not(doesNotExist())))
    }

    /**
     * サイト追加のメニューが存在していること
     */
    @Test
    fun navigationMenu_displaycount_exist() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Thread.sleep(1500)
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_gallery))
        Thread.sleep(1500)
        onView(ViewMatchers.withId(R.id.displaycountLabel)).check(matches(not(doesNotExist())))
    }

    /**
     * 更新確認タイミング設定のメニューが存在していること
     */
    @Test
    fun navigationMenu_updatetime_exist() {
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Thread.sleep(1500)
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_slideshow))
        Thread.sleep(1500)
        onView(ViewMatchers.withId(R.id.updateTimeLabel)).check(matches(not(doesNotExist())))
    }
}