package com.example.nogorok.features.auth.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.SignupLoginActivity

class OnboardingFragment : Fragment() {

    companion object {
        fun newInstance(
            title: String,
            subtitle: String,
            imageResId: Int,
            isLastPage: Boolean
        ): OnboardingFragment {
            val fragment = OnboardingFragment()
            fragment.arguments = Bundle().apply {
                putString("title", title)
                putString("subtitle", subtitle)
                putInt("imageResId", imageResId)
                putBoolean("isLastPage", isLastPage)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_onboarding, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = arguments?.getString("title") ?: ""
        val subtitle = arguments?.getString("subtitle") ?: ""
        val imageResId = arguments?.getInt("imageResId") ?: 0
        val isLastPage = arguments?.getBoolean("isLastPage") ?: false

        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<TextView>(R.id.tvSubtitle).text = subtitle
        view.findViewById<ImageView>(R.id.ivImage).setImageResource(imageResId)

        val btnNext = view.findViewById<View>(R.id.btnNext)
        btnNext.setOnClickListener {
            if (isLastPage) {
                startActivity(Intent(requireContext(), SignupLoginActivity::class.java))
                requireActivity().finish()
            } else {
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.onboardingViewPager)
                viewPager.currentItem += 1
            }
        }
    }
}
