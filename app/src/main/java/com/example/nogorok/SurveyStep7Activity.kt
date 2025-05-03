// 파일 위치: app/src/main/java/com/example/nogorok/SurveyStep7Activity.kt
package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class SurveyStep7Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_step7)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val edtMethod1 = findViewById<EditText>(R.id.edtMethod1)
        val edtMethod2 = findViewById<EditText>(R.id.edtMethod2)
        val edtMethod3 = findViewById<EditText>(R.id.edtMethod3)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 뒤로가기 버튼
        btnBack.setOnClickListener { finish() }

        // 텍스트 입력 감지해서 버튼 활성화 및 텍스트 컬러 변경
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState(edtMethod1, edtMethod2, edtMethod3, btnNext)
                updateEditTextColor(edtMethod1)
                updateEditTextColor(edtMethod2)
                updateEditTextColor(edtMethod3)
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        edtMethod1.addTextChangedListener(watcher)
        edtMethod2.addTextChangedListener(watcher)
        edtMethod3.addTextChangedListener(watcher)

        // 다음 버튼 클릭 시
        btnNext.setOnClickListener {
            // 세 개 모두 입력되어 있으면 다음 페이지로 이동
            val intent = Intent(this, SurveyFinalActivity::class.java)
            // 입력값을 넘기고 싶으면 아래처럼 사용 가능
            // intent.putExtra("method1", edtMethod1.text.toString())
            // intent.putExtra("method2", edtMethod2.text.toString())
            // intent.putExtra("method3", edtMethod3.text.toString())
            startActivity(intent)
        }
    }

    // EditText 모두 입력해야 다음 버튼 활성화
    private fun updateNextButtonState(
        edt1: EditText, edt2: EditText, edt3: EditText, btnNext: MaterialButton
    ) {
        btnNext.isEnabled =
            edt1.text.isNotBlank() && edt2.text.isNotBlank() && edt3.text.isNotBlank()
    }

    // 입력값이 있으면 텍스트 컬러 100%, 없으면 50%
    private fun updateEditTextColor(editText: EditText) {
        if (editText.text.isNullOrBlank()) {
            editText.setTextColor(0x80F4EED4.toInt()) // 50% 투명도
        } else {
            editText.setTextColor(0xFFF4EED4.toInt()) // 100% 불투명
        }
    }
}
