@file:Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")

package com.jeff.dytbr

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*

@Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")
class MyWebViewClient(private val mainActivity: MainActivity) : WebViewClient(),
    View.OnClickListener {
    private val myErrorLayout: View
    private val myErrorTextView: TextView
    private val myUpdateButton: Button
    private val myProgressBar: View
    private var error = false
    private val sharedPref: SharedPreferences

    init {
        myProgressBar = mainActivity.findViewById(R.id.my_progressBar)
        myErrorLayout = mainActivity.findViewById(R.id.errorLinearLayout)
        myErrorTextView = mainActivity.findViewById(R.id.errorTextView)
        myUpdateButton = mainActivity.findViewById(R.id.updateButton)
        myUpdateButton.setOnClickListener(this)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
    }

    override fun onClick(view: View) {
        mainActivity.load(MainActivity.HOME_URL)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        myProgressBar.visibility = View.GONE
        if (!error) myErrorLayout.visibility = View.GONE
        if (isLinkShortener(Uri.parse(url).scheme)) {
            Toast.makeText(mainActivity, "SHORTENER", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLinkShortener(scheme: String?): Boolean {
        for (url in shorteners_urls) {
            if (url == scheme) {
                return true
            }
        }
        return false
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        error = false
        super.onPageStarted(view, url, favicon)
        myProgressBar.visibility = View.VISIBLE
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
    ) {
        handleError(error!!.errorCode, error.description)
        super.onReceivedError(view, request, error)
    }

    private fun handleError(errorCode: Int, description: CharSequence?) {
        error = true
        if (errorCode != -2) myErrorTextView.text = description
        myErrorLayout.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onReceivedError(
        view: WebView, errorCode: Int, description: String, failingUrl: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return
        handleError(errorCode, description)
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val host = Uri.parse(url).host
        if ("dyttextura.blogspot.com" == host) {
            // This is my website, so do not override; let my WebView load the page
            return false
        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        if (sharedPref.getBoolean(
                mainActivity.getString(R.string.key_show_tip), true
            ) && isLinkShortener(host)
        ) {
            showDownloadTip(url)
            return true
        }
        openExternalApp(url)
        return true
    }

    private fun showDownloadTip(url: String) {
        val rootView = mainActivity.layoutInflater.inflate(R.layout.download_tip_layout, null)
        val videoView = rootView.findViewById<VideoView>(R.id.video_view)
        val checkBox = rootView.findViewById<CheckBox>(R.id.check_box_do_not_show_again)
        val path = "android.resource://" + mainActivity.packageName + "/" + R.raw.download_tutorial
        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
        videoView.setOnCompletionListener { mp -> mp.start() }
        val builder = AlertDialog.Builder(
            mainActivity
        )
        builder.setTitle("Como baixar")
        builder.setView(rootView)
        builder.setPositiveButton(mainActivity.getString(android.R.string.ok)) { dialog, which ->
            openExternalApp(
                url
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setOnDismissListener {
                if (checkBox.isChecked) sharedPref.edit()
                    .putBoolean(mainActivity.getString(R.string.key_show_tip), false).apply()
                openExternalApp(url)
            }
        }
        builder.create().show()
    }

    private fun openExternalApp(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mainActivity.startActivity(intent)
    }

    companion object {
        var shorteners_urls = arrayOf("4br.me", "linkebr.com", "seulink.online")
    }
}