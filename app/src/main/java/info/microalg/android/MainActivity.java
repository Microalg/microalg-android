package info.microalg.android;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Deploy example files if not found.
        File microalgDirectory = new File(Environment.getExternalStorageDirectory() + "/MicroAlg");
        if (! microalgDirectory.exists()) {
            microalgDirectory.mkdirs();
            AssetManager assetManager = getAssets();
            String[] files = null;
            try {
                files = assetManager.list("examples");
            } catch (IOException e) {
                Log.e("Deploy examples", "Failed to get asset file list.", e);
            }
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("examples/" + filename);
                    File outFile = new File(microalgDirectory, filename);
                    out = new FileOutputStream(outFile);
                    // Copy in to out:
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                } catch (IOException e) {
                    Log.e("Deploy examples", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        }
        setContentView(R.layout.activity_main);
        String html_src = "<html><body>(Afficher \"Bonjour !\")</body></html>";
        WebView webview = (WebView) findViewById(R.id.webView);
        webview.loadData(html_src, "text/html", "utf-8");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, About.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
