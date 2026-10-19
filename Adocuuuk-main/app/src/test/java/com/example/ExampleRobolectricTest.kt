package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.EmochiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Velora Ado AI", appName)
  }

  @Test
  fun `test story time jump parser`() {
    val jumpResult = com.example.data.repository.TimePerceptionEngine.parseStoryTimeJump("3 gün sonra sabah oldu")
    assertEquals(3, jumpResult.daysToAdvance)
    assertEquals("sabah", jumpResult.newSceneTime)
  }

  @Test
  fun `test in-memory database creation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    db.openHelper.writableDatabase
    assertTrue(db.isOpen)
    db.close()
  }
}
