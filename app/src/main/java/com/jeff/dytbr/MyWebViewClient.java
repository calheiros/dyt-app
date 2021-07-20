package com.jeff.dytbr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyWebViewClient extends WebViewClient implements OnClickListener {

    private MainActivity mainActivity;
    private View myProgressLayout;
    private View myErrorLayout;
    private TextView myErrorTextView;
    private Button myUpdateButton;
    private boolean error;

    public MyWebViewClient(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
          
        this.myProgressLayout = mainActivity.findViewById(R.id.progress_LinearLayout);
        this.myErrorLayout = mainActivity.findViewById(R.id.errorLinearLayout);
        this.myErrorTextView = mainActivity.findViewById(R.id.errorTextView);
        this.myUpdateButton = mainActivity.findViewById(R.id.updateButton);
        
        myUpdateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mainActivity.load(MainActivity.HOME_URL);
    }

    
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        myProgressLayout.setVisibility(view.GONE);
        if(!error)
            myErrorLayout.setVisibility(view.GONE);
        
    }
    
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        error = false;
        super.onPageStarted(view, url, favicon);
        myProgressLayout.setVisibility(view.VISIBLE);
        
    }
    
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        error = true;
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (errorCode != -2) 
            myErrorTextView.setText(description);
            
        myErrorLayout.setVisibility(View.VISIBLE);
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if ("dyttextura.blogspot.com".equals(Uri.parse(url).getHost())) {
            // This is my website, so do not override; let my WebView load the page
            return false;
        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mainActivity.startActivity(intent);
        
        return true;
    }
}
