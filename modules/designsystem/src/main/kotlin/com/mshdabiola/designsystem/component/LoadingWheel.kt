/*
 *abiola 2022
 */

package com.mshdabiola.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NoteLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    LoadingIndicator(modifier = modifier)
//    val infiniteTransition = rememberInfiniteTransition(label = "wheel transition")
//
//    // Specifies the float animation for slowly drawing out the lines on entering
//    val startValue = if (LocalInspectionMode.current) 0F else 1F
//    val floatAnimValues = (0 until NUM_OF_LINES).map { remember { Animatable(startValue) } }
//    LaunchedEffect(floatAnimValues) {
//        (0 until NUM_OF_LINES).map { index ->
//            launch {
//                floatAnimValues[index].animateTo(
//                    targetValue = 0F,
//                    animationSpec = tween(
//                        durationMillis = 100,
//                        easing = FastOutSlowInEasing,
//                        delayMillis = 40 * index,
//                    ),
//                )
//            }
//        }
//    }
//
//    // Specifies the rotation animation of the entire Canvas composable
//    val rotationAnim by infiniteTransition.animateFloat(
//        initialValue = 0F,
//        targetValue = 360F,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = ROTATION_TIME, easing = LinearEasing),
//        ),
//        label = "wheel rotation animation",
//    )
//
//    // Specifies the color animation for the base-to-progress line color change
//    val baseLineColor = MaterialTheme.colorScheme.onBackground
//    val progressLineColor = MaterialTheme.colorScheme.inversePrimary
//
//    val colorAnimValues = (0 until NUM_OF_LINES).map { index ->
//        infiniteTransition.animateColor(
//            initialValue = baseLineColor,
//            targetValue = baseLineColor,
//            animationSpec = infiniteRepeatable(
//                animation = keyframes {
//                    durationMillis = ROTATION_TIME / 2
//                    progressLineColor at ROTATION_TIME / NUM_OF_LINES / 2 using LinearEasing
//                    baseLineColor at ROTATION_TIME / NUM_OF_LINES using LinearEasing
//                },
//                repeatMode = RepeatMode.Restart,
//                initialStartOffset = StartOffset(ROTATION_TIME / NUM_OF_LINES / 2 * index),
//            ),
//            label = "wheel color animation",
//        )
//    }
//
//    // Draws out the LoadingWheel Canvas composable and sets the animations
//    Canvas(
//        modifier = modifier
//            .size(48.dp)
//            .padding(8.dp)
//            .graphicsLayer { rotationZ = rotationAnim }
//            .semantics { contentDescription = contentDesc }
//            .testTag("loadingWheel"),
//    ) {
//        repeat(NUM_OF_LINES) { index ->
//            rotate(degrees = index * 30f) {
//                drawLine(
//                    color = colorAnimValues[index].value,
//                    // Animates the initially drawn 1 pixel alpha from 0 to 1
//                    alpha = if (floatAnimValues[index].value < 1f) 1f else 0f,
//                    strokeWidth = 4F,
//                    cap = StrokeCap.Round,
//                    start = Offset(size.width / 2, size.height / 4),
//                    end = Offset(size.width / 2, floatAnimValues[index].value * size.height / 4),
//                )
//            }
//        }
//    }
// }
//
// @Composable
// fun NoteOverlayLoadingWheel(
//    contentDesc: String,
//    modifier: Modifier = Modifier,
// ) {
//    Surface(
//        shape = RoundedCornerShape(60.dp),
//        shadowElevation = 8.dp,
//        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.83f),
//        modifier = modifier
//            .size(60.dp),
//    ) {
//        NoteLoadingWheel(
//            contentDesc = contentDesc,
//        )
//    }
}

private const val ROTATION_TIME = 12000
private const val NUM_OF_LINES = 12
