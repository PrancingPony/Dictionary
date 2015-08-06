package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.wan.yalandan.app.R;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        findViewById(R.id.btnQuestionPool).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btnMyWords).setOnClickListener(v -> startActivity(new Intent(this, MyWordsActivity.class)));
        findViewById(R.id.btnAddWords).setOnClickListener(v -> startActivity(new Intent(this, AddWordsActivity.class)));
    }
}
