package com.example.nogorok.features.rest.longrest

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R
import com.example.nogorok.features.schedule.Schedule
import com.example.nogorok.features.schedule.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// 시나리오 데이터 클래스 (각 시나리오에 이름과 일정 리스트를 담음)
data class Scenario(
    val name: String,
    val schedules: List<Schedule>
)

class LongRestActivity : AppCompatActivity() {

    // ScheduleViewModel 인스턴스 가져오기 (나의 일정 데이터 사용)
    private val scheduleViewModel: ScheduleViewModel by viewModels()

    private var scenarios: List<Scenario> = emptyList()
    private var currentScenario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_rest)

        // 일정 데이터가 바뀔 때마다 시나리오 3개를 자동 생성해서 보여줌
        scheduleViewModel.scheduleList.observe(this) { scheduleList ->
            scenarios = generateScenarios(scheduleList)
            currentScenario = 0
            showScenario()
        }

        // 시나리오 순환(무한 루프)
        findViewById<ImageButton>(R.id.btnPrev).setOnClickListener {
            if (scenarios.isNotEmpty()) {
                currentScenario = (currentScenario - 1 + scenarios.size) % scenarios.size
                showScenario()
            }
        }

        findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            if (scenarios.isNotEmpty()) {
                currentScenario = (currentScenario + 1) % scenarios.size
                showScenario()
            }
        }

        // 선택 버튼 (추후 선택된 시나리오 처리)
        findViewById<Button>(R.id.btnSelect).setOnClickListener {
            // TODO: 선택된 시나리오 활용 (예: 저장, 다음 화면 이동 등)
            finish()
        }
    }

    // 일정 리스트를 받아서 시나리오 3개를 자동 생성
    private fun generateScenarios(scheduleList: List<Schedule>): List<Scenario> {
        val scenario1 = Scenario("시나리오 1", scheduleList)
        val scenario2 = Scenario("시나리오 2", scheduleList.shuffled())
        val scenario3 = Scenario("시나리오 3", scheduleList.reversed())
        return listOf(scenario1, scenario2, scenario3)
    }

    // 일정 정보를 카드에 표시할 데이터로 변환 (이모지, 제목, 시간)
    private fun scheduleToCard(schedule: Schedule): Triple<String, String, String> {
        // 이모지는 제목 키워드로 임의 매핑 (원하면 커스터마이즈 가능)
        val emoji = when {
            schedule.title.contains("독일어") -> "📝"
            schedule.title.contains("헬스") -> "🏋️‍♂️"
            schedule.title.contains("알바") -> "🥤"
            schedule.title.contains("저녁") -> "🍽️"
            schedule.title.contains("명상") -> "🧘‍♂️"
            schedule.title.contains("독서") -> "📖"
            else -> "📅"
        }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = "${timeFormat.format(schedule.startDate)}~${timeFormat.format(schedule.endDate)}"
        return Triple(emoji, schedule.title, time)
    }

    // 현재 시나리오를 화면에 표시
    private fun showScenario() {
        if (scenarios.isEmpty()) return
        val scenario = scenarios[currentScenario]
        findViewById<TextView>(R.id.tvScenario).text = scenario.name

        val layout = findViewById<LinearLayout>(R.id.layoutScenario)
        layout.removeAllViews()

        for (schedule in scenario.schedules) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_schedule_card, layout, false)
            val (emoji, title, time) = scheduleToCard(schedule)
            view.findViewById<TextView>(R.id.tvEmoji).text = emoji
            view.findViewById<TextView>(R.id.tvTitle).text = title
            view.findViewById<TextView>(R.id.tvTime).text = time
            layout.addView(view)
        }
    }
}
