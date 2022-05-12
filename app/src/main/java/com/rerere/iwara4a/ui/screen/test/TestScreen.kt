package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import kotlinx.coroutines.flow.MutableStateFlow

class TestScreenVM : ViewModel() {
    val stateFlow = MutableStateFlow(StateTest(0, 0))

    data class StateTest(
        val a: Int,
        val b: Int,
        var c: List<Int> = emptyList()
    )

    fun addA() {
        val newValue = stateFlow.value.a + 1
        stateFlow.value = stateFlow.value.copy(
            a = newValue
        )
    }

    fun addB() {
        val newValue = stateFlow.value.b + 1
        stateFlow.value = stateFlow.value.copy(
            b = newValue
        )
    }
}

@Composable
fun TestScreen(vm: TestScreenVM = viewModel()) {
    val state by vm.stateFlow.collectAsState()
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(title = "Test")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            TextButton(
                onClick = {
                    vm.addA()
                }
            ) {
                Text(text = "a: ${state.a}")
            }

            TextButton(
                onClick = {
                    vm.addB()
                }
            ) {
                Text(text = "b: ${state.b}")
            }
        }
    }
}