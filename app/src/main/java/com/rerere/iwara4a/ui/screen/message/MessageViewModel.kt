package com.rerere.iwara4a.ui.screen.message

import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.data.model.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()