package com.rerere.iwara4a.ui.screen.donate

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
) : ViewModel() {
    var donateList = listOf(
        "黑暗剑" to 30.0,
        "assqer" to 10.0,
        "东东" to 10.0,
        "null0421" to 10.0,
        "DUYA666" to 10.0,
        "空白" to 10.0,
        "爱发电用户_RSub" to 10.0,
        "fiveto" to 10.0,
        "繁花丶" to 10.0,
        "枕头也是饺子" to 30.0,
        "御坂桜" to 20.0,
        "toki" to 360.0,
        "阿柒散悦" to 10.0,
        "出门先穿鞋" to 30.0,
        "一瓶蓝莓汁" to 10.0,
        "Refire" to 10.0,
        "sumika" to 10.0
    )
}