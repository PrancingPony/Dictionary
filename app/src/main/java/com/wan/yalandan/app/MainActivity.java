package com.wan.yalandan.app;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Scanner;


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

        tv.setText(dr.get4RandomWord().toString());
        tv.setText(getBaseContext().getResources().openRawResource(R.raw.american_english).toString());
        Button btn = (Button) findViewById(R.id.btnAnswer);
        btn.setOnClickListener(v -> tv.setText(dr.get4RandomWord().toString()));
    }
}
