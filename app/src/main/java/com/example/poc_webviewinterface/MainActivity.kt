package com.example.poc_webviewinterface

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.web_journey.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_journey)
        setUpView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpView() {
        val webViewSettings: WebSettings = webViewJourney.settings
        webViewJourney.loadUrl("https://qa.skaleup.tech/employeeportal/#/")
        webViewSettings.javaScriptEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)
        webViewSettings.domStorageEnabled = true
        webViewSettings.databaseEnabled = true
        webViewJourney.addJavascriptInterface(PermissionJSInterface(this, webViewJourney), "Android")
    }
}