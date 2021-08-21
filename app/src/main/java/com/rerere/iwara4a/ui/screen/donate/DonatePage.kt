package com.rerere.iwara4a.ui.screen.donate

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.util.openUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DonatePage(navController: NavController, donateViewModel: DonateViewModel = hiltViewModel()){
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            DefTopBar(navController, "捐助")
        }
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        context.openUrl("https://afdian.net/@re_ovo")
                    },
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp)
            ) {
               Column(Modifier.padding(16.dp)) {
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Text(text = "捐助", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                       Spacer(modifier = Modifier.width(10.dp))
                       Text(text = "点击打开爱发点捐助")
                   }
                   Spacer(modifier = Modifier
                       .padding(vertical = 2.dp)
                       .fillMaxWidth()
                       .height(0.5.dp)
                       .background(Color.Gray.copy(0.5f)))
                   CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                       Text(text = "有闲钱的考虑发个点吧，你的赞助是我开发的动力!")
                       Text(text = "捐助的会被放进下面的名单里~")
                   }
               }
            }
            Text(text = "捐助名单: ", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(donateViewModel.donateList) {
                    DonateCard(name = it.first, amount = it.second)
                }
            }
        }
    }
}

@Composable
private fun DonateCard(
    name: String,
    amount: Double
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
       Row(
           modifier = Modifier.padding(16.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
           Text(
               text = name,
               modifier = Modifier.weight(1f),
               style = LocalTextStyle.current.copy(
                   fontWeight = FontWeight.Bold
               )
           )
           Text(text = "$amount ¥")
       }
    }
}