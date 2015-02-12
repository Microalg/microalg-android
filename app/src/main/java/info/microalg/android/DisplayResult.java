package info.microalg.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DisplayResult extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        // https://developer.chrome.com/devtools/docs/remote-debugging#debugging-webviews
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                interpret(sharedText);
            } else {
                String message = getString(R.string.incompatible_data);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            StringBuilder sb = new StringBuilder();
            try {
                String path = intent.getData().getPath();
                FileInputStream fis =  new FileInputStream(path);
                BufferedReader br = new BufferedReader( new InputStreamReader(fis, "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (FileNotFoundException e) {
                String message = getString(R.string.file_not_found);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                String message = getString(R.string.problem_reading_file);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
            interpret(sb.toString());
        } else {
            String message = getString(R.string.incompatible_action);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void interpret(String src) {

        // Some escapes in MicroAlg src:
        final String final_src =
                src.replace("\\", "\\\\")       // JS has to see the backslashes
                   .replace("'", "\\'")         // src is '-delimited in the JS call
                   .replaceAll("\n", "\\\\n");  // JS has to see the newlines
        WebView webview = (WebView) findViewById(R.id.webViewResult);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                final EditText et = new EditText(view.getContext());
                et.setSingleLine();
                et.setText(defaultValue);
                new AlertDialog.Builder(view.getContext())
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setView(et)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm(et.getText().toString());
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    result.cancel();
                                }
                            })
                    .show();
                return true;
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:ide_action('" + final_src + "')");
            }
        });

        webview.loadUrl("file:///android_asset/www/display_result.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
