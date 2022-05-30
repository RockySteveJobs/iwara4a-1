package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatryk.vico.compose.style.LocalChartStyle
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.rerere.iwara4a.ui.component.Md3TopBar
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
                contentPadding = innerPadding
            ) {
                item {
                    val entryModel = entryModelOf(5f, 15f, 10f, 20f, 10f, 100000f)
                    Chart(
                        chart = columnChart(),
                        model = entryModel,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                }

                item {
                    fun getRandomEntries() = List(size = 500) {
                        25f * Random.nextFloat()
                    }.mapIndexed { x, y ->
                        FloatEntry(
                            x = x.toFloat(),
                            y = y,
                        )
                    }

                    val producer = ChartEntryModelProducer(getRandomEntries())
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = producer,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis()
                    )
                }
            }
        }
    }
}