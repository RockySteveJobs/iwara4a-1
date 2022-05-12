package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.util.PreviewAll

@PreviewAll
@Composable
fun MyComp(){
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text("Preview")
                }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .padding(it)
        ) {
            Text("测试")
            Icon(Icons.Outlined.Wallet, null)
            Text("测试预览速度")
            Text("哈哈")
            Card {
                Text("只是一个测试", modifier = Modifier.padding(8.dp))
            }
            Text("快速构建")
            AssistChip(
                onClick = { /*TODO*/ },
                enabled = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Watch, null)
                },
                label = {
                    Text("Label")
                }
            )
            Button(onClick = { /*TODO*/ }) {
                Text("测试")
            }
        }
    }
}

@Composable
fun TestScreen() {

}
