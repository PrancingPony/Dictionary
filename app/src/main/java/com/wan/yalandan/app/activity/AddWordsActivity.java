package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.data.DataStore;
import com.wan.yalandan.app.util.DictionaryReader;

public class AddWordsActivity extends Activity {
    private Button btnAddWord;
    private DictionaryReader dr;
    private DataStore dataStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwords);
        init();

        btnAddWord.setOnClickListener(v ->
        {

        });
    }

    private void init() {
        btnAddWord = (Button) findViewById(R.id.btnAddWord);
        dr = new DictionaryReader(R.raw.american_english, this);
        dataStore = new DataStore(this);
        //DownloadFileProcess.ICallbackUri fileDownloadedCallback = uri ->
       // };
    }
}
