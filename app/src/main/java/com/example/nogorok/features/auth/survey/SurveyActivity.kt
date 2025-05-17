package com.example.nogorok.features.auth.survey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.example.nogorok.features.auth.survey.fragments.SurveyFinalFragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep1Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep2Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep3Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep4Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep5Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep6Fragment
import com.example.nogorok.features.auth.survey.fragments.SurveyStep7Fragment

class SurveyActivity : AppCompatActivity() {

    private val viewModel: SurveyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        // 첫 화면에 Step1 띄우기
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.surveyContainer, SurveyStep1Fragment())
            }
        }
    }

    fun navigateToNext(currentStep: String) {
        val nextFragment = when (currentStep) {
            "Step1" -> SurveyStep2Fragment()
            "Step2" -> SurveyStep3Fragment()
            "Step3" -> SurveyStep4Fragment()
            "Step4" -> SurveyStep5Fragment()
            "Step5" -> SurveyStep6Fragment()
            "Step6_YES" -> SurveyStep7Fragment()
            "Step6_NO" -> SurveyFinalFragment()
            "Step7" -> SurveyFinalFragment()
            else -> null
        }

        nextFragment?.let {
            supportFragmentManager.commit {
                replace(R.id.surveyContainer, it)
                addToBackStack(null)
            }
        }
    }

}
