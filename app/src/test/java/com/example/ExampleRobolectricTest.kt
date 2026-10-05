package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.QuranData
import com.example.data.model.ReciterData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Easy Quranify", appName)
  }

  @Test
  fun `quran data contains 114 surahs`() {
    assertEquals(114, QuranData.surahs.size)
    assertEquals("Al-Fatihah", QuranData.surahs.first().nameEnglish)
    assertEquals("An-Nas", QuranData.surahs.last().nameEnglish)
  }

  @Test
  fun `reciters catalog is populated`() {
    assertTrue(ReciterData.reciters.size >= 12)
  }
}
