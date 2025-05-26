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
        emotionButtons.forEach { button ->
            button.setOnClickListener {
                selectedEmotionButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedEmotionButton = button
            }
        }

        val fatigueButtons = listOf(
            view.findViewById<Button>(R.id.btnVeryTired),
            view.findViewById<Button>(R.id.btnFatigueNormal),
            view.findViewById<Button>(R.id.btnEnergetic)
        )
        fatigueButtons.forEach { button ->
            button.setOnClickListener {
                selectedFatigueButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedFatigueButton = button
            }
        }

        val weatherButtons = listOf(
            view.findViewById<Button>(R.id.btnSunny),
            view.findViewById<Button>(R.id.btnCloudy),
            view.findViewById<Button>(R.id.btnRainy),
            view.findViewById<Button>(R.id.btnSnowy)
        )
        weatherButtons.forEach { button ->
            button.setOnClickListener {
                selectedWeatherButton?.let { resetButtonStyle(it) }
                applySelectedStyle(button)
                selectedWeatherButton = button
            }
        }

        val editText = view.findViewById<EditText>(R.id.editSpecialNote)

        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val emotion = selectedEmotionButton?.text?.toString()?.trim() ?: ""
            val fatigue = selectedFatigueButton?.text?.toString()?.trim() ?: ""
            val weather = selectedWeatherButton?.text?.toString()?.trim() ?: ""
            val note = editText.text.toString().trim()

            if (emotion.isNotBlank() && fatigue.isNotBlank() && weather.isNotBlank()) {
                Log.d("DiaryDialog", "요청 보냄: $emotion, $fatigue, $weather, $note")
                viewModel.submitHaru(
                    emotion, fatigue, weather, note,
                    onSuccess = {
                        Log.d("DiaryDialog", "서버 응답 성공 -> 다이얼로그 종료")
                        dismiss()
                    },
                    onError = {
                        Log.e("DiaryDialog", "서버 요청 실패", it)
                        // 예: Toast.makeText(requireContext(), "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Log.w("DiaryDialog", "필수 항목 선택 안 됨")
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
