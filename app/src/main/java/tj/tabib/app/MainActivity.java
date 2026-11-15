package tj.tabib.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.setBackgroundColor(0xFF0B0F17);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setTextZoom(100);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return openOutside(request.getUrl());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return openOutside(Uri.parse(url));
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/www/index.html");
        }
    }

    /** tel: links open the phone dialer, web links open the browser; app pages stay inside. */
    private boolean openOutside(Uri uri) {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme();
        try {
            if (scheme.equals("tel")) {
                startActivity(new Intent(Intent.ACTION_DIAL, uri));
                return true;
            }
            if (scheme.equals("http") || scheme.equals("https") || scheme.equals("mailto")) {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
        } catch (ActivityNotFoundException ignored) {
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        // Close an open bottom sheet or go back inside the app first.
        web.evaluateJavascript(
            "(function(){var o=document.getElementById('ov');" +
            "if(o&&o.classList.contains('show')){o.classList.remove('show');return 'sheet';}" +
            "if(typeof view!=='undefined'&&view){view=null;render();return 'view';}" +
            "if(typeof tab!=='undefined'&&tab!=='home'){tab='home';render();return 'tab';}" +
            "return 'exit';})()",
            value -> {
                if ("\"exit\"".equals(value)) {
                    MainActivity.super.onBackPressed();
                }
            });
    }
}
