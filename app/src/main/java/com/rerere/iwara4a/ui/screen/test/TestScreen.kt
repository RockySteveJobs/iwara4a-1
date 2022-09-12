package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
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
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.md.TimerPickerDialog
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.ui.util.rememberMutableState
import com.rerere.iwara4a.util.findActivity
import kotlinx.coroutines.launch

@Composable
fun TestScreen() {
    TwoPane(
        first = {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("测试A")
            }
        },
        second = {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("OK")
            }
        },
        strategy = HorizontalTwoPaneStrategy(
            splitFraction = 0.5f
        ),
        displayFeatures = calculateDisplayFeatures(LocalContext.current.findActivity())
    )
}

@Composable
fun TestScreenOld() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /*TODO*/ },
                    icon = {
                        Icon(Icons.Outlined.Wallet, null)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /*TODO*/ },
                    icon = {
                        Icon(Icons.Outlined.Wallet, null)
                    }
                )
            }
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()
        val sheetState = rememberBottomSheetState()
        BottomSheet(
            state = sheetState,
            skipPeek = true
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
                    var showPicker by rememberMutableState(false)
                    if(showPicker) {
                        TimerPickerDialog(
                            onDismissRequest = { showPicker = false }
                        ) {
                            println("finish")
                        }
                    }
                    Button(onClick = { showPicker = true }) {
                        Text(text = "Time Picker")
                    }
                }

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

                item {
                    var showDialog by rememberMutableState(false)
                    var progress by rememberMutableState(0f)
                    if(showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text("测试")
                            },
                            text = {
                                Slider(
                                    value = progress,
                                    onValueChange = { progress = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = { /*TODO*/ }) {
                                    Text("Ok")
                                }
                            }
                        )
                    }
                    Button(onClick = { showDialog = true }) {
                        Text("Open")
                    }
                }
            }
        }
    }
}