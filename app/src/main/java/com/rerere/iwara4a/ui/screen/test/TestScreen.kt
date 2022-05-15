package com.rerere.iwara4a.ui.screen.test

import android.app.PictureInPictureParams
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.rerere.iwara4a.util.findActivity


@Composable
fun TestScreen() {
    val comp = remember {
        movableContentOf {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(16 / 9f)
                    .background(Color.Black)
            )
        }
    }
    var state by remember {
        mutableStateOf(true)
    }
    val activity = LocalContext.current
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Button(onClick = { state = !state }) {
            Text("Switch")
        }
        if (state) {
            Column(
                modifier = Modifier.clickable {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity.findActivity()
                            .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    }
                }
            ) {
                comp()
                Text("Column")
            }
        } else {
            Row(
                modifier = Modifier.clickable {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity.findActivity()
                            .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    }
                }
            ) {
                Text("Row")
                comp()
            }
        }
    }
}