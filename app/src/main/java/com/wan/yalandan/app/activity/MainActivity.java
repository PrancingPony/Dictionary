package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.wan.yalandan.app.DictionaryReader;
import com.wan.yalandan.app.DownloadFileProcess;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    static List<RadioButton> radioButtons = new ArrayList<>(4);
    static ArrayList<Object> uriList = new ArrayList<>(4);
    static List<String> randomWords = new ArrayList<>(4);
    static int currentTrue;
    static ProgressBar progressBar;
    static Button btnAnswer;
    public  static boolean isDone = false;
    private static int requestCounter = 0;
    private static int requestfinished = 0;
    public DictionaryReader dr;
    TextView tv;

    public void request(String uri,String word) {
        XmlParser parser = new XmlParser(getApplicationContext());
        Word wordModel = parser.getWordData(uri);
        if (wordModel == null && !isDone) {
            requestCounter--;
            getUri();
        } else {
            if(randomWords.contains(word))return;
            uriList.add(uri);
            randomWords.add(word);
            requestCounter--;
            requestfinished++;
            Log.d("---------Word:-> ",word);

            if (uriList.size() == 4) {
                for (RadioButton radioButton : radioButtons) {
                    parser = new XmlParser(getApplicationContext());
                    wordModel = parser.getWordData(uriList.get(radioButtons.indexOf(radioButton)).toString());
                    radioButton.setText(wordModel.getMeaningCore());
                }
                isDone = true;
                btnAnswer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                tv.setText(randomWords.get(currentTrue));
                Log.d("Request:-> ", "<<----------DONE--------->>");

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnAnswer.setOnClickListener(v -> {

            btnAnswer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            currentTrue = Math.abs(new Random().nextInt() % 4);
            requestCounter = 0;
            requestfinished = 0;
            isDone = false;
            uriList.clear();
            randomWords.clear();
            for (int i =0;i<4;i++) {
                getUri();
            }

            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.runOnUiThread(() -> {
                    if (!isDone) {
                        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                        uriList.clear();
                    }
                    btnAnswer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }).start();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void init() {
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dr = new DictionaryReader(R.raw.american_english, this.getBaseContext());
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");

        radioButtons.add((RadioButton) findViewById(R.id.radioButton));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton2));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton3));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton4));

    }

    public void getUri() {

        if (!isDone && requestCounter<=4-requestfinished) {
            requestCounter++;
            String randomWord = null;
            do {

                randomWord  = dr.getRandomWords(1).get(0);
            }
            while (randomWords.contains(randomWord));

            final String finalRandomWord = randomWord;
            DownloadFileProcess dfp = new DownloadFileProcess(uri -> request(uri, finalRandomWord), getBaseContext());
            dfp.getWordUriFromApi(randomWord);
        }
    }

}

