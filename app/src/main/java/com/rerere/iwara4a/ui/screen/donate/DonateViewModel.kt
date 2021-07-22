package com.rerere.iwara4a.ui.screen.donate

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
) : ViewModel() {
    var donateList = listOf(
        "没有人" to 0.0
    )
}