package com.example.poc_webviewinterface

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

import kotlinx.android.synthetic.main.activity_twaactivity.*


class TWAActivity : AppCompatActivity() {
    private val URL: Uri = Uri.parse("https://peconn.github.io/starters/")
    private val UPDATED_URL: Uri = Uri.parse("https://peconn.github.io/starters/?updated=true")

    private var mSession: CustomTabsSession? = null
    private var mConnection: CustomTabsServiceConnection? = null

    private var mExtraButton: Button? = null


    override fun onStart() {
        super.onStart()
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                mSession = client.newSession(null)
                client.warmup(0)
                mExtraButton?.isEnabled = true
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {}
        }
        val packageName: String? = CustomTabsClient.getPackageName(this@TWAActivity, null)
        if (packageName == null) {
            Toast.makeText(this, "Can't find a Custom Tabs provider.", Toast.LENGTH_SHORT).show()
            return
        }
        CustomTabsClient.bindCustomTabsService(this, packageName,
            mConnection as CustomTabsServiceConnection
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.poc_webviewinterface.R.layout.activity_twaactivity)
        mExtraButton = findViewById(R.id.launch_twa)
        launch_twa.setOnClickListener(View.OnClickListener { view: View? ->
            val intent: CustomTabsIntent = CustomTabsIntent.Builder(mSession).build()
            intent.launchUrl(this@TWAActivity, URL)
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    val updateIntent: CustomTabsIntent = CustomTabsIntent.Builder(mSession).build()
                    updateIntent.launchUrl(this@TWAActivity, UPDATED_URL)
                }, 5000)
            }
        })
    }
    override fun onStop() {
        super.onStop()
        if (mConnection == null) return
        unbindService(mConnection!!)
        mConnection = null
        mExtraButton!!.isEnabled = false
    }

}