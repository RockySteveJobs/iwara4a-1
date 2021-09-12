package com.rerere.iwara4a.ui.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.message.PrivateMessagePreviewList
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()