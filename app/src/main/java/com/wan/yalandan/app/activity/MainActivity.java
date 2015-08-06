package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.data.DataStore;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.DictionaryReader;
import com.wan.yalandan.app.util.DownloadFileProcess;
import com.wan.yalandan.app.util.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private final static int NUMBER_OF_OPTIONS = 4;

    private List<RadioButton> radioButtons = new ArrayList<>(NUMBER_OF_OPTIONS);
    private ArrayList<Word> words = new ArrayList<>(NUMBER_OF_OPTIONS);
    private int indexOfCorrectAnswer;
    private ProgressBar progressBar;
    private Button btnAnswer;
    private TextView tv;
    private DictionaryReader dr;
    private DownloadFileProcess downloader;
    private XmlParser parser;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        ColorDrawable[] correctAnswer = {new ColorDrawable(Color.argb(255, 150, 250, 150)), new ColorDrawable(Color.WHITE)};
        ColorDrawable[] incorrectAnswer = {new ColorDrawable(Color.argb(255, 250, 150, 150)), new ColorDrawable(Color.WHITE)};
        TransitionDrawable incorrectAnswerTrans = new TransitionDrawable(incorrectAnswer);
        TransitionDrawable correctAnswerTrans = new TransitionDrawable(correctAnswer);
        btnAnswer.setOnClickListener(v -> {
            if (!radioButtons.get(indexOfCorrectAnswer).isChecked()) {
                relativeLayout.setBackground(incorrectAnswerTrans);
                incorrectAnswerTrans.startTransition(1000);
                return;
            }
            relativeLayout.setBackground(correctAnswerTrans);
            correctAnswerTrans.startTransition(1500);
            onClickStartDownload();
        });
        onClickStartDownload();
    }

    private void onClickStartDownload() {
        btnAnswer.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        indexOfCorrectAnswer = Math.abs(new Random().nextInt() % NUMBER_OF_OPTIONS);
        List<String> randomWords = dr.getRandomWords(NUMBER_OF_OPTIONS);
        for (String word : randomWords) {
            Log.d("TEST", "Request word");
            downloader.getWordUriFromApi(word);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(downloader.receiver);
    }

    public void init() {
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        dr = new DictionaryReader(R.raw.american_english, this);
        parser = new XmlParser();
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");
        DownloadFileProcess.ICallbackUri fileDownloadedCallback = new DownloadFileProcess.ICallbackUri() {
            @Override
            public void onSuccess(String uri) {
                Word result = parser.getWordData(uri);
                if (result == null) {
                    requestNewWord(null);
                } else {
                    if (words.contains(result)) {
                        requestNewWord(null);
                    } else {
                        words.add(result);
                        if (words.size() == NUMBER_OF_OPTIONS) {
                            updateUI();
                            words.clear();
                        }
                    }
                }
            }

            @Override
            public void onFail(String word) {
                //if (words.contains())
                Log.d("TEST", "FAIL DOWNLOAD > " + word);
                requestNewWord(null);
            }
        };
        downloader = new DownloadFileProcess(fileDownloadedCallback, getBaseContext(), DataStore.ListName.GENERAL);
        radioButtons.add((RadioButton) findViewById(R.id.radioButton));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton2));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton3));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton4));
    }

    private void requestNewWord(String word) {
        if (word == null) {
            word = dr.getRandomWords(1).get(0);
        }
        Log.d("TEST", "Request New word");
        downloader.getWordUriFromApi(word);
    }

    private void updateUI() {
        for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
            radioButtons.get(i).setText(words.get(i).getMeaningCore());
        }
        btnAnswer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        tv.setText(words.get(indexOfCorrectAnswer).getHeadWord());
        ((RadioGroup) findViewById(R.id.radioGroup)).clearCheck();
    }

}

