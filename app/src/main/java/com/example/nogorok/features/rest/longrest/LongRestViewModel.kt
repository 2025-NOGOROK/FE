package com.example.nogorok.features.rest.longrest

import androidx.lifecycle.ViewModel

class LongRestViewModel : ViewModel() {

    fun getScenario(index: Int): List<RestItem> {
        return when (index) {
            1 -> listOf(
                RestItem("🖋", "교양독립 발표 준비", "09:00~10:00"),
                RestItem("🏋️", "헬스장", "09:00~10:00"),
                RestItem("🍓", "메가커피 알바", "13:00~17:00"),
                RestItem("👥", "서영이랑 저녁약속", "18:00~20:00"),
                RestItem("📖", "독서하기", "20:00~20:30")
            )

            2 -> listOf(
                RestItem("🧘", "명상하기", "07:00~07:10"),
                RestItem("🖋", "교양독립 발표 준비", "09:00~10:00"),
                RestItem("🏋️", "헬스장", "09:00~10:00"),
                RestItem("🍓", "메가커피 알바", "13:00~17:00"),
                RestItem("👥", "서영이랑 저녁약속", "18:00~20:00"),
                RestItem("📖", "독서하기", "20:00~20:30")
            )

            else -> listOf(
                RestItem("🖋", "교양독립 발표 준비", "09:00~10:00"),
                RestItem("🧘", "명상하기", "07:00~07:10"),
                RestItem("🏋️", "헬스장", "09:00~10:00"),
                RestItem("🍓", "메가커피 알바", "13:00~17:00"),
                RestItem("👥", "서영이랑 저녁약속", "18:00~20:00"),
                RestItem("📖", "독서하기", "20:00~20:30")
            )
        }
    }
}
