package com.rerere.iwara4a.ui.screen.self

import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.model.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelfViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel() {
}