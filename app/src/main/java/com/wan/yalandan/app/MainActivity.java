package com.wan.yalandan.app;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    static List<RadioButton> radioButtons = new ArrayList<>(4);
    static ArrayList<Object> uriList = new ArrayList<>(4);
    DownloadFileProcess dfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //----TEST CODE------------
        DictionaryReader dr = new DictionaryReader(R.raw.american_english, getBaseContext());
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");
        final Button btnAnswer = (Button) findViewById(R.id.btnAnswer);
        final TextView tv = (TextView) findViewById(R.id.textView);

        dfp = new DownloadFileProcess(uri -> {
            Log.d("CALLED URI", uri);
        }, getBaseContext()
        );

        btnAnswer = (Button) findViewById(R.id.btnAnswer);

        btnAnswer.setOnClickListener(v -> {

            dfp.getWordUriFromApi("hi");
        //-----------------------------
        radioButtons.add((RadioButton) findViewById(R.id.radioButton));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton2));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton3));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton4));
        //-----------------------------

        List<String> randomWords = dr.getRandomWords(4);
        Random random = new Random();
        int wordNum = random.nextInt() % 4;
        randomWords = dr.getRandomWords(4);
        final List<String> finalRandomWords = randomWords;

        btnAnswer.setOnClickListener(v -> {
            uriList.clear();
            dfp = new DownloadFileProcess(
                    uri -> {
                        uriList.add(uri);
                        if(uriList.size()==4) {
                            for (RadioButton radioButton : radioButtons) {
                                radioButton.setText(uriList.get(radioButtons.indexOf(radioButton)).toString());
                            }
                        }
                        Log.d("URILIST", String.valueOf(uriList.size()));
                    }
                    , getBaseContext());
            for (String word : finalRandomWords) {
                dfp.getWordUriFromApi(word);
            }

        });
//

        //----TEST END-------------
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
    }


}

