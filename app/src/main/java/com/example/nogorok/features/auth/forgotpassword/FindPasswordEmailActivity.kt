package com.example.nogorok.features.auth.forgotpassword

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nogorok.R
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.CheckEmailRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindPasswordEmailActivity : AppCompatActivity() {

    private var isValidationActivated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password_email)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val emailError = findViewById<TextView>(R.id.emailError)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        val colorFull = ContextCompat.getColor(this, R.color.primary)
        val color70 = ContextCompat.getColor(this, R.color.primary_70)

        btnNext.backgroundTintList = ColorStateList.valueOf(color70)

        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = !s.isNullOrEmpty()
                btnNext.backgroundTintList = ColorStateList.valueOf(
                    if (isNotEmpty) colorFull else color70
                )
                if (isValidationActivated) {
                    emailError.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNext.setOnClickListener {
            isValidationActivated = true
            val email = edtEmail.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) {
                emailError.visibility = TextView.VISIBLE
            } else {
                emailError.visibility = TextView.GONE
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.authApi.checkEmail(CheckEmailRequest(email))
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val intent = Intent(this@FindPasswordEmailActivity, FindPasswordResetActivity::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                            } else {
                                showErrorDialog("일치하는 회원정보가 없습니다.")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            showErrorDialog("서버와 통신 중 오류가 발생했습니다.")
                        }
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }
}
