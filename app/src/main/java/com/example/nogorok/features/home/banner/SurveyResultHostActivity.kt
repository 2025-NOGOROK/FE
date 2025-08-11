package com.example.nogorok.features.home.banner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R
import com.example.nogorok.features.survey.SurveyResultFragment

class SurveyResultHostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_result_host)

        val resultType = intent.getStringExtra("result") ?: SurveyResultFragment.ResultType.STABLE.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SurveyResultFragment.newInstance(SurveyResultFragment.ResultType.valueOf(resultType)))
                .commit()
        }
    }
}
