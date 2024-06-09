package com.example.modul7.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.modul7.R
import com.example.modul7.utils.Constants.DUMMY_EMAIL
import com.example.modul7.utils.Constants.PASSWORD
import com.example.modul7.utils.Constants.REAL_EMAIL
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginIsSuccess() {
        onView(withId(R.id.ed_login_email)).perform(typeText(REAL_EMAIL))

        onView(withId(R.id.ed_login_password)).perform(scrollTo())
        onView(withId(R.id.ed_login_password)).perform(typeText(PASSWORD))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btn_login)).perform(scrollTo())
        onView(withId(R.id.btn_login)).perform(click())

        waitOrPause()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val stringResource = context.getString(R.string.main_activity_title)

        onView(withText(stringResource))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginIsFail() {
        onView(withId(R.id.ed_login_email)).perform(typeText(DUMMY_EMAIL))

        onView(withId(R.id.ed_login_password)).perform(scrollTo())
        onView(withId(R.id.ed_login_password)).perform(typeText(PASSWORD))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.btn_login)).perform(scrollTo())
        onView(withId(R.id.btn_login)).perform(click())

        waitOrPause()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val stringResource = context.getString(R.string.main_activity_title)

        onView(withText(stringResource))
            .check(doesNotExist())
    }

    private fun waitOrPause (duration: Long = 6000) {
        val idlingResource = CountingIdlingResource("WaitForLoading")
        IdlingRegistry.getInstance().register(idlingResource)

        idlingResource.increment()
        Thread.sleep(duration)
        idlingResource.decrement()

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}