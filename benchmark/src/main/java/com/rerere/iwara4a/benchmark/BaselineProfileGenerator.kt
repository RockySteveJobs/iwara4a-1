package com.rerere.iwara4a.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() =
        baselineProfileRule.collectBaselineProfile(
            packageName = "com.rerere.iwara4a"
        ) {
            pressHome()
            startActivityAndWait()
            device.run {
                waitForIdle()
                wait(Until.findObject(By.desc("Media Preview Card")), 10_000)
                findObjects(By.desc("Media Preview Card")).random().click()
                waitForIdle()
                wait(Until.findObject(By.desc("Video Player")), 10_000)
                waitForIdle()
                pressBack()
                waitForIdle()
            }
        }
}