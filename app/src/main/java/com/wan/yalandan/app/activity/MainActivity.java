package com.wan.yalandan.app.activity;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {
    private final static int NUMBER_OF_OPTIONS = 4;

    private List<RadioButton> radioButtons = new ArrayList<>(NUMBER_OF_OPTIONS);
    private ArrayList<Word> words = new ArrayList<>(NUMBER_OF_OPTIONS);
    private int indexOfCorrectAnswer;
    private ProgressBar progressBar;
    private FloatingActionButton  btnAnswer;
    private TextView tv;
    private DictionaryReader dr;
    private DownloadFileProcess downloader;
    private XmlParser parser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        ObjectAnimator animatorCorrect = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.animator_correct);
        ObjectAnimator animatorIncorrect = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.animator_incorrect);
        animatorIncorrect.setTarget(tv);
        animatorIncorrect.setEvaluator(new ArgbEvaluator());
        animatorCorrect.setTarget(tv);
        animatorCorrect.setEvaluator(new ArgbEvaluator());
        btnAnswer.setOnClickListener(v -> {
            if (!radioButtons.get(indexOfCorrectAnswer).isChecked()) {
                animatorIncorrect.start();
                return;
            }
            animatorCorrect.start();
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
        btnAnswer = (FloatingActionButton) findViewById(R.id.btnAnswer);
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dr = new DictionaryReader(R.raw.american_english, this);
        parser = new XmlParser();
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

