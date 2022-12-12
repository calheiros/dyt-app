package com.jeff.dytbr

import android.app.Activity
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar

class MyWebChromeClient(myActivity: Activity) : WebChromeClient() {
    var myProgressBar: ProgressBar

    init {
        myProgressBar = myActivity.findViewById(R.id.my_progressBar)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        myProgressBar.progress = newProgress
        Log.i("PROGRESS", "progress: $newProgress")
    }
}