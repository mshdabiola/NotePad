package com.mshdabiola.drawing

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TabUnselected
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.searchscreen.FlowLayout2
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingBar(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(initialPage = 1)
    var isUp by remember {
        mutableStateOf(true)
    }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier) {

        IconButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { isUp = !isUp }) {
            Icon(
                imageVector = if (!isUp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = ""
            )
        }
        HorizontalPager(
            pageCount = 5,
            state = pagerState,
            modifier = Modifier.animateContentSize()
        ) { index ->
            if (isUp) {
                when (index) {
                    0 -> {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "Select none")
                        }


                    }

                    1 -> {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "Clear canvas")
                        }
                    }

                    2 -> {
                        Column {
                            FlowLayout2(verticalSpacing = 8.dp) {
                                NoteIcon.noteColors.forEach {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(it)
                                            .size(16.dp)

                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                repeat(10) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .size((it * 4).dp)

                                    )

                                }
                            }

                        }
                    }

                    3 -> {
                        Text(text = "mark")
                    }

                    else -> {
                        Text(text = "Light")
                    }
                }
            }

        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                Box(
                    Modifier
                        .tabIndicatorOffset(it[pagerState.currentPage])
                        .width(2.dp)
                        .height(3.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            }
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.TabUnselected,
                    contentDescription = "edit"
                )
            }
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }) {
                Icon(painter = painterResource(id = NoteIcon.Erase), contentDescription = "edit")
            }
            Tab(
                selected = pagerState.currentPage == 2,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }) {
                Icon(imageVector = Icons.Default.Brush, contentDescription = "edit")
            }

            Tab(
                selected = pagerState.currentPage == 3,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } }) {
                Icon(imageVector = Icons.Default.Brush, contentDescription = "edit")
            }
            Tab(
                selected = pagerState.currentPage == 4,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(4) } }) {
                Icon(imageVector = Icons.Default.Brush, contentDescription = "edit")
            }
        }
    }


}

@Preview
@Composable
fun DrawingBarPreview() {
    DrawingBar()
}

