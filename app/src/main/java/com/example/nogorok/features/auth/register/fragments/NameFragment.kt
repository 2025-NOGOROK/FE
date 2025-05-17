package com.example.nogorok.features.auth.register.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.RegisterActivity
import com.example.nogorok.features.auth.register.RegisterViewModel
import com.google.android.material.button.MaterialButton

class NameFragment : Fragment() {

    private lateinit var edtName: EditText
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_input_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)

        edtName = view.findViewById(R.id.edtName)
        btnNext = view.findViewById(R.id.btnNext)
        btnBack = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        edtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = !s.isNullOrBlank()
                btnNext.isEnabled = isNotEmpty
                if (isNotEmpty) {
                    btnNext.setBackgroundColor(Color.parseColor("#73605A"))
                    btnNext.setTextColor(Color.parseColor("#F4EED4"))
                    btnNext.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF4D403C"))
                } else {
                    btnNext.setBackgroundColor(Color.parseColor("#F4EED4"))
                    btnNext.setTextColor(Color.parseColor("#8073605A"))
                    btnNext.strokeColor = ColorStateList.valueOf(Color.parseColor("#804D403C"))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNext.setOnClickListener {
            val name = edtName.text.toString()
            viewModel.name = name

            (activity as? RegisterActivity)?.navigateToNext("Name")
        }
    }
}
