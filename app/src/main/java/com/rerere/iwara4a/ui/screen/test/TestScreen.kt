package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
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
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.util.plus
import kotlinx.coroutines.launch

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
        val scope = rememberCoroutineScope()
        val sheetState = rememberBottomSheetState()
        BottomSheet(
            state = sheetState
        ) {
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
        CompositionLocalProvider(LocalChartStyle provides m3ChartStyle()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding + PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                   Button(onClick = {
                       scope.launch {
                           sheetState.expand()
                       }
                   }) {
                       Text("Open Sheet")
                   }
                }

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