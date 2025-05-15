package com.example.nogorok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class SplashFragment : Fragment() {

    companion object {
        fun newInstance(text: String, imageResId: Int): SplashFragment {
            val fragment = SplashFragment()
            val args = Bundle()
            args.putString("text", text)
            args.putInt("imageResId", imageResId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        // 데이터 설정
        val textView = view.findViewById<TextView>(R.id.textView)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        val text = arguments?.getString("text") ?: "기본 텍스트"
        val imageResId = arguments?.getInt("imageResId") ?: android.R.drawable.ic_menu_gallery

        textView.text = text
        imageResId.let { imageView.setImageResource(it) }

        return view
    }
}