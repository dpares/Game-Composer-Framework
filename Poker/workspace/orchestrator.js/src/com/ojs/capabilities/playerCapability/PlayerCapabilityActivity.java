package com.ojs.capabilities.playerCapability;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ojs.R;

public class PlayerCapabilityActivity extends Activity {

	
	protected static final String TAG = PlayerCapabilityActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RelativeLayout r = new RelativeLayout(getApplicationContext());
		
		setContentView(r);
		
		p("PlayerCapabilityActivity created!");
		
		try {

            Bundle b = getIntent().getExtras();
            String url = b.getString("url_to_show");
            setContentView(R.layout.webview);
            WebView webView = (WebView) findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);

                    return true;
                }

            });

            webView.loadUrl(url);

			p("the end");
		} catch (Exception e) {
			p(e.toString());
		}
		
		
	}


	
	
	private void p(String s) {
		Log.d(TAG, s);
	}

	
	
}
