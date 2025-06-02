package com.example.nogorok.features.auth.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.LoginActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.ResetPasswordRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class FindPasswordResetActivity : AppCompatActivity() {

    private var isNewPwVisible = false
    private var isConfirmPwVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password_reset)

        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val edtNewPassword = findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnToggleNewPassword = findViewById<ImageButton>(R.id.btnToggleNewPassword)
        val btnToggleConfirmPassword = findViewById<ImageButton>(R.id.btnToggleConfirmPassword)
        val passwordMatchMessage = findViewById<TextView>(R.id.passwordMatchMessage)
        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirm)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val email = intent.getStringExtra("email") ?: ""
        tvEmail.text = email

        btnBack.setOnClickListener { finish() }

        edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
        btnToggleNewPassword.setImageResource(R.drawable.ic_eye_off)

        edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
        btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off)

        btnToggleNewPassword.setOnClickListener {
            isNewPwVisible = !isNewPwVisible
            edtNewPassword.inputType = if (isNewPwVisible) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnToggleNewPassword.setImageResource(if (isNewPwVisible) R.drawable.ic_eye_on else R.drawable.ic_eye_off)
            edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
        }

        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPwVisible = !isConfirmPwVisible
            edtConfirmPassword.inputType = if (isConfirmPwVisible) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnToggleConfirmPassword.setImageResource(if (isConfirmPwVisible) R.drawable.ic_eye_on else R.drawable.ic_eye_off)
            edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
        }

        edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isNewPwVisible) {
                    edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_off)
                    edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
                }
                updatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isConfirmPwVisible) {
                    edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off)
                    edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
                }
                updatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnConfirm.setOnClickListener {
            val pw = edtNewPassword.text?.toString()?.trim() ?: ""
            val confirm = edtConfirmPassword.text?.toString()?.trim() ?: ""

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.authApi.resetPassword(
                        ResetPasswordRequest(email, pw, confirm)
                    )
                    if (response.isSuccessful) {
                        val intent = Intent(this@FindPasswordResetActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showAlert("비밀번호 변경에 실패했습니다.")
                    }
                } catch (e: Exception) {
                    showAlert("네트워크 오류가 발생했습니다.")
                }
            }
        }
    }

    private fun updatePasswordMatch() {
        val pw = findViewById<EditText>(R.id.edtNewPassword).text?.toString() ?: ""
        val confirm = findViewById<EditText>(R.id.edtConfirmPassword).text?.toString() ?: ""
        val passwordMatchMessage = findViewById<TextView>(R.id.passwordMatchMessage)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        if (confirm.isEmpty()) {
            passwordMatchMessage.visibility = TextView.GONE
            btnConfirm.isEnabled = false
        } else if (pw == confirm) {
            passwordMatchMessage.visibility = TextView.VISIBLE
            passwordMatchMessage.text = "비밀번호가 일치합니다."
            passwordMatchMessage.setTextColor(0xFF4CAF50.toInt())
            btnConfirm.isEnabled = true
        } else {
            passwordMatchMessage.visibility = TextView.VISIBLE
            passwordMatchMessage.text = "비밀번호가 일치하지 않습니다."
            passwordMatchMessage.setTextColor(0xFFD32F2F.toInt())
            btnConfirm.isEnabled = false
        }
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }
}