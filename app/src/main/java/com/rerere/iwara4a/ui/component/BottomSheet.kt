package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.rerere.iwara4a.ui.component.basic.Centered

/**
 * 一个BottomSheet包装
 */
@Composable
fun BottomSheet(
    state: ModalBottomSheetState,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            // 顶部的手势指示器
            Centered(Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(30f))
                        .width(70.dp)
                        .aspectRatio(10 / 1f)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            // Sheet内容
            sheetContent()
            // 底部导航栏space, 防止底部被遮挡
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        },
        sheetShape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        content = content
    )
}