package info.microalg.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;


public class DisplayResult extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        WebView webview = (WebView) findViewById(R.id.webViewResult);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                String html_src = "<html><head><meta charset=\"utf-8\">";
                html_src += "<script type=\"text/javascript\" src=\"www/js/emulisp_core.js\"></script>";
                html_src += "</head><body>";
                html_src += "<div id=\"#display\"></div>";
                html_src += "<div id=\"#error\"></div>";
                html_src += "<script>document.write(EMULISP_CORE.eval('(+ 2 2)'));</script>";
                html_src += "</body></html>";
                webview.loadDataWithBaseURL("file:///android_asset/", html_src, "text/html", "utf-8", "");
            } else {
                String message = getString(R.string.incompatible_data);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        } else {
            String message = getString(R.string.incompatible_action);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
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
