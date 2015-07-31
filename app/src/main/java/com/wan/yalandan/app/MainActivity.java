package com.wan.yalandan.app;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

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

        DatabaseAdapter dbAdapter = new DatabaseAdapter(getBaseContext());
        Button btn = (Button) findViewById(R.id.btnAnswer);
        Button btnAddRow = (Button) findViewById(R.id.btnAdd);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAddRow.setOnClickListener(v -> {
            String[] values = editTextAdd.getText().toString().split(",");
            dbAdapter.insertWord(values[0], values[1]);
        });
        btnSearch.setOnClickListener(v -> {
            List<Uri> uris = dbAdapter.getUri(editTextSearch.getText().toString());
            StringBuilder sB = new StringBuilder();
            if (uris == null) return;
            for (Uri u : uris)
                sB.append(u.getPath() + "\n");
            tv.setText(sB.toString());
        });
        //----TEST END-------------

    }
}
