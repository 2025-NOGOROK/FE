package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SignupLoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup_login, container, false)

        view.findViewById<Button>(R.id.btn_signup).setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        return view
    }
}
