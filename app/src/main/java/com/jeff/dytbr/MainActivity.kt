package com.jeff.dytbr

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var myWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myWebView = findViewById(R.id.webview)
        val webSettings = myWebView?.settings
        if (webSettings != null) {
            webSettings.javaScriptEnabled = true
        }
        startActivity(Intent(this, ExplorerActivity::class.java))

        myWebView?.webViewClient = MyWebViewClient(this)
        myWebView?.webChromeClient = MyWebChromeClient(this)
        load(HOME_URL)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (myWebView!!.canGoBack()) {
            myWebView!!.goBack()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ಠ_ಠ")
            builder.setMessage(R.string.exit_confirmation_message)
            builder.setPositiveButton(getString(android.R.string.ok)) { _, _ -> finish() }
            builder.setNegativeButton(getString(android.R.string.cancel), null)
            builder.create().show()
        }
    }

    fun load(url: String?) {
        myWebView!!.loadUrl(url!!)
    }

    companion object {
        @JvmField
        var HOME_URL = "https://dyttextura.blogspot.com/"
    }
}