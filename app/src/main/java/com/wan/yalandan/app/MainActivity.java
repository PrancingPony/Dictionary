package com.wan.yalandan.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    Button btnAnswer;
    DownloadFileProcess dfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseAdapter dbAdapter = new DatabaseAdapter(getBaseContext());
        DictionaryReader dr = new DictionaryReader(R.raw.american_english, getBaseContext());
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        final DownloadFileProcess.ICallbackUri downloadFinishedCallback = uri -> Log.d("CALLED URI", uri);

        this.btnAnswer.setOnClickListener(v -> {
            dfp = new DownloadFileProcess(downloadFinishedCallback, getBaseContext());
            dfp.getWordUriFromApi("book");

        });
        //----TEST CODE------------
        final TextView tv = (TextView) findViewById(R.id.textView);


//        btnAnswer.setOnClickListener(v -> {
//
//            Cursor c = dbAdapter.getUri("book");
//            StringBuilder sB = new StringBuilder();
//            while (c.moveToNext()) {
//                sB.append(c.getString(c.getColumnIndex(DatabaseAdapter.TOKENWORDS_URI))).append("\n");
//            }
//            tv.setText(sB.toString());
//        });
        //----TEST END-------------
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
    }


}

