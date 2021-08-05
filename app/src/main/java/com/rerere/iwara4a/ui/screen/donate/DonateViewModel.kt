package com.rerere.iwara4a.ui.screen.donate

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
) : ViewModel() {
    var donateList = listOf<Pair<String, Double>>(
        "黑暗剑" to 30.0,
        "assqer" to 10.0,
        "东东" to 10.0,
        "null0421" to 10.0,
        "DUYA666" to 10.0,
        "空白" to 10.0
    )
}