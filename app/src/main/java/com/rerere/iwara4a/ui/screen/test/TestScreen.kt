package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Chip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun MyComp(){
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text("测试")
        Icon(Icons.Outlined.Wallet, null)
        Text("测试预览速度")
        Text("哈哈")
        Card {
            Text("只是一个测试", modifier = Modifier.padding(8.dp))
        }
        Text("快速构建")
        Chip(
            onClick = { /*TODO*/ },
            enabled = true,
            leadingIcon = {
                Icon(Icons.Outlined.Watch, null)
            },
            content = {
                Text("Chip测试")
            }
        )
    }
}

@Composable
fun TestScreen() {

}
