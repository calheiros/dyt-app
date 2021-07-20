package com.jeff.dytbr;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class MyWebChromeClient extends WebChromeClient {

    ProgressBar myProgressBar;

    public MyWebChromeClient(Activity myActivity) {
        this.myProgressBar = myActivity.findViewById(R.id.myProgressBar);
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        myProgressBar.setProgress(newProgress);
        Log.i("PROGRESS" , "progress: " + newProgress);
    }
}
