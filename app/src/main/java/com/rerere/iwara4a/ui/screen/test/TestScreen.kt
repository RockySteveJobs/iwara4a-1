package com.rerere.iwara4a.ui.screen.test

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.vico.compose.axis.axisGuidelineComponent
import com.patrykandpatryk.vico.compose.axis.axisLineComponent
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.component.marker.markerComponent
import com.patrykandpatryk.vico.compose.component.shape.textComponent
import com.patrykandpatryk.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatryk.vico.compose.style.LocalChartStyle
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.util.plus
import kotlin.math.pow
import kotlin.random.Random

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text("Test")
                }
            )
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalChartStyle provides m3ChartStyle()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding + PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    val entryModel = remember {
                        entryModelOf(5f, 15f, 10f, 20f, 10f, 35f, 5f, 14f, 12f)
                    }
                    Chart(
                        chart = lineChart(),
                        model = entryModel,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                        marker = markerComponent(
                            label = textComponent(),
                            indicator = axisLineComponent(),
                            guideline = axisGuidelineComponent()
                        )
                    )
                }

                item {
                    val entryModel = remember {
                        entryModelOf(5f, 15f, 10f, 20f, 10f, 35f, 5f, 14f, 12f)
                    }
                    Chart(
                        chart = columnChart(),
                        model = entryModel,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                        marker = markerComponent(
                            label = textComponent(),
                            indicator = axisLineComponent(),
                            guideline = axisGuidelineComponent()
                        )
                    )
                }
            }
        }
    }
}