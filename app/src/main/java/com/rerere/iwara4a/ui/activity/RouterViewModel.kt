package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouterViewModel @Inject constructor(
    // private val sessionManager: SessionManager,
    // private val userRepo: UserRepo
) : ViewModel(){
    var screenOrientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)
    var pipMode by mutableStateOf(false)
}