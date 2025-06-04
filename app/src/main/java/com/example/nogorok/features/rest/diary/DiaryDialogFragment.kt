package com.example.nogorok.features.rest.diary

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.nogorok.R
import java.text.SimpleDateFormat
import java.util.*

class DiaryDialogFragment : DialogFragment() {

    private val viewModel: DiaryViewModel by viewModels()

    private var selectedEmotionButton: Button? = null
    private var selectedFatigueButton: Button? = null
    private var selectedWeatherButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_dialog_diary, null)

        val emotionButtons = listOf(
            view.findViewById<Button>(R.id.btnJoy),
            view.findViewById<Button>(R.id.btnNormal),
            view.findViewById<Button>(R.id.btnSad),
            view.findViewById<Button>(R.id.btnAngry),
            view.findViewById<Button>(R.id.btnFurious)
        )
        val fatigueButtons = listOf(
            view.findViewById<Button>(R.id.btnVeryTired),
            view.findViewById<Button>(R.id.btnFatigueNormal),
            view.findViewById<Button>(R.id.btnEnergetic)
        )
        val weatherButtons = listOf(
            view.findViewById<Button>(R.id.btnSunny),
            view.findViewById<Button>(R.id.btnCloudy),
            view.findViewById<Button>(R.id.btnRainy),
            view.findViewById<Button>(R.id.btnSnowy)
        )

        val editText = view.findViewById<EditText>(R.id.editSpecialNote)

        // 버튼 클릭 스타일 적용
        emotionButtons.forEach { button ->
            button.setOnClickListener {
                selectedEmotionButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedEmotionButton = button
            }
        }
        fatigueButtons.forEach { button ->
            button.setOnClickListener {
                selectedFatigueButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedFatigueButton = button
            }
        }
        weatherButtons.forEach { button ->
            button.setOnClickListener {
                selectedWeatherButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedWeatherButton = button
            }
        }

        // 오늘 날짜 기준 조회
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // ✅ 기존 데이터 불러오기
        viewModel.fetchHaru(
            date = today,
            onResult = { diary ->
                diary?.let {
                    // 감정
                    val emotionId = when (it.emotion) {
                        "JOY" -> R.id.btnJoy
                        "NORMAL" -> R.id.btnNormal
                        "DEPRESSED" -> R.id.btnSad
                        "IRRITATED" -> R.id.btnAngry
                        "ANGRY" -> R.id.btnFurious
                        else -> null
                    }
                    emotionId?.let { id ->
                        val btn = view.findViewById<Button>(id)
                        applySelectedStyle(btn)
                        selectedEmotionButton = btn
                    }

                    // 피로도
                    val fatigueId = when (it.fatigue) {
                        "VERY_TIRED" -> R.id.btnVeryTired
                        "NORMAL" -> R.id.btnFatigueNormal
                        "ENERGETIC" -> R.id.btnEnergetic
                        else -> null
                    }
                    fatigueId?.let { id ->
                        val btn = view.findViewById<Button>(id)
                        applySelectedStyle(btn)
                        selectedFatigueButton = btn
                    }

                    // 날씨
                    val weatherId = when (it.weather) {
                        "SUNNY" -> R.id.btnSunny
                        "CLOUDY" -> R.id.btnCloudy
                        "RAIN" -> R.id.btnRainy
                        "SNOW" -> R.id.btnSnowy
                        else -> null
                    }
                    weatherId?.let { id ->
                        val btn = view.findViewById<Button>(id)
                        applySelectedStyle(btn)
                        selectedWeatherButton = btn
                    }

                    // 메모
                    editText.setText(it.specialNotes ?: "")
                }
            },
            onError = {
                Log.e("DiaryDialog", "데이터 조회 실패", it)
            }
        )

        // 확인 버튼
        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val emotion = when (selectedEmotionButton?.id) {
                R.id.btnJoy -> "JOY"
                R.id.btnNormal -> "NORMAL"
                R.id.btnSad -> "DEPRESSED"
                R.id.btnAngry -> "IRRITATED"
                R.id.btnFurious -> "ANGRY"
                else -> ""
            }
            val fatigue = when (selectedFatigueButton?.id) {
                R.id.btnVeryTired -> "VERY_TIRED"
                R.id.btnFatigueNormal -> "NORMAL"
                R.id.btnEnergetic -> "ENERGETIC"
                else -> ""
            }
            val weather = when (selectedWeatherButton?.id) {
                R.id.btnSunny -> "SUNNY"
                R.id.btnCloudy -> "CLOUDY"
                R.id.btnRainy -> "RAIN"
                R.id.btnSnowy -> "SNOW"
                else -> ""
            }
            val note = editText.text?.toString()?.trim() ?: ""

            if (emotion.isNotBlank() && fatigue.isNotBlank() && weather.isNotBlank()) {
                Log.d("DiaryDialog", "요청 보냄: $emotion, $fatigue, $weather, $note")
                viewModel.submitHaru(
                    emotion = emotion,
                    fatigueLevel = fatigue,
                    weather = weather,
                    specialNotes = note,
                    onSuccess = {
                        Log.d("DiaryDialog", "저장 성공 -> 종료")
                        dismiss()
                    },
                    onError = {
                        Log.e("DiaryDialog", "저장 실패", it)
                    }
                )
            } else {
                Log.w("DiaryDialog", "필수 항목을 모두 선택해야 합니다.")
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun resetButtonStyle(button: Button) {
        button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.brown_70)
        button.setTextColor(Color.WHITE)
    }

    private fun applySelectedStyle(button: Button) {
        button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.brown)
        button.setTextColor(Color.WHITE)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
