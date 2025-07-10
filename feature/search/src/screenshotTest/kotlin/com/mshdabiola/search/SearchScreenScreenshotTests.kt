/*
 *abiola 2023
 */

package com.mshdabiola.search

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mshdabiola.model.getDefinedNotePads
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.PreviewMain

class SearchScreenScreenshotTests {
    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
    @PreviewMain
    @Composable
    fun Select() {
        PreviewContainer {
            SearchScreen(
                searchState = SearchState.Select(
                    types = listOf(
                        SearchSort.Type(0),
                        SearchSort.Type(1),
                        SearchSort.Type(2),
                        SearchSort.Type(3),
                        SearchSort.Type(4),
                        SearchSort.Type(5),
                    ),
                    label = listOf(
                        SearchSort.Label("Java", iconIndex = 6, 3),
                        SearchSort.Label("Python", iconIndex = 6, 3),
                        SearchSort.Label("C Sharp", iconIndex = 6, 3),
                        SearchSort.Label("JavaScript", iconIndex = 6, 3),
                        SearchSort.Label("Java", iconIndex = 6, 3),
                        SearchSort.Label("Java", iconIndex = 6, 3),

                    ),
                    color = listOf(
                        SearchSort.Color(-1),
                        SearchSort.Color(0),
                        SearchSort.Color(1),
                        SearchSort.Color(2),
                        SearchSort.Color(3),
                        SearchSort.Color(4),
                        SearchSort.Color(5),
                    ),
                ),
            )
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
    @PreviewMain
    @Composable
    fun Empty() {
        //  val list = getDefinedNotePads()
        PreviewContainer {
            SearchScreen(
                searchQuery = rememberTextFieldState("Java"),
                searchState = SearchState.Success(
                    searches = emptyList(),
                    isGrid = true,

                ),
            )
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
    @PreviewMain
    @Composable
    fun Main() {
        val list = getDefinedNotePads().take(4)
        PreviewContainer {
            SearchScreen(
                searchQuery = rememberTextFieldState("Java"),
                searchState = SearchState.Success(
                    searches = list,
                    isGrid = true,

                ),
            )
        }
    }
}
