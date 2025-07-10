package com.mshdabiola.datastore

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.ThemeBrand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.junit.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {

    private lateinit var testScope: TestScope
    private lateinit var testDispatcher: TestDispatcher
    private val userDataJsonSerializer = UserDataJsonSerializer()

    @BeforeTest
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher + Job())
    }

    @AfterTest
    fun tearDown() {
        testScope.cancel() // Cancels all coroutines launched in this scope

        // TemporaryFolder rule handles file deletion
    }
    private fun getDataStore(name: String): UserPreferencesRepository {
        val testDataStore = DataStoreFactory.create(
            storage =
            OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = userDataJsonSerializer,
                producePath = {
                    val path = File(FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toFile(), "$name.json")
                    if (!path.parentFile.exists()) {
                        path.mkdirs()
                    }
                    path.toOkioPath()
                },
            ),
        )
        return UserPreferencesRepository(testDataStore)
    }

    @Test
    fun initialUserData_emitsDefaultValues() = testScope.runTest {
        val userData = getDataStore("init").userData.first()
        assertEquals(userDataJsonSerializer.defaultValue, userData)
    }

    @Test
    fun setThemeBrand_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setThemeBrand")
        val newThemeBrand = ThemeBrand.DEFAULT
        repository.setThemeBrand(newThemeBrand)
        val userData = repository.userData.first()
        assertEquals(newThemeBrand, userData.themeBrand)
        // Verify other defaults remain
        assertEquals(userDataJsonSerializer.defaultValue.darkThemeConfig, userData.darkThemeConfig)
    }

    @Test
    fun setThemeContrast_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setThemeContrast")
        val newContrast = Contrast.High
        repository.setThemeContrast(newContrast)
        val userData = repository.userData.first()
        assertEquals(newContrast, userData.contrast)
        assertEquals(userDataJsonSerializer.defaultValue.themeBrand, userData.themeBrand)
    }

    @Test
    fun setDynamicColorPreference_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setDynamicColorPreference")
        val useDynamicColor = true
        repository.setDynamicColorPreference(useDynamicColor)
        val userData = repository.userData.first()
        assertEquals(useDynamicColor, userData.useDynamicColor)
    }

    @Test
    fun setDarkThemeConfig_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setDarkThemeConfig")
        val newDarkThemeConfig = DarkThemeConfig.DARK
        repository.setDarkThemeConfig(newDarkThemeConfig)
        val userData = repository.userData.first()
        assertEquals(newDarkThemeConfig, userData.darkThemeConfig)
    }

    @Test
    fun setShouldHideOnboarding_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setShouldHideOnboarding")
        val shouldHide = true
        repository.setShouldHideOnboarding(shouldHide)
        val userData = repository.userData.first()
        assertEquals(shouldHide, userData.shouldHideOnboarding)
    }

    @Test
    fun toggleGrid_flipsIsGridValue() = testScope.runTest {
        val repository = getDataStore("toggleGrid")
        val initialIsGrid = repository.userData.first().isGrid

        repository.toggleGrid()
        val afterFirstToggle = repository.userData.first().isGrid
        assertEquals(!initialIsGrid, afterFirstToggle)

        repository.toggleGrid()
        val afterSecondToggle = repository.userData.first().isGrid
        assertEquals(initialIsGrid, afterSecondToggle) // Should be back to initial
    }

    @Test
    fun toggleGrid_whenInitiallyTrue_becomesFalse() = testScope.runTest {
        val repository = getDataStore("toggleGrid2")
        // Assuming default isGrid is true from UserPreferencesSerializer.defaultValue
        if (!userDataJsonSerializer.defaultValue.isGrid) {
            repository.toggleGrid() // Make it true if default is false for this specific test
        }
        assertTrue(repository.userData.first().isGrid, "Pre-condition: isGrid should be true")

        repository.toggleGrid()
        assertFalse(repository.userData.first().isGrid)
    }

    @Test
    fun toggleGrid_whenInitiallyFalse_becomesTrue() = testScope.runTest {
        val repository = getDataStore("toggleGrid3")
        // Make isGrid false initially
        if (userDataJsonSerializer.defaultValue.isGrid) {
            repository.toggleGrid() // Make it false if default is true
        }
        assertFalse(repository.userData.first().isGrid, "Pre-condition: isGrid should be false")

        repository.toggleGrid()
        assertTrue(repository.userData.first().isGrid)
    }

    @Test
    fun setNoteDisplayCategory_updatesDataStore() = testScope.runTest {
        val repository = getDataStore("setNoteDisplayCategory")
        val newCategory = NoteDisplayCategory()
        repository.setNoteDisplayCategory(newCategory)
        val userData = repository.userData.first()
        assertEquals(newCategory, userData.noteDisplayCategory)
        assertEquals(userDataJsonSerializer.defaultValue.themeBrand, userData.themeBrand) // Check another default
    }

    @Test
    fun multipleUpdates_areReflectedCorrectly() = testScope.runTest {
        val repository = getDataStore("multipleUpdates")
        repository.setThemeBrand(ThemeBrand.DEFAULT)
        repository.setDarkThemeConfig(DarkThemeConfig.LIGHT)
        repository.setShouldHideOnboarding(true)

        val userData = repository.userData.first()
        assertEquals(ThemeBrand.DEFAULT, userData.themeBrand)
        assertEquals(DarkThemeConfig.LIGHT, userData.darkThemeConfig)
        assertTrue(userData.shouldHideOnboarding)
        assertEquals(userDataJsonSerializer.defaultValue.contrast, userData.contrast) // Check a default not changed
    }
}
