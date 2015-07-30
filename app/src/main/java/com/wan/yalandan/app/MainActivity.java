package com.wan.yalandan.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

        //----TEST CODE------------
        final TextView tv = (TextView) findViewById(R.id.textView);
        final EditText editTextAdd = (EditText) findViewById(R.id.editTextAddRow);
        final EditText editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        DatabaseAdaptor dbAdaptor = new DatabaseAdaptor(getBaseContext());
        Button btn = (Button) findViewById(R.id.btnAnswer);
        Button btnAddRow = (Button) findViewById(R.id.btnAdd);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);

        btnAddRow.setOnClickListener(v -> {
            String[] values = editTextAdd.getText().toString().split(",");
            dbAdaptor.insertWord(values[0], values[1]);
        });
        btnSearch.setOnClickListener(v -> {
            tv.setText(dbAdaptor.getUri(editTextSearch.getText().toString()));
        });
        //----TEST END-------------

    }
}
