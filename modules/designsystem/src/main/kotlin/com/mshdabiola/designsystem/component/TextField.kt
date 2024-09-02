/*
 *abiola 2024
 */

package com.mshdabiola.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SkTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardAction: () -> Unit = {},
    maxNum: TextFieldLineLimits = TextFieldLineLimits.Default,
) {
    MyTextField(
        modifier = modifier,
//                    .bringIntoViewRequester(focusRequester2)
//            .focusRequester(focusRequester)

        state = state,
        placeholder = {
            if (placeholder != null) {
                Text(text = placeholder)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),

        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions { keyboardAction() },

        lineLimits = maxNum,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),

    inputTransformation: InputTransformation? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    // codepointTransformation: CodepointTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),

) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(
            state = state,
            modifier = modifier
                .defaultErrorSemantics(isError, "Error occur")
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight,
                ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor(isError).value),
            keyboardOptions = keyboardOptions,
            // keyboardActions = keyboardActions,
            interactionSource = interactionSource,

            inputTransformation = inputTransformation,
            lineLimits = lineLimits,
            onTextLayout = onTextLayout,
            // codepointTransformation = codepointTransformation,
            decorator = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = state.text.toString(),
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = lineLimits == TextFieldLineLimits.SingleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                )
            },

            scrollState = scrollState,

        )
    }
}

// @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
// @Composable
// fun MyOutlinedTextField(
//    state: TextFieldState,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    textStyle: TextStyle = LocalTextStyle.current,
//    label: @Composable() (() -> Unit)? = null,
//    placeholder: @Composable() (() -> Unit)? = null,
//    leadingIcon: @Composable() (() -> Unit)? = null,
//    trailingIcon: @Composable() (() -> Unit)? = null,
//    prefix: @Composable() (() -> Unit)? = null,
//    suffix: @Composable() (() -> Unit)? = null,
//    supportingText: @Composable() (() -> Unit)? = null,
//    isError: Boolean = false,
//    visualTransformation: VisualTransformation = VisualTransformation.None,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    shape: Shape = OutlinedTextFieldDefaults.shape,
//    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
//    inputTransformation: InputTransformation? = null,
//    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
//    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
//    codepointTransformation: CodepointTransformation? = null,
//    scrollState: ScrollState = rememberScrollState(),
// ) {
//    // If color is not provided via the text style, use content color as a default
//    val textColor = textStyle.color.takeOrElse {
//        colors.textColor(enabled, isError, interactionSource).value
//    }
//    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
//
//    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
//        BasicTextField2(
//            state = state,
//            modifier = if (label != null) {
//                modifier
//                    // Merge semantics at the beginning of the modifier chain to ensure padding is
//                    // considered part of the text field.
//                    .semantics(mergeDescendants = true) {}
//                    .padding(top = OutlinedTextFieldTopPadding)
//            } else {
//                modifier
//            }
//                .defaultErrorSemantics(isError, "Error Occur")
//                .defaultMinSize(
//                    minWidth = OutlinedTextFieldDefaults.MinWidth,
//                    minHeight = OutlinedTextFieldDefaults.MinHeight,
//                ),
//            enabled = enabled,
//            readOnly = readOnly,
//            textStyle = mergedTextStyle,
//            cursorBrush = SolidColor(colors.cursorColor(isError).value),
//            keyboardOptions = keyboardOptions,
//            keyboardActions = keyboardActions,
//            interactionSource = interactionSource,
//
//            inputTransformation = inputTransformation,
//            lineLimits = lineLimits,
//            onTextLayout = onTextLayout,
//            codepointTransformation = codepointTransformation,
//            decorator = @Composable { innerTextField ->
//                // places leading icon, text field with label and placeholder, trailing icon
//                TextFieldDefaults.DecorationBox(
//                    value = state.text.toString(),
//                    visualTransformation = visualTransformation,
//                    innerTextField = innerTextField,
//                    placeholder = placeholder,
//                    label = label,
//                    leadingIcon = leadingIcon,
//                    trailingIcon = trailingIcon,
//                    prefix = prefix,
//                    suffix = suffix,
//                    supportingText = supportingText,
//                    shape = shape,
//                    singleLine = lineLimits == TextFieldLineLimits.SingleLine,
//                    enabled = enabled,
//                    isError = isError,
//                    interactionSource = interactionSource,
//                    colors = colors,
//                )
//            },
//
//
//            scrollState = scrollState,
//
//            )
//    }
// }

internal val TextFieldColors.selectionColors: TextSelectionColors
    @Composable get() = textSelectionColors

internal fun Modifier.defaultErrorSemantics(
    isError: Boolean,
    defaultErrorMessage: String,
): Modifier = if (isError) semantics { error(defaultErrorMessage) } else this

@Composable
internal fun TextFieldColors.cursorColor(isError: Boolean): State<Color> {
    return rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
}

@Composable
internal fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

internal val OutlinedTextFieldTopPadding = 8.dp
