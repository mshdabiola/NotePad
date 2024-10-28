/*
 *abiola 2022
 */

package com.mshdabiola.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val _settingState = MutableStateFlow<SettingState>(SettingState.Loading())
    val settingState = _settingState.asStateFlow()

    init {
        update()
    }

    fun setThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {
            _settingState.value = SettingState.Loading()

            userDataRepository.setThemeBrand(themeBrand)

            update()
        }
    }

    /**
     * Sets the desired dark theme config.
     */
    fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            _settingState.value = SettingState.Loading()

            userDataRepository.setDarkThemeConfig(darkThemeConfig)

            update()
        }
    }

    private fun update() {
        viewModelScope.launch {
            _settingState.value = userDataRepository.userData.map {
                SettingState.Success(
                    themeBrand = it.themeBrand,
                    darkThemeConfig = it.darkThemeConfig,
                )
            }.first()
        }
    }
}
