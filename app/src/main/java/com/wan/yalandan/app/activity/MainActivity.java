package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.wan.yalandan.app.DictionaryReader;
import com.wan.yalandan.app.DownloadFileProcess;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";

    Button btnAnswer;
    TextView tv;
    DownloadFileProcess dfp;

    static List<RadioButton> radioButtons = new ArrayList<>(4);
    static ArrayList<Object> uriList = new ArrayList<>(4);
    static ProgressBar progressBar;
    static long currenttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        final DictionaryReader dr = new DictionaryReader(R.raw.american_english, getBaseContext());
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        final List[] randomWords = new List[]{dr.getRandomWords(4)};
        Random random = new Random();
        int wordNum = Math.abs(random.nextInt() % 4);
        randomWords[0] = dr.getRandomWords(4);

        dfp = new DownloadFileProcess(uri -> {
            btnAnswer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            randomWords[0] = dr.getRandomWords(4);
            tv.setText(randomWords[0].toString());
            currenttime = System.currentTimeMillis();
            for (Object word : randomWords[0]) {
            uriList.add(uri);
                        if (uriList.size() == 4) {
                for (RadioButton radioButton : radioButtons) {

                                XmlParser parser = new XmlParser(getApplicationContext());
                                Word wordModel = parser.getWordData(uriList.get(radioButtons.indexOf(radioButton)).toString());
                                radioButton.setText(wordModel.getMeaningCore());
                }
                            tv.setText(randomWords[0].get(wordNum).toString());
                            uriList.clear();
                            progressBar.setVisibility(View.INVISIBLE);
                            btnAnswer.setVisibility(View.VISIBLE);
            }
        }, getBaseContext()
        );

        btnAnswer.setOnClickListener(v -> {
            uriList.clear();
                dfp.getWordUriFromApi((String) word);
            }
            new Thread(() -> {
                while (System.currentTimeMillis() - currenttime < 5000) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.runOnUiThread(() -> {
                    uriList.clear();
                    btnAnswer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
    }

    public void init() {
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        tv = (TextView) findViewById(R.id.textView);

        radioButtons.add((RadioButton) findViewById(R.id.radioButton));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton2));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton3));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton4));
    }
}

