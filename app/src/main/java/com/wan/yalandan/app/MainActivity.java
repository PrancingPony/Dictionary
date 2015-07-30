package com.wan.yalandan.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.io.File;

public class MainActivity extends Activity {

    Button btnCallback;
    DownloadFileProcess dfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");

        final DownloadFileProcess.ICallbackUri downloadFinishedCallback = new DownloadFileProcess.ICallbackUri() {
            @Override
            public void callback(String uri) {
                Log.d("CALLED URI", uri);
            }
        };

        this.btnCallback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dfp = new DownloadFileProcess(downloadFinishedCallback, getBaseContext());
                dfp.getWordUriFromApi("book");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {

        this.btnCallback = (Button) findViewById(R.id.btnCallback);
    }
}
