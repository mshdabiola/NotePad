package com.mshdabiola.database

import android.content.Context
import androidx.compose.ui.graphics.vector.path
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.model.NoteType // Assuming NoteType is in this package
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NoteDrawingDaoTest {

}
