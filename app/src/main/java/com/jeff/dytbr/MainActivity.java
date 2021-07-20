package com.jeff.dytbr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.webkit.WebViewFeature;
import androidx.webkit.WebSettingsCompat;

public class MainActivity extends Activity { 

    private WebView myWebView;
    public static String HOME_URL="https://dyttextura.blogspot.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = findViewById(R.id.webview);
         
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
       
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(myWebView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        myWebView.setWebViewClient(new MyWebViewClient(this));
        myWebView.setWebChromeClient(new MyWebChromeClient(this));
        load(HOME_URL);
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ಠ_ಠ");
            builder.setMessage("Quer mesmo sair?");
            builder.setPositiveButton("sim", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface inter, int p2) {
                        finish();
                    }
                });
            builder.setNegativeButton("ficar", null);
            builder.create().show();
        }
    }
    public void load(String url) {
        myWebView.loadUrl(url);
    }
}

