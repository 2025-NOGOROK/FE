package com.example.nogorok.features.survey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogorok.databinding.ActivityBannerSurveyBinding

class BannerSurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerSurveyBinding
    private lateinit var adapter: QuestionAdapter

    private val questions = listOf(
        "1) 나는 사소한 일에도 쉽게 흥분하거나\n짜증이 난다.",
        "2) 나는 긴장되거나 신경이 곤두서는 기\n분이 든다.",
        "3) 예기치 못한 일들이 생기면 나는 쉽게\n압도당하는 기분이 든다.",
        "4) 나는 감정의 기복이 심하다고 느낀다.",
        "5) 나는 스트레스를 받으면 몸이 피로\n하거나 수면에 영향을 받는다.",
        "6) 내가 감정을 조절하는 것이 어렵다고\n느낀다.",
        "7) 최근, 나를 도와줄 사람이 없다고 느낀\n적이 있다."
    )

    private val options = listOf(
        "전혀 그렇지 않아요",
        "가끔 그런 것 같아요",
        "자주 그런 편이에요",
        "거의 항상 그래요"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        adapter = QuestionAdapter(
            items = questions.map { Question(it, -1) }.toMutableList(),
            options = options
        ) { answers ->
            // TODO: 모든 답변 완료 후 처리
            finish()
        }

        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = adapter
    }
}
