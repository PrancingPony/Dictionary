package com.wan.yalandan.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DictionaryReader dr = new DictionaryReader(R.raw.american_english,getBaseContext());
        final TextView tv = (TextView) findViewById(R.id.textView);

        tv.setText(dr.get4RandomWords().toString());
        tv.setText(getBaseContext().getResources().openRawResource(R.raw.american_english).toString());
        Button btn = (Button) findViewById(R.id.btnAnswer);
        btn.setOnClickListener(v -> tv.setText(dr.get4RandomWords().toString()));
    }
}
