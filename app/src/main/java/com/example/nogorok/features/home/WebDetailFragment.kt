package com.example.nogorok.features.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.nogorok.R

class WebDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_detail, container, false)
        val webView: WebView = view.findViewById(R.id.webView)

        // WebView 기본 설정
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        // 전달받은 URL 열기
        val url = arguments?.getString("url") ?: "https://www.naver.com"
        webView.loadUrl(url)

        return view
    }
}
