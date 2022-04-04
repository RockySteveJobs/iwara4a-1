package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.component.BottomSheet
import kotlinx.coroutines.launch

@Composable
fun TestScreen() {
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Expanded
    )
    val scope = rememberCoroutineScope()
    BottomSheet(
        state = state,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Text("Hello")
            }
        }
    ) {
        Column(
            Modifier.statusBarsPadding()
        ) {
            Text("测试")
            Button(onClick = {
                scope.launch {
                    if (state.isVisible) {
                        state.hide()
                    } else {
                        state.show()
                    }
                }
            }) {
                Text("展开")
            }
        }
    }
}