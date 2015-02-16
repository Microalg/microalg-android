package info.microalg.android;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // https://developer.chrome.com/devtools/docs/remote-debugging#debugging-webviews
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        WebView webview = (WebView) findViewById(R.id.webViewAbout);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e("version info", "PackageManager.NameNotFoundException");
        }
        final String final_version = version;
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:populate_version('" + final_version + "')");
            }
        });
        webview.loadUrl("file:///android_asset/www/about.html");
    }
}
