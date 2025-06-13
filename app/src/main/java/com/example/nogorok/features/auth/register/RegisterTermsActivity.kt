package com.example.nogorok.features.auth.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.terms.HealthTermsActivity
import com.example.nogorok.features.auth.register.terms.LocationTermsActivity
import com.example.nogorok.features.auth.register.terms.PrivacyTermsActivity
import com.example.nogorok.features.auth.register.terms.ServiceTermsActivity
import com.google.android.material.button.MaterialButton

class RegisterTermsActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel

    private val LOCATION_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_terms)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        val btnServiceDetail = findViewById<ImageButton>(R.id.btnServiceDetail)
        val btnPrivacyDetail = findViewById<ImageButton>(R.id.btnPrivacyDetail)
        val btnHealthDetail = findViewById<ImageButton>(R.id.btnHealthDetail)
        val btnLocationDetail = findViewById<ImageButton>(R.id.btnLocationDetail)

        btnServiceDetail.setOnClickListener {
            startActivity(Intent(this, ServiceTermsActivity::class.java))
        }
        btnPrivacyDetail.setOnClickListener {
            startActivity(Intent(this, PrivacyTermsActivity::class.java))
        }
        btnHealthDetail.setOnClickListener {
            startActivity(Intent(this, HealthTermsActivity::class.java))
        }
        btnLocationDetail.setOnClickListener {
            startActivity(Intent(this, LocationTermsActivity::class.java))
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val chkAll = findViewById<CheckBox>(R.id.chkAll)
        val chkService = findViewById<CheckBox>(R.id.chkService)
        val chkPrivacy = findViewById<CheckBox>(R.id.chkPrivacy)
        val chkHealth = findViewById<CheckBox>(R.id.chkHealth)
        val chkLocation = findViewById<CheckBox>(R.id.chkLocation)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        val allChecks = listOf(chkService, chkPrivacy, chkHealth, chkLocation)

        chkAll.setOnCheckedChangeListener { _, isChecked ->
            allChecks.forEach { it.setOnCheckedChangeListener(null) }
            allChecks.forEach { it.isChecked = isChecked }
            allChecks.forEach { chk ->
                chk.setOnCheckedChangeListener { _, _ ->
                    updateAllCheck(chkAll, allChecks, btnNext)
                }
            }

            // ✅ ViewModel 동기화
            viewModel.agreedService = isChecked
            viewModel.agreedPrivacy = isChecked
            viewModel.agreedHealth = isChecked
            viewModel.agreedLocation = isChecked

            updateButtonState(btnNext, allChecks)

            // ✅ 위치 항목만 따로 권한 요청
            if (isChecked) requestLocationPermissionIfNeeded()
        }

        chkLocation.setOnCheckedChangeListener { _, isChecked ->
            updateAllCheck(chkAll, allChecks, btnNext)
            viewModel.agreedLocation = isChecked

            if (isChecked) requestLocationPermissionIfNeeded()
        }

        allChecks.filter { it != chkLocation }.forEach { chk ->
            chk.setOnCheckedChangeListener { _, _ ->
                updateAllCheck(chkAll, allChecks, btnNext)
            }
        }

        updateButtonState(btnNext, allChecks)

        btnNext.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                putExtra("AGREED_SERVICE", chkService.isChecked)
                putExtra("AGREED_PRIVACY", chkPrivacy.isChecked)
                putExtra("AGREED_HEALTH", chkHealth.isChecked)
                putExtra("AGREED_LOCATION", chkLocation.isChecked)
            }
            startActivity(intent)
        }
    }

    private fun updateAllCheck(
        chkAll: CheckBox,
        allChecks: List<CheckBox>,
        btnNext: MaterialButton
    ) {
        val allChecked = allChecks.all { it.isChecked }

        chkAll.setOnCheckedChangeListener(null)
        chkAll.isChecked = allChecked
        chkAll.setOnCheckedChangeListener { _, isChecked ->
            allChecks.forEach { it.setOnCheckedChangeListener(null) }
            allChecks.forEach { it.isChecked = isChecked }
            allChecks.forEach { chk ->
                chk.setOnCheckedChangeListener { _, _ ->
                    updateAllCheck(chkAll, allChecks, btnNext)
                }
            }

            // ✅ ViewModel 동기화 재적용
            viewModel.agreedService = isChecked
            viewModel.agreedPrivacy = isChecked
            viewModel.agreedHealth = isChecked
            viewModel.agreedLocation = isChecked

            updateButtonState(btnNext, allChecks)

            // ✅ 위치 권한도 다시 체크
            if (isChecked) requestLocationPermissionIfNeeded()
        }

        updateButtonState(btnNext, allChecks)
    }

    private fun updateButtonState(button: MaterialButton, checks: List<CheckBox>) {
        val allChecked = checks.all { it.isChecked }
        button.isEnabled = allChecked
    }

    private fun requestLocationPermissionIfNeeded() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

        if (ContextCompat.checkSelfPermission(this, fine) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, coarse) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(fine, coarse), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!granted) {
                Toast.makeText(this, "위치 권한이 없으면 일부 기능이 제한될 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
