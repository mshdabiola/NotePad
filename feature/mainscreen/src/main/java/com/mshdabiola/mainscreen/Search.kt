package com.mshdabiola.mainscreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search() {
//    DockedSearchBar(
//        query = "search",
//        onQueryChange = {},
//        onSearch = {},
//        active = true,
//        onActiveChange = {}
//    ) {
//
//    }
//    Column(Modifier.fillMaxSize()) {
//        val tooltipState= remember {
//            TooltipState()
//        }
//        LaunchedEffect(key1 = Unit, block = {
//            tooltipState.show()
//        })
//
//        PlainTooltipBox(
//            tooltip = { Text(text = "Tool")},
//            tooltipState=tooltipState
//        ) {
//            Text(text = "Text")
//        }
//    }
//    SwipeToDismiss(state = remember {
//        DismissState(DismissValue.Default)
//    }, background = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")},
//    dismissContent = { Text(text = "Abiola")}
//        )
//    val datePickerState= rememberDatePickerState()
//    datePickerState.
//    DatePicker(datePickerState =datePickerState)

}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    Search()
}