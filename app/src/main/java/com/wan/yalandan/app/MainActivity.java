package com.wan.yalandan.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DictionaryReader dr = new DictionaryReader(R.raw.american_english, getBaseContext());

//        final DownloadFileProcess.ICallbackUri downloadFinishedCallback = uri -> Log.d("CALLED URI", uri);

    }

    @Override
    protected void onStart() {
        super.onStart();

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
}
