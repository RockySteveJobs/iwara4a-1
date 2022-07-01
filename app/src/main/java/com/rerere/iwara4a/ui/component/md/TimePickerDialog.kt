package com.rerere.iwara4a.ui.component.md

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.component.basic.Centered
import java.util.*

/**
 * 日历Picker组件，基于AlertDialog
 *
 * @param onDismissRequest 关闭请求
 * @param onChooseDate 选中日期回调
 *
 * @author RE
 */
@Composable
fun TimerPickerDialog(
    onDismissRequest: () -> Unit,
    onChooseDate: (Day) -> Unit
) {
    var currentDay by remember {
        mutableStateOf(
            Day.of(calendarFactory())
        )
    }
    var currentShowingMonth by remember {
        mutableStateOf(currentDay.month)
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("选择日期")
        },
        icon = {
            Icon(Icons.Outlined.DateRange, null)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    // Toolbar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${currentShowingMonth.year}/${currentShowingMonth.month}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                currentShowingMonth = currentShowingMonth.prevMonth
                            }
                        ) {
                            Icon(Icons.Outlined.NavigateBefore, null)
                        }
                        IconButton(
                            onClick = {
                                currentShowingMonth = currentShowingMonth.nextMonth
                            }
                        ) {
                            Icon(Icons.Outlined.NavigateNext, null)
                        }
                    }
                }
                item {
                    // Week
                    Crossfade(currentShowingMonth) { currentShowingMonth ->
                        WeekBar(
                            currentDay = currentDay,
                            days = currentShowingMonth.days,
                            onClickDay = {
                                currentDay = it
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onChooseDate(currentDay)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun WeekBar(
    currentDay: Day,
    days: List<Day>,
    onClickDay: (Day) -> Unit
) {
    Card {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // Week Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(7) { week ->
                    Text(
                        text = when (week) {
                            0 -> "一"
                            1 -> "二"
                            2 -> "三"
                            3 -> "四"
                            4 -> "五"
                            5 -> "六"
                            6 -> "日"
                            else -> "?"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // Days
            repeat(days.maxOf { it.weekOfMonth }) { weekOfMonth ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeekDay.values().forEach { dayOfWeek ->
                        val day = days.firstOrNull {
                            it.weekOfMonth == weekOfMonth + 1 && it.dayOfWeek == dayOfWeek
                        }
                        val selected = day == currentDay
                        if (day != null) {
                            Centered(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .clickable {
                                        onClickDay(day)
                                    }
                                    .then(
                                        if (selected)
                                            Modifier
                                                .background(MaterialTheme.colorScheme.secondary)
                                        else
                                            Modifier
                                    )
                            ) {
                                Text(
                                    text = day.day.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Text(
                                text = "",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 日历工厂
 *
 * 在构建日历时设置 firstDayOfWeek 可以改变星期的顺序
 * 否则很有可能显示错误的星期
 */
private val calendarFactory: () -> Calendar = {
    GregorianCalendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
    }
}

/**
 * 代表一周中的一天
 */
enum class WeekDay {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    companion object {
        /**
         * 将Calendar的星期转换为WeekDay枚举
         */
        fun ofCalendar(calendar: Calendar): WeekDay {
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> Monday
                Calendar.TUESDAY -> Tuesday
                Calendar.WEDNESDAY -> Wednesday
                Calendar.THURSDAY -> Thursday
                Calendar.FRIDAY -> Friday
                Calendar.SATURDAY -> Saturday
                else -> Sunday
            }
        }
    }
}

/**
 * 代表某年的某个月
 *
 * @param year 年
 * @param month 月
 */
data class Month(
    val year: Int,
    val month: Int,
) {
    /**
     * 获取下一个月
     *
     * @return 下一个月
     */
    val nextMonth: Month
        get() = if (month == 12) {
            Month(year + 1, 1)
        } else {
            Month(year, month + 1)
        }

    /**
     * 获取上一个月
     *
     * @return 上一个月
     */
    val prevMonth: Month
        get() = if (month == 1) {
            Month(year - 1, 12)
        } else {
            Month(year, month - 1)
        }

    /**
     * 获取本月的所有天
     *
     * @return 本月的所有天
     */
    val days: List<Day> by lazy {
        val cal = calendarFactory().apply {
            set(year, month - 1, 1)
        }
        val daysCountOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        (1..daysCountOfMonth).map {
            // 更新到该天
            cal.set(Calendar.DAY_OF_MONTH, it)

            Day.of(cal)
        }
    }

    companion object {
        /**
         * 基于给定的Calendar获取本月
         *
         * @param calendar 给定的Calendar
         * @return 本月
         */
        fun of(calendar: Calendar) = Month(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
        )
    }
}

/**
 * @param month 年/月
 * @param dayOfWeek 星期几 (1..7)
 * @param day 几号
 */
data class Day(
    val month: Month,
    val dayOfWeek: WeekDay,
    val weekOfMonth: Int,
    val day: Int
) {
    companion object {
        /**
         * 基于给定的Calendar获取该天
         *
         * @param calendar 给定的Calendar
         * @return 该天
         */
        fun of(calendar: Calendar): Day {
            return Day(
                month = Month.of(calendar),
                dayOfWeek = WeekDay.ofCalendar(calendar),
                weekOfMonth = calendar[Calendar.WEEK_OF_MONTH],
                day = calendar[Calendar.DAY_OF_MONTH]
            )
        }
    }
}