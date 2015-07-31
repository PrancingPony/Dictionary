package com.wan.yalandan.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
        DictionaryReader dr = new DictionaryReader(R.raw.american_english, getBaseContext());
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
    }

    @Override

        //----TEST CODE------------
        final TextView tv = (TextView) findViewById(R.id.textView);
        final Button btnAnswer = (Button) findViewById(R.id.btnAnswer);

        DatabaseAdapter dbAdapter = new DatabaseAdapter(getBaseContext());
        DictionaryReader dr = new DictionaryReader(R.raw.american_english,getBaseContext());
        btnAnswer.setOnClickListener(v -> {

            Cursor c = dbAdapter.getUri("book");
            StringBuilder sB = new StringBuilder();
            while (c.moveToNext()) {
                sB.append(c.getString(c.getColumnIndex(DatabaseAdapter.TOKENWORDS_URI)) + "\n");
            }
            tv.setText(sB.toString());
        });
        //----TEST END-------------
    }

    public void init() {

        this.btnCallback = (Button) findViewById(R.id.btnCallback);
    }
}
