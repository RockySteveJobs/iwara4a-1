package com.rerere.iwara4a.ui.screen.donate

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.openUrl

@OptIn(ExperimentalFoundationApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun DonatePage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    Surface(
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题栏
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "捐助", fontSize = 24.sp, modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(Icons.Default.Close, null)
                }
            }
            // 爱发电
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.openUrl("https://afdian.net/@re_ovo")
                    },
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "爱发电", fontWeight = FontWeight.Bold, fontSize = 23.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "点击打开爱发点赞助页面")
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            // Patreon
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // context.openUrl("https://afdian.net/@re_ovo")
                    },
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "Pateron", fontWeight = FontWeight.Bold, fontSize = 23.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "暂未开通该渠道")
                }
            }
        }
    }
}