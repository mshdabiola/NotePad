package com.mshdabiola.drawing

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mshdabiola.searchscreen.FlowLayout2
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingBar(
    modifier: Modifier = Modifier,
    controller: DrawingController = rememberDrawingController(),

    ) {
    var isUp by remember {
        mutableStateOf(false)
    }
    var penColor by remember {
        mutableStateOf(controller.color)
    }
    var markColor by remember {
        mutableStateOf(0)
    }
    var crayonColor by remember {
        mutableStateOf(0)
    }

    var penWidth by remember {
        mutableStateOf((controller.lineWidth / 4) - 1)
    }
    var markWidth by remember {
        mutableStateOf(4)
    }
    var crayonWidth by remember {
        mutableStateOf(4)
    }

    LaunchedEffect(key1 = controller.unCompletePathData.value, block = {
        if (isUp) {
            isUp = false
        }
    })

    val pagerState = rememberPagerState(initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    Surface(modifier) {
        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        controller.draw_mode = DRAW_MODE.ERASE
                        isUp = if (pagerState.currentPage == 0) {
                            !isUp
                        } else {
                            false
                        }

                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                ) {
                    Box(Modifier.padding(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.eraser),
                            contentDescription = "eraser",
                            tint = if (pagerState.currentPage == 0) Color.DarkGray else Color.Gray,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.eraser_tiny),
                            contentDescription = "pen",
                            tint = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary else Color.Gray,
                        )
                    }
                }
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        controller.draw_mode = DRAW_MODE.PEN
                        controller.colorAlpha = 1f
                        controller.lineCap = 0
                        controller.lineWidth = (penWidth + 1) * 4
                        controller.color = penColor
                        isUp = if (pagerState.currentPage == 1) {
                            !isUp
                        } else {
                            true
                        }
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                ) {
                    Box(Modifier.padding(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.pen),
                            contentDescription = "pen",
                            tint = if (pagerState.currentPage == 1) Color.DarkGray else Color.Gray,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.pen_cap),
                            contentDescription = "pen",
                            tint = if (pagerState.currentPage == 1) controller.getColor(penColor) else Color.Gray,
                        )
                    }
                }

                Tab(
                    selected = pagerState.currentPage == 2,
                    unselectedContentColor = Color.Gray,
                    onClick = {
                        controller.draw_mode = DRAW_MODE.MARKER
                        controller.colorAlpha = 1f
                        controller.lineCap = 0
                        controller.lineWidth = (markWidth + 1) * 8
                        controller.color = markColor
                        isUp = if (pagerState.currentPage == 2) {
                            !isUp
                        } else {
                            true
                        }
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    },
                ) {
                    Box(Modifier.padding(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.markerr),
                            contentDescription = "marker",
                            tint = if (pagerState.currentPage == 2) Color.DarkGray else Color.Gray,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.marker_cap),
                            contentDescription = "marker",
                            tint = if (pagerState.currentPage == 2) controller.getColor(markColor) else Color.Gray,
                        )
                    }
                }
                Tab(
                    selected = pagerState.currentPage == 3,
                    unselectedContentColor = Color.Gray,
                    onClick = {
                        controller.draw_mode = DRAW_MODE.CRAYON
                        controller.colorAlpha = 0.5f
                        controller.lineCap = 1
                        controller.lineWidth = (crayonWidth + 1) * 8
                        controller.color = crayonColor
                        isUp = if (pagerState.currentPage == 3) {
                            !isUp
                        } else {
                            true
                        }
                        coroutineScope.launch { pagerState.animateScrollToPage(3) }
                    },
                ) {
                    Box(Modifier.padding(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.crayon),
                            contentDescription = "crayon",
                            tint = if (pagerState.currentPage == 3) Color.DarkGray else Color.Gray,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.crayon_cap),
                            contentDescription = "crayon",
                            tint = if (pagerState.currentPage == 3) controller.getColor(crayonColor) else Color.Gray,
                        )
                    }
                }
            }
//        IconButton(
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            onClick = { isUp = !isUp }) {
//            Icon(
//                imageVector = if (!isUp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                contentDescription = ""
//            )
//        }

            HorizontalPager(
                pageCount = 4,
                state = pagerState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .animateContentSize(),
            ) { index ->
                if (isUp) {
                    when (index) {
//

                        0 -> {
                            TextButton(onClick = { controller.clearPath() }) {
                                Text(text = "Clear canvas")
                            }
                        }

                        1 -> {
                            ColorAndWidth(
                                colors = controller.colors,
                                currentColor = penColor,
                                currentWidth = penWidth,
                                onColorClick = {
                                    penColor = it
                                    controller.color = it
                                },
                                onlineClick = {
                                    penWidth = it
                                    controller.lineWidth = (it + 1) * 4
                                },
                            )
                        }

                        2 -> {
                            ColorAndWidth(
                                colors = controller.colors,
                                currentColor = markColor,
                                currentWidth = markWidth,
                                onColorClick = {
                                    markColor = it
                                    controller.color = it
                                },
                                onlineClick = {
                                    markWidth = it
                                    controller.lineWidth = (it + 1) * 8
                                },
                            )
                        }

                        else -> {
                            ColorAndWidth(
                                colors = controller.colors,
                                currentColor = crayonColor,
                                currentWidth = crayonWidth,
                                onColorClick = {
                                    crayonColor = it
                                    controller.color = it
                                },
                                onlineClick = {
                                    crayonWidth = it
                                    controller.lineWidth = (it + 1) * 8
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorAndWidth(
    colors: Array<Color>,
    currentColor: Int,
    currentWidth: Int,
    onColorClick: (Int) -> Unit = {},
    onlineClick: (Int) -> Unit = {},
) {
    Column {
        FlowLayout2(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            verticalSpacing = 8.dp,
        ) {
            colors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .clickable {
                            onColorClick(index)
                        }
                        .clip(CircleShape)
                        .background(color)
                        .size(if (index == currentColor) 34.dp else 30.dp),

                    )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            repeat(10) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onlineClick(it)
                        }
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            if (it == currentWidth) Color.Gray else Color.Transparent,
                            CircleShape,
                        )
                        .size(30.dp),

                    ) {
                    Box(
                        modifier =
                        Modifier
                            .clip(CircleShape)
                            .background(Color.Black)
                            .align(Alignment.Center)
                            .padding(2.dp)
                            .size(((it + 1) * 2).dp),

                        )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawingBarPreview() {
    DrawingBar()
}
